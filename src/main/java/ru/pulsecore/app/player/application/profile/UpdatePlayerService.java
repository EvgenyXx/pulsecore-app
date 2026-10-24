package ru.pulsecore.app.player.application.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.api.dto.response.PlayerProfileResponse;
import ru.pulsecore.app.player.application.mapper.PlayerMapper;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException;
import ru.pulsecore.app.player.infrastructure.repository.PlayerRepository;
import java.util.Locale;
import java.util.UUID;

/**
 * Сервис по управлению
 * игрока обновляет поля пользователя
 * пока что только почту
 */
@Service
@RequiredArgsConstructor
public class UpdatePlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerSearchService playerSearchService;
    private final PlayerMapper playerMapper;


    @Transactional
    public PlayerProfileResponse updateProfile(UUID id, String email) {
        String normalizeEmail = email.trim().toLowerCase(Locale.ROOT);
        Player player = playerSearchService.getById(id);

        if (!normalizeEmail.equals(player.getEmail()) && playerSearchService.existsByEmail(normalizeEmail)) {
            throw new EmailAlreadyExistsException();
        }
        player.setEmail(normalizeEmail);
        playerRepository.save(player);

        return playerMapper.toProfileResponse(player);
    }


}