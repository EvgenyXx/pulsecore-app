package ru.pulsecore.app.player.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Role;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PlayerFactory {

    private final PasswordEncoder passwordEncoder;
    private final PlayerRepository playerRepository;

    public Player create(String name, String email, String rawPassword, Role defaultRole) {
        return playerRepository.save(Player.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .verified(true)
                .createdAt(LocalDateTime.now())
                .roles(Set.of(defaultRole))
                .build());
    }
}