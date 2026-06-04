package ru.pulsecore.app.core.integration;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.shared.exception.SiteUnavailableException;

@Service
@Slf4j
public class DocumentLoader {

    private static final int MAX_ATTEMPTS = 2;
    private static final int TIMEOUT = 30_000;
    private static final int RETRY_DELAY_MS = 3000;

    public Document load(String url) {
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            try {
                return fetch(url);
            } catch (Exception e) {
                if (!handleError(url, i, e)) {
                    throw new SiteUnavailableException();
                }
            }
        }
        throw new SiteUnavailableException();
    }

    private Document fetch(String url) throws java.io.IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(TIMEOUT)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .get();
    }

    private boolean handleError(String url, int attempt, Exception e) {
        logAttempt(url, attempt, e);
        if (attempt < MAX_ATTEMPTS) {
            sleep();
            return true;
        }
        return false;
    }

    private void logAttempt(String url, int attempt, Exception e) {
        if (e instanceof java.net.SocketTimeoutException) {
            log.warn("Timeout loading {}, attempt {}/{}", url, attempt, MAX_ATTEMPTS);
        } else {
            log.error("Error loading {}: {} - {}", url, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void sleep() {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}