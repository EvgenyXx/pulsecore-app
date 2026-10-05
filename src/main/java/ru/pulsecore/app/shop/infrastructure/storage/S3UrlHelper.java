package ru.pulsecore.app.shop.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3UrlHelper {

    private final FileStorageProperties props;

    /**
     * Из публичного URL вытаскивает S3-ключ.
     * https://storage.yandexcloud.net/pulsecore-products/products/uuid.jpg
     *   → products/uuid.jpg
     *
     * @return ключ или null, если URL не из нашего бакета
     */
    public String extractKey(String url) {
        if (url == null || url.isBlank()) return null;

        String prefix = props.getS3().getPublicUrl();
        if (!url.startsWith(prefix)) {
            log.warn("URL не из нашего бакета: {}", url);
            return null;
        }

        String key = url.substring(prefix.length());
        // Убираем ведущий слеш и возможные query-параметры/фрагменты
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
        int q = key.indexOf('?');
        if (q >= 0) key = key.substring(0, q);
        int h = key.indexOf('#');
        if (h >= 0) key = key.substring(0, h);

        if (key.isBlank()) return null;
        return key;
    }

    /**
     * Собирает публичный URL из ключа.
     * products/uuid.jpg → https://storage.yandexcloud.net/pulsecore-products/products/uuid.jpg
     */
    public String buildPublicUrl(String key) {
        String base = props.getS3().getPublicUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + key;
    }
}