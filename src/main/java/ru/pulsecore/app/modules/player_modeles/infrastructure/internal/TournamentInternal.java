package ru.pulsecore.app.modules.player_modeles.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player_modeles.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.modules.shared.dto.NotificationInfo;
import ru.pulsecore.app.modules.shared.dto.PlayerData;
import ru.pulsecore.app.modules.shared.exception.PlayerNotFoundException;
import ru.pulsecore.app.modules.tournament_module.infrastructure.client.PlayerClient;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentInternal implements PlayerClient {

    private final PlayerRepository playerRepository;

    @Override
    public PlayerData getPlayerById(UUID playerId) {
        log.info("getPlayerById {}", playerId);
        return playerRepository.findProjectionById(playerId)
                .map(p ->
                        new PlayerData(p.getId(), p.getName(), p.getEmail()))
                .orElseThrow(()-> new PlayerNotFoundException(playerId.toString()));
    }


    @Override
    public List<PlayerData> searchByName(String query) {
        return playerRepository.searchByName(query)
                .stream()
                .map(p->
                        new PlayerData(p.getId(),p.getName(),p.getEmail()))
                .toList();//todo добавить проекцию
    }

    @Override
    public PlayerData findByName(String fullName) {
        return playerRepository.findByNameIgnoreCase(fullName)
                .map(p -> new PlayerData(p.getId(), p.getName(), p.getEmail()))
                .orElseThrow(()-> new PlayerNotFoundException(fullName));
    }

    @Override
    public NotificationInfo getNotificationInfo(UUID playerId) {
        var player = playerRepository.findById(playerId).orElseThrow(()
        -> new PlayerNotFoundException(String.valueOf(playerId)));
        return new NotificationInfo(
                player.isNotificationsEnabled() && player.hasActiveSubscription(),
                player.isPushEnabled() && player.hasActiveSubscription()
        );
    }
}
