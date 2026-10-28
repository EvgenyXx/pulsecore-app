package ru.pulsecore.app.tournament.application.mapping.helper;

import ru.pulsecore.app.tournament.domain.entity.TopPlayersView;

import java.util.List;
import java.util.UUID;

public final class TopPlayersViewMapperHelper {

    private TopPlayersViewMapperHelper() {}

    public static int findPlayerPosition(List<TopPlayersView> players, UUID playerId) {
        if (players == null || playerId == null) return 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerId().equals(playerId)) {
                return i + 1;
            }
        }
        return 0;
    }

    public static int findPlayerTournaments(List<TopPlayersView> players, UUID playerId) {
        if (players == null || playerId == null) return 0;
        for (TopPlayersView view : players) {
            if (view.getPlayerId().equals(playerId)) {
                return view.getTournaments() != null ? view.getTournaments().intValue() : 0;
            }
        }
        return 0;
    }
}