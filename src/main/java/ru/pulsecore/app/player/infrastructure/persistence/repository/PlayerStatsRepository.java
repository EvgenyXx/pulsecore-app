package ru.pulsecore.app.player.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.player.infrastructure.persistence.repository.projection.PlayerPageStatsProjection;
import ru.pulsecore.app.shared.infrastructure.SqlReader;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerStatsRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final SqlReader sqlReader;

    public List<PlayerPageStatsProjection> getPlayerStats(Instant since) {
        String sql = sqlReader.read("sql/analytics/player_page_stats.sql");

        var params = new MapSqlParameterSource()
                .addValue("since", java.sql.Timestamp.from(since));

        RowMapper<PlayerPageStatsProjection> mapper = (rs, rowNum) -> PlayerPageStatsProjection.builder()
                .name(rs.getString("name"))
                .paths(rs.getString("paths"))
                .total(rs.getLong("total"))
                .percent(rs.getDouble("percent"))
                .build();

        return jdbc.query(sql, params, mapper);
    }
}