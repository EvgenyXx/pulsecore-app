package ru.pulsecore.app.tournament.infrastructure.util.hash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.TournamentDto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
@Profile("hash-sha256")
@RequiredArgsConstructor
@Primary
@Slf4j
public class Sha256TournamentHashCalculator implements TournamentHashCalculator {

    private static final String HASH_ALGORITHM = "SHA-256";
    private final ObjectMapper objectMapper;


    @Override
    public String calculateTournamentHash(TournamentDto tournament) {
        try {
            byte[] content = objectMapper.writeValueAsBytes(tournament);
            byte[] hash = MessageDigest.getInstance(HASH_ALGORITHM).digest(content);

            String result = HexFormat.of().formatHex(hash);

            log.debug("Вычислен хеш турнира: tournamentId={}, algorithm=sha256, hash={}",
                    tournament.getId(), result);

            return result;
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Не удалось вычислить SHA-256 хеш турнира: tournamentId=" + tournament.getId(), e);
        }
    }
}