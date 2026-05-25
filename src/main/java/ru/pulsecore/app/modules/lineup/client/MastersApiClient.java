package ru.pulsecore.app.modules.lineup.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.shared.properties.MastersApiProperties;
import ru.pulsecore.app.modules.shared.service.NameNormalizer;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MastersApiClient {

    private final MastersApiProperties properties;
    private final ObjectMapper mapper;
    private final NameNormalizer nameNormalizer;

    public List<TournamentDto> loadTournaments(String date) {
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

                res.body();
                log.info("Masters API: HTTP {}, body length: {}",
                        res.statusCode(), res.body().length());

                if (res.statusCode() != 200) {
                    String preview = res.body() != null ? res.body().substring(0, Math.min(300, res.body().length())) : "empty";
                    log.warn("Masters API non-200: code={}, preview={}", res.statusCode(), preview);
                    if (i == 2) return List.of();
                    continue;
                }

                String body = res.body();
                if (body.trim().startsWith("<")) {
                    log.error("Masters API вернул HTML вместо JSON: {}", body.substring(0, Math.min(500, body.length())));
                    if (i == 2) return List.of();
                    continue;
                }

                List<TournamentDto> tournaments = mapper.readValue(body, new TypeReference<>() {});

                // Нормализуем имена игроков в каждом турнире
                if (tournaments != null) {
                    for (TournamentDto tournament : tournaments) {
                        if (tournament.getPlayers() != null) {
                            tournament.setPlayers(nameNormalizer.normalizePlayers(tournament.getPlayers()));
                        }
                    }
                }

                return tournaments;

            } catch (java.net.SocketTimeoutException e) {
                log.warn("Masters API timeout, attempt {}", i);
            } catch (Exception e) {
                log.error("Masters API error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                if (i == 2) return List.of();
            }
        }
        log.warn("Masters API недоступен после 2 попыток");
        return List.of();
    }
}