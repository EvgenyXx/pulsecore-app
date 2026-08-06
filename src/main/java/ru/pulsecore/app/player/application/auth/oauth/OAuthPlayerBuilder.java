package ru.pulsecore.app.player.application.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.auth.RegistrationValidator;
import ru.pulsecore.app.player.infrastructure.persistence.entity.Player;
import ru.pulsecore.app.player.infrastructure.persistence.entity.Role;
import ru.pulsecore.app.player.application.player.PlayerService;
import ru.pulsecore.app.player.application.role.RoleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class OAuthPlayerBuilder {

    private final PlayerService playerService;
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
                // Игнорируем невалидный формат даты от OAuth-провайдера
            }
        }
        player.getRoles().add(userRole);
        return playerService.save(player);
    }
}