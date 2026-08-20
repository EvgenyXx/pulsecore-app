package ru.pulsecore.app.player.application.profile;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.api.dto.response.PlayerProfileResponse;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException;


import java.util.UUID;

/**
 * Сервис по управлению
 * игрока обновляет поля пользователя
 * пока что только почту
 */
@Service
@RequiredArgsConstructor
public class UpdatePlayerService {

    private final PlayerCommandService playerCommandService;
    private final PlayerSearchService playerSearchService;


    @Transactional
    public PlayerProfileResponse updateProfile(UUID id, String email) {
        Player player = playerSearchService.getById(id);

        if (!email.equals(player.getEmail()) && playerSearchService.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        player.setEmail(email);
        playerCommandService.save(player);

        return PlayerProfileResponse.builder()
                .id(player.getId().toString())
                .name(player.getName())
                .email(player.getEmail())
                .createdAt(player.getCreatedAt())
                .build();
    }


}