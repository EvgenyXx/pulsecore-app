package ru.pulsecore.app.tournament.application.roster.change.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerIdentity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerRemovalDetector {

    /**
     * Находит игроков, которые были в старом составе, но исчезли из нового.
     * Сравнение происходит по строгому совпадению через PlayerIdentity.
     */
    public List<String> findRemovedNames(List<PlayerData> oldPlayers, TournamentDto newTournament) {
        if (newTournament.getPlayers() == null) return List.of();

        Set<String> newCleaned = newTournament.getPlayers().stream()
                .map(PlayerIdentity::cleanName)
                .collect(Collectors.toSet());

        List<String> removed = oldPlayers.stream()
                .map(PlayerData::name)
                .filter(name -> !newCleaned.contains(PlayerIdentity.cleanName(name)))
                .toList();

        if (!removed.isEmpty()) {
            log.debug("Удаление: удалены={}", removed);
        }

        return removed;
    }

    /**
     * Находит полные объекты PlayerData для списка имен удаленных игроков.
     * Фильтрует общий список игроков, оставляя только тех, кто есть в removedNames.
     */
    public List<PlayerData> findPlayerForReplace(List<String> removedNames, List<PlayerData> allPlayers) {
        Set<String> removedCleaned = removedNames.stream()
                .map(PlayerIdentity::cleanName)
                .collect(Collectors.toSet());

        List<PlayerData> removedPlayers = allPlayers.stream()
                .filter(player -> removedCleaned.contains(PlayerIdentity.cleanName(player.name())))
                .toList();

        log.debug("Удаление: найдены PlayerData для игроков={}",
                removedPlayers.stream().map(PlayerData::name).toList());

        return removedPlayers;
    }

    /**
     * Находит игроков, которые есть в новом составе, но отсутствуют в старом.
     * Сравнение происходит по строгому совпадению через PlayerIdentity.
     */
    public List<String> findAddedNames(List<PlayerData> oldPlayers, TournamentDto newTournament) {
        if (newTournament.getPlayers() == null) return List.of();

        Set<String> oldCleaned = oldPlayers.stream()
                .map(player -> PlayerIdentity.cleanName(player.name()))
                .collect(Collectors.toSet());

        return newTournament.getPlayers().stream()
                .filter(name -> !oldCleaned.contains(PlayerIdentity.cleanName(name)))
                .toList();
    }
}