// ==================== PlayerRegistrationService.java ====================
package ru.pulsecore.app.modules.shared.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.domain.Subscription;
import ru.pulsecore.app.modules.player.exception.*;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.player.repository.SubscriptionRepository;
import ru.pulsecore.app.modules.player.service.role.RoleService;
import ru.pulsecore.app.modules.player.service.strategy.MailStrategyRegistry;
import ru.pulsecore.app.modules.player.service.strategy.MailTypes;
import ru.pulsecore.app.modules.shared.properties.AdminProperties;
import ru.pulsecore.app.modules.tournament.service.TournamentAutoAddService;
import ru.pulsecore.app.modules.tournament.service.TournamentCascadeSyncService;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlayerRegistrationService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailStrategyRegistry mailStrategyRegistry;
    private final SubscriptionRepository subscriptionRepository;
    private final TournamentAutoAddService tournamentAutoAddService;
    private final TournamentCascadeSyncService cascadeSyncService;
    private final RoleService roleService;
    private final AdminProperties adminProperties;
    private final Parser uaParser;

    public record Pending(String name, String email, String password, String code) implements Serializable {}

    public Pending initiate(String name, String email, String rawPassword) {
        String normalizedEmail = email.toLowerCase().trim();
        String normalizedName = name.toLowerCase().trim();

        checkEmailNotTakenByLocal(normalizedEmail);
        checkNameUnique(normalizedName);

        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        String encodedPassword = passwordEncoder.encode(rawPassword);
        mailStrategyRegistry.send(MailTypes.VERIFICATION, normalizedEmail, code);

        return new Pending(normalizedName, normalizedEmail, encodedPassword, code);
    }

    private void checkEmailNotTakenByLocal(String normalizedEmail) {
        playerRepository.findByEmail(normalizedEmail).ifPresent(player -> {
            if (player.getPassword() == null || player.getPassword().isBlank()) {
                throw new OAuthOnlyLoginException(player.getOauthProvider());
            }
            throw new EmailAlreadyExistsException();
        });
    }

    private void checkNameUnique(String normalizedName) {
        if (playerRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new PlayerNameAlreadyExistsException();
        }
    }

    @Transactional
    public Player complete(Pending pending, String code, HttpServletRequest request) {
        if (!pending.code().equals(code)) throw new BadCredentialsException();

        var defaultRole = roleService.findRoleUser();
        Player player = playerRepository.save(Player.builder()
                .name(pending.name()).email(pending.email()).password(pending.password())
                .verified(true).createdAt(LocalDateTime.now())
                .roles(Set.of(defaultRole))
                .build());

        Subscription trial = Subscription.builder().player(player).build();
        trial.activate(7);
        subscriptionRepository.save(trial);

        // Синхронно: последние 30 дней
        tournamentAutoAddService.addRecentTournamentsForPlayer(player, 30);

        // Асинхронно: вся история до 2025 года
        cascadeSyncService.syncAllHistory(player);

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "Неизвестно";
        Client client = uaParser.parse(userAgent);
        String device = client.device.family;
        String os = client.os.family;
        String browser = client.userAgent.family;

        mailStrategyRegistry.send(MailTypes.ADMIN_NEW_USER,
                adminProperties.getEmail(),
                player.getName(), player.getEmail(), ip, userAgent,
                device, os, browser);

        mailStrategyRegistry.send(MailTypes.WELCOME, player.getEmail(), player.getName());

        return player;
    }
}