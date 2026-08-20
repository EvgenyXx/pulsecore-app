package ru.pulsecore.app.player.application.player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Role;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис по управлению игроком внутреннего пользования
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerCommandService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;



    @Transactional(propagation = Propagation.MANDATORY)
    public void deletePlayer(UUID playerId) {
        playerRepository.deleteById(playerId);
    }


    @Transactional(propagation = Propagation.MANDATORY)
    public Player save(Player player) {
        if (player.getName() != null) {
            player.setName(NameNormalizer.normalize(player.getName()));
        }
        return playerRepository.save(player);
    }

    @Transactional(propagation = Propagation.MANDATORY)
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