package ru.pulsecore.app.tournament.infrastructure.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.TournamentDto;

import ru.pulsecore.app.tournament.infrastructure.gate.MastersApiGate;
import ru.pulsecore.app.tournament.infrastructure.properties.MastersApiProperties;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;
import ru.pulsecore.app.tournament.infrastructure.circuit.MastersApiCircuitBreaker;


import java.time.Duration;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class MastersApiClient {

    private final MastersApiProperties properties;
    private final ObjectMapper mapper;
    private final MastersApiCircuitBreaker breaker;
    private final MastersApiGate apiGate;


    public List<TournamentDto> loadTournaments(String date) {
        if (breaker.isBlocked()) {
            return List.of();
        }

        return apiGate.execute(() -> doLoadTournaments(date));
    }

    private List<TournamentDto> doLoadTournaments(String date) {
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
                            t.setPlayers(NameNormalizer.normalizePlayers(t.getPlayers()));
                        }
                    }
                }

                return tournaments;

            } catch (Exception e) {
                log.error("Ошибка при запросе к Masters API: {}", e.getMessage(), e);
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