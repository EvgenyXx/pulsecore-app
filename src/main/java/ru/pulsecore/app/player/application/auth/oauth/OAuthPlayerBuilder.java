package ru.pulsecore.app.player.application.auth.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.auth.RegistrationValidator;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Role;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.role.RoleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthPlayerBuilder {

    private final PlayerCommandService playerCommandService;
    private final RoleService roleService;
    private final RegistrationValidator registrationValidator;

    public Player build(String name, String email, OAuthSessionExtractor.OAuthSessionData sessionData) {
        registrationValidator.validate(email, name);
        Role userRole = roleService.findRoleUser();

        Player player = Player.builder()
                .name(name).email(email)
                .oauthProvider(sessionData.provider()).oauthId(sessionData.oauthId())
                .verified(true).password("")
                .phone(sessionData.phone()).avatarUrl(sessionData.avatar()).gender(sessionData.gender())
                .roles(new HashSet<>()).createdAt(LocalDateTime.now())
                .build();

        String birthday = sessionData.birthday();
        if (birthday != null) {
            try {
                player.setBirthday(LocalDate.parse(birthday));
            } catch (DateTimeParseException e) {
                log.error("Пришел не валидный формат от OAuth: {}",e.getMessage());
                // Игнорируем невалидный формат даты от OAuth-провайдера
            }
        }
        player.getRoles().add(userRole);
        return playerCommandService.save(player);
    }
}