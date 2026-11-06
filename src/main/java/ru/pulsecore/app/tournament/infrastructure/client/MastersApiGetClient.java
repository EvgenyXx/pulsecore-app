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
            try {
                Connection connection = Jsoup.connect("https://masters-league.com/api/v1/tours")
                        .method(Connection.Method.GET)
                        .header("User-Agent", properties.getUserAgent())
                        .ignoreContentType(true)
                        .timeout(properties.getTimeout());
                connection.data("country", properties.getCountry());
                connection.data("date", date);

                Connection.Response res = connection.execute();

                JsonNode root = mapper.readTree(res.body());
                JsonNode toursNode = root.get("tours");


                List<TournamentDto> tournaments = mapper.convertValue(
                        toursNode,
                        mapper.getTypeFactory().constructCollectionType(List.class, TournamentDto.class)
                );

                breaker.recordSuccess();

                return tournaments;

            } catch (Exception e) {
                log.error("Masters API (GET): {} для даты {}", e.getMessage(), date);
                breaker.recordFailure();
                return List.of();
            }
        }




}