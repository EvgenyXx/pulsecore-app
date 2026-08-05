package ru.pulsecore.app.core.integration;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.exception.SiteUnavailableException;
import ru.pulsecore.app.modules.tournament.infrastructure.circuit.MastersApiCircuitBreaker;


@Component
@RequiredArgsConstructor
public class DocumentLoader {

    private final MastersApiCircuitBreaker breaker;
    private static final int TIMEOUT = 30_000;

    public Document load(String url) {
        if (breaker.isBlocked()) {
            throw new SiteUnavailableException();
        }

        for (int i = 1; i <= 2; i++) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(TIMEOUT)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .get();
                breaker.recordSuccess();
                return doc;
            } catch (Exception e) {
                breaker.recordFailure();
                if (i == 2) throw new SiteUnavailableException();
                sleep(breaker.backoff());
            }
        }
        throw new SiteUnavailableException();
    }

    private void sleep(java.time.Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}