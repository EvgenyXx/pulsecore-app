package ru.pulsecore.app.tournament.infrastructure.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.exception.SiteUnavailableException;
import ru.pulsecore.app.tournament.infrastructure.circuit.MastersApiCircuitBreaker;
import ru.pulsecore.app.tournament.infrastructure.exception.PageNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.gate.MastersApiGate;

import java.util.concurrent.Semaphore;


@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentLoader {

    private final MastersApiCircuitBreaker breaker;
    private static final int TIMEOUT = 30_000;
    private final MastersApiGate apiGate;


    public Document load(String url) {
        if (breaker.isBlocked()) {
            throw new SiteUnavailableException();
        }

        return apiGate.execute(() -> doLoad(url));
    }

    private Document doLoad(String url) {
        for (int i = 1; i <= 2; i++) {
            try {
                Connection.Response response = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(TIMEOUT)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .execute();

                if (response.statusCode() == 404) {
                    breaker.recordSuccess();
                    throw new PageNotFoundException(url);
                }

                breaker.recordSuccess();
                return response.parse();
            } catch (PageNotFoundException e) {
                throw e;
            } catch (Exception e) {
                breaker.recordFailure();
                log.error("Ошибка при запросе к {}: {}", url, e.getMessage());

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

//    public Document load(String url) {
//        if (breaker.isBlocked()) {
//            throw new SiteUnavailableException();
//        }
//
//        for (int i = 1; i <= 2; i++) {
//            try {
//                Connection.Response response = Jsoup.connect(url)
//                        .userAgent("Mozilla/5.0")
//                        .timeout(TIMEOUT)
//                        .ignoreHttpErrors(true)
//                        .ignoreContentType(true)
//                        .execute();
//
//                if (response.statusCode() == 404) {
//                    breaker.recordSuccess();
//                    throw new PageNotFoundException(url);
//                }
//
//                breaker.recordSuccess();
//                return response.parse();
//            } catch (PageNotFoundException e) {
//                throw e;
//            } catch (Exception e) {
//                breaker.recordFailure();
//                log.error("Ошибка при запросе к {}: {}", url, e.getMessage());
//
//                if (i == 2) throw new SiteUnavailableException();
//                sleep(breaker.backoff());
//            }
//        }
//        throw new SiteUnavailableException();
//    }
//
//
//    private void sleep(java.time.Duration duration) {
//        try {
//            Thread.sleep(duration.toMillis());
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//}