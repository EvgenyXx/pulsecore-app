// ==================== OAuthFinishService.java ====================
package ru.pulsecore.app.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.auth.api.PlayerLoginService;
import ru.pulsecore.app.modules.auth.api.dto.OAuthFinishRequest;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.domain.Role;
import ru.pulsecore.app.modules.player.domain.Subscription;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.player.repository.SubscriptionRepository;
import ru.pulsecore.app.modules.player.service.role.RoleService;
import ru.pulsecore.app.modules.tournament.service.TournamentAutoAddService;
import ru.pulsecore.app.modules.tournament.service.TournamentCascadeSyncService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class OAuthFinishService {

    private static final int TRIAL_DAYS = 7;
    private static final int RECENT_DAYS = 30;
    private static final String[] SESSION_KEYS = {
            "oauth_email", "oauth_provider", "oauth_id",
            "oauth_phone", "oauth_avatar", "oauth_birthday", "oauth_gender"
    };

    private final PlayerRepository playerRepository;
    private final RoleService roleService;
    private final SubscriptionRepository subscriptionRepository;
    private final TournamentAutoAddService tournamentAutoAddService;
    private final TournamentCascadeSyncService cascadeSyncService;
    private final PlayerLoginService playerLoginService;

    @Transactional
    public void complete(OAuthFinishRequest request, HttpServletRequest httpRequest,
                         HttpServletResponse httpResponse) throws IOException {
        HttpSession session = httpRequest.getSession();

        Player player = createPlayer(request, session);
        activateTrial(player);

        // Синхронно: последние 30 дней
        tournamentAutoAddService.addRecentTournamentsForPlayer(player, RECENT_DAYS);

        // Асинхронно: вся история до 2025 года
        cascadeSyncService.syncAllHistory(player);

        clearSession(session);
        playerLoginService.login(player, httpRequest, httpResponse);
    }

    private Player createPlayer(OAuthFinishRequest request, HttpSession session) {
        String name = (request.getLastName() + " " + request.getFirstName()).toLowerCase().trim();
        Role userRole = roleService.findRoleUser();

        Player player = Player.builder()
                .name(name)
                .email(sessionAttr(session, "oauth_email"))
                .oauthProvider(sessionAttr(session, "oauth_provider"))
                .oauthId(sessionAttr(session, "oauth_id"))
                .verified(true)
                .password("")
                .phone(sessionAttr(session, "oauth_phone"))
                .avatarUrl(sessionAttr(session, "oauth_avatar"))
                .gender(sessionAttr(session, "oauth_gender"))
                .roles(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();

        String birthday = sessionAttr(session, "oauth_birthday");
        if (birthday != null) {
            try { player.setBirthday(LocalDate.parse(birthday)); } catch (Exception ignored) {}
        }

        player.getRoles().add(userRole);
        return playerRepository.save(player);
    }

    private void activateTrial(Player player) {
        Subscription trial = Subscription.builder().player(player).build();
        trial.activate(TRIAL_DAYS);
        subscriptionRepository.save(trial);
    }

    private void clearSession(HttpSession session) {
        for (String key : SESSION_KEYS) {
            session.removeAttribute(key);
        }
    }

    private String sessionAttr(HttpSession session, String name) {
        return (String) session.getAttribute(name);
    }
}