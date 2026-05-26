package ru.pulsecore.app.modules.player.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.modules.player.api.dto.BestTimeResponse;
import ru.pulsecore.app.modules.shared.service.SqlReader;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PlayerAnalyticsRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SqlReader sqlReader;

    public List<BestTimeResponse> getBestTime(UUID playerId, LocalDate start, LocalDate end) {
        String sql = sqlReader.read("sql/analytics/best_time.sql");

        var params = new MapSqlParameterSource()
                .addValue("playerId", playerId)
                .addValue("start", start)
                .addValue("end", end);

        RowMapper<BestTimeResponse> mapper = (rs, rowNum) -> BestTimeResponse.builder()
                .time(rs.getString("time"))
                .gamesCount(rs.getLong("games_count"))
                .avgPoints(rs.getDouble("avg_points"))
                .totalPoints(rs.getDouble("total_points"))
                .build();

        return jdbc.query(sql, params, mapper);
    }
}