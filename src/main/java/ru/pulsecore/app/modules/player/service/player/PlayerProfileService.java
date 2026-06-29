package ru.pulsecore.app.modules.player.service.player;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.auth.api.dto.ChangePasswordRequest;
import ru.pulsecore.app.modules.auth.api.dto.UpdateProfileRequest;
import ru.pulsecore.app.modules.player.api.dto.player.PlayerProfileResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.exception.EmailAlreadyExistsException;
import ru.pulsecore.app.modules.player.exception.OldPasswordMismatchException;
import ru.pulsecore.app.modules.player.exception.SamePasswordException;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;

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