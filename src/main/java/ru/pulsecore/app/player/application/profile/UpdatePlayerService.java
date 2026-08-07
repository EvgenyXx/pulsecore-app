package ru.pulsecore.app.player.application.profile;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.PlayerProfileResponse;
import ru.pulsecore.app.player.application.player.PlayerService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdatePlayerService {

    private final PlayerService playerService;



    public PlayerProfileResponse updateProfile(UUID id, String email) {
        Player player = playerService.getById(id);

        if (!email.equals(player.getEmail()) && playerService.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        player.setEmail(email);
        playerService.save(player);

        return PlayerProfileResponse.builder()
                .id(player.getId().toString())
                .name(player.getName())
                .email(player.getEmail())
                .createdAt(player.getCreatedAt())
                .build();
    }


}