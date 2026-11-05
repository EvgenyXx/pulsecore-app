package ru.pulsecore.app.tournament.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.circuit.MastersApiCircuitBreaker;
import ru.pulsecore.app.tournament.infrastructure.gate.MastersApiGate;
import ru.pulsecore.app.tournament.infrastructure.properties.MastersApiProperties;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;

import java.time.Duration;
import java.util.List;

@Profile("masters-client-get")
@Slf4j
@Component
@RequiredArgsConstructor
public class MastersApiGetClient implements MastersApi {

    private final MastersApiProperties properties;
    private final ObjectMapper mapper;
    private final MastersApiCircuitBreaker breaker;
    private final MastersApiGate apiGate;

    @Override
    public List<TournamentDto> loadTournaments(String date) {
        if (breaker.isBlocked()) {
            return List.of();
        }

        return apiGate.execute(() -> doLoadTournaments(date));
    }

    private List<TournamentDto> doLoadTournaments(String date) {
        log.info("СРАБОТАЛ НОВЫЙ ГЕТ ЗАПРОС");
        for (int i = 1; i <= 2; i++) {
            try {
                // 1. Формируем GET-запрос
                Connection connection = Jsoup.connect("https://masters-league.com/api/v1/tours")
                        .method(Connection.Method.GET)
                        .header("User-Agent", properties.getUserAgent())
                        .ignoreContentType(true)
                        .timeout(properties.getTimeout());

                // В новом API не нужен action, только country и date
                connection.data("country", properties.getCountry());
                if (date != null) connection.data("date", date);

                Connection.Response res = connection.execute();

                if (res.statusCode() != 200) {
                    breaker.recordFailure();
                    if (i == 2) return List.of();
                    sleep(breaker.backoff());
                    continue;
                }

                // 2. Парсим JSON (достаем массив "tours")
                JsonNode root = mapper.readTree(res.body());
                JsonNode toursNode = root.get("tours");

                if (toursNode == null || toursNode.isNull()) {
                    breaker.recordSuccess();
                    return List.of();
                }

                List<TournamentDto> tournaments = mapper.convertValue(
                        toursNode,
                        mapper.getTypeFactory().constructCollectionType(List.class, TournamentDto.class)
                );

                breaker.recordSuccess();

                // 3. Нормализуем игроков
                if (tournaments != null) {
                    for (TournamentDto t : tournaments) {
                        if (t.getPlayers() != null) {
                            t.setPlayers(NameNormalizer.normalizePlayers(t.getPlayers()));
                        }
                    }
                }

                return tournaments != null ? tournaments : List.of();

            } catch (Exception e) {
                log.warn("Masters API (GET): {} для даты {}", e.getMessage(), date);
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