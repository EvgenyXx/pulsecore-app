package ru.pulsecore.app.tournament.application.roster.change.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerRemovalDetector {

    public List<String> findRemovedNames(List<PlayerData> oldPlayers, TournamentDto newTournament) {
        List<String> oldNames = oldPlayers.stream()
                .map(PlayerData::playerName)
                .map(NameNormalizer::normalizeForSearch)
                .toList();

        List<String> newNames = newTournament.getPlayers().stream()
                .map(NameNormalizer::normalizeForSearch)
                .toList();

        List<String> removed = oldNames.stream()
                .filter(name -> !newNames.contains(name))
                .toList();

        if (!removed.isEmpty()) {
            log.debug("Удаление: старый состав={}, новый состав={}, удалены={}",
                    oldNames, newNames, removed);
        }

        return removed;
    }

    public List<PlayerData> findPlayerForReplace(List<String> removedNames, List<PlayerData> allPlayers) {
        Set<String> normalizedRemoved = removedNames.stream()
                .map(NameNormalizer::normalizeForSearch)
                .collect(Collectors.toSet());

        List<PlayerData> removedPlayers = allPlayers.stream()
                .filter(player -> normalizedRemoved.contains(
                        NameNormalizer.normalizeForSearch(player.playerName())))
                .toList();

        log.debug("Удаление: найдены PlayerData для игроков={}",
                removedPlayers.stream().map(PlayerData::playerName).toList());

        return removedPlayers;
    }
}