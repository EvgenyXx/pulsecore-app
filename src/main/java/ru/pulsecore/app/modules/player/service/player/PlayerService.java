package ru.pulsecore.app.modules.player.service.player;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.auth.api.dto.ChangePasswordRequest;
import ru.pulsecore.app.modules.auth.api.dto.UpdateProfileRequest;
import ru.pulsecore.app.modules.player.api.dto.PlayerProfileResponse;
import ru.pulsecore.app.modules.player.api.dto.PlayerResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.exception.EmailAlreadyExistsException;
import ru.pulsecore.app.modules.player.exception.OldPasswordMismatchException;
import ru.pulsecore.app.modules.player.exception.SamePasswordException;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.shared.exception.PlayerNotFoundException;
import ru.pulsecore.app.modules.shared.service.NameNormalizer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final NameNormalizer nameNormalizer;  // 🔥 ДОБАВЛЕНО

    public boolean isNotificationsEnabled(UUID id) {
        return getById(id).isNotificationsEnabled();
    }

    @Transactional
    public void setNotificationsEnabled(UUID id, boolean enabled) {
        Player player = getById(id);
        player.setNotificationsEnabled(enabled);
        playerRepository.save(player);
    }

    public Player getById(UUID id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id.toString()));
    }

    public Player findById(UUID id) {
        return playerRepository.findById(id).orElse(null);
    }

    public List<Player> getAll() {
        return playerRepository.findAll();
    }

    public PlayerProfileResponse updateProfile(UUID id, UpdateProfileRequest request) {
        Player player = getById(id);

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
        Player player = getById(id);
        if (!passwordEncoder.matches(rawPassword, player.getPassword())) {
            throw new OldPasswordMismatchException();
        }
    }

    public Player getByName(String name) {
        // 🔥 НОРМАЛИЗУЕМ ИМЯ ПЕРЕД ПОИСКОМ
        String normalizedName = nameNormalizer.normalize(name);
        return playerRepository.findByNameIgnoreCase(normalizedName)
                .orElseThrow(() -> new PlayerNotFoundException(name));
    }

    public void changePassword(UUID id, ChangePasswordRequest request) {
        Player player = getById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), player.getPassword())) {
            throw new OldPasswordMismatchException();
        }
        if (passwordEncoder.matches(request.getNewPassword(), player.getPassword())) {
            throw new SamePasswordException();
        }

        player.setPassword(passwordEncoder.encode(request.getNewPassword()));
        playerRepository.save(player);
    }

    public List<PlayerResponse> searchPlayers(String q) {
        // 🔥 НОРМАЛИЗУЕМ ПОИСКОВЫЙ ЗАПРОС
        String normalizedQuery = nameNormalizer.normalizeForSearch(q);
        return playerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(normalizedQuery, q)
                .stream()
                .map(p -> PlayerResponse.builder()
                        .id(p.getId().toString())
                        .name(p.getName())
                        .email(p.getEmail())
                        .build())
                .toList();
    }

    @Transactional
    public void deletePlayer(UUID id){
        playerRepository.deleteById(id);
    }

    public Optional<Player> findByEmail(String email) {
        return playerRepository.findByEmail(email);
    }

    @Transactional
    public Player save(Player player){
        // 🔥 НОРМАЛИЗУЕМ ИМЯ ПЕРЕД СОХРАНЕНИЕМ
        if (player.getName() != null) {
            player.setName(nameNormalizer.normalize(player.getName()));
        }
        return playerRepository.save(player);
    }

    @Transactional
    public void saveSelectedHalls(UUID playerId, String halls) {
        Player player = getById(playerId);
        player.setSelectedHalls(halls);
        playerRepository.save(player);
    }

    public String getSelectedHalls(UUID playerId) {
        return getById(playerId).getSelectedHalls();
    }
}