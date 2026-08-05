package ru.pulsecore.app.player.application.player;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.api.dto.request.ChangePasswordRequest;
import ru.pulsecore.app.player.api.dto.request.UpdateProfileRequest;
import ru.pulsecore.app.player.api.dto.response.PlayerProfileResponse;
import ru.pulsecore.app.player.entity.Player;
import ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException;
import ru.pulsecore.app.player.infrastructure.exception.OldPasswordMismatchException;
import ru.pulsecore.app.player.infrastructure.exception.SamePasswordException;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PlayerProfileResponse updateProfile(UUID id, UpdateProfileRequest request) {
        Player player = playerService.getById(id);

        if (!request.getEmail().equals(player.getEmail()) && playerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        player.setEmail(request.getEmail());
        playerRepository.save(player);

        return PlayerProfileResponse.builder()
                .id(player.getId().toString())
                .name(player.getName())
                .email(player.getEmail())
                .createdAt(player.getCreatedAt())
                .build();
    }

    public void verifyPassword(UUID id, String rawPassword) {
        Player player = playerService.getById(id);
        if (!passwordEncoder.matches(rawPassword, player.getPassword())) {
            throw new OldPasswordMismatchException();
        }
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        Player player = playerService.getById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), player.getPassword())) {
            throw new OldPasswordMismatchException();
        }
        if (passwordEncoder.matches(request.getNewPassword(), player.getPassword())) {
            throw new SamePasswordException();
        }

        player.setPassword(passwordEncoder.encode(request.getNewPassword()));
        playerRepository.save(player);
    }
}