package ru.pulsecore.app.modules.tournament_module.infrastructure.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.shared.exception.SiteUnavailableException;
import ru.pulsecore.app.modules.shared.properties.MastersApiProperties;
import ru.pulsecore.app.modules.shared.util.NameNormalizer;
import ru.pulsecore.app.modules.tournament_module.infrastructure.circuit.MastersApiCircuitBreaker;


import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MastersApiClient {

    private final MastersApiProperties properties;
    private final ObjectMapper mapper;
    private final NameNormalizer nameNormalizer;
    private final MastersApiCircuitBreaker breaker;

    public List<TournamentDto> loadTournaments(String date) {
        if (breaker.isBlocked()) {
            throw new SiteUnavailableException();
        }

        for (int i = 1; i <= 2; i++) {
            try {
                Connection connection = Jsoup.connect(properties.getUrl())
                        .method(Connection.Method.valueOf(properties.getMethod()))
                        .header("User-Agent", properties.getUserAgent())
                        .ignoreContentType(true)
                        .timeout(properties.getTimeout());

                connection.data("action", properties.getAction());
                connection.data("country", properties.getCountry());
                if (date != null) connection.data("date", date);

                Connection.Response res = connection.execute();

                if (res.statusCode() != 200 || res.body().trim().startsWith("<")) {
                    breaker.recordFailure();
                    if (i == 2) return List.of();
                    sleep(breaker.backoff());
                    continue;
                }

                List<TournamentDto> tournaments = mapper.readValue(res.body(), new TypeReference<>() {});
                breaker.recordSuccess();

                if (tournaments != null) {
                    for (TournamentDto t : tournaments) {
                        if (t.getPlayers() != null) {
                            t.setPlayers(nameNormalizer.normalizePlayers(t.getPlayers()));
                        }
                    }
                }

                return tournaments;

            } catch (Exception e) {
                breaker.recordFailure();
                if (i == 2) return List.of();
                sleep(breaker.backoff());
            }
        }
        return List.of();
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}