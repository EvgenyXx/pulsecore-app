package ru.pulsecore.app.core.integration;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.shared.exception.SiteUnavailableException;

@Service
@Slf4j
public class DocumentLoader {

    private static final int MAX_ATTEMPTS = 3;
    private static final int TIMEOUT = 60_000;  // было 30_000, стало 60_000

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
            } catch (Exception e) {
                throw new SiteUnavailableException();
            }
        }
        throw new SiteUnavailableException();
    }
}