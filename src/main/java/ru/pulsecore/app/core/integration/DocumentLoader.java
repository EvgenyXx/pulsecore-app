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

    public Document load(String url) {
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            try {
                return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(TIMEOUT)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .get();
            } catch (java.net.SocketTimeoutException e) {
                log.warn("Timeout loading {}, attempt {}/{}", url, i, MAX_ATTEMPTS);
                if (i < MAX_ATTEMPTS) {
                    try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                }
            } catch (Exception e) {
                log.error("Error loading {}: {} - {}", url, e.getClass().getSimpleName(), e.getMessage());
                if (i < MAX_ATTEMPTS) {
                    try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                } else {
                    throw new SiteUnavailableException();
                }
            }
        }
        throw new SiteUnavailableException();
    }
}