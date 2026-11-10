package ru.pulsecore.app.tournament.application.mapping.helper;

import ru.pulsecore.app.player.api.dto.response.DetailPeriod;

public final class TopPlayerMapperHelper {

    private TopPlayerMapperHelper() {
    }

    public static Integer calculateWinrate(DetailPeriod dp) {
        if (dp == null || dp.matchesCount() == null || dp.matchesCount() == 0) {
            return 0;
        }
        return (int) Math.round(dp.wins() * 100.0 / dp.matchesCount());
    }
}