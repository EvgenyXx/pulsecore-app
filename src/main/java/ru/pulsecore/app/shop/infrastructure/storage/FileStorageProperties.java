package ru.pulsecore.app.shop.infrastructure.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.storage")
public class FileStorageProperties {

    private long maxFileSize = 5 * 1024 * 1024;
    private List<String> allowedTypes = List.of(
            "image/jpeg", "image/png", "image/webp",
            "image/heic", "image/heif"
    );

    private S3 s3 = new S3();

    @Getter
    @Setter
    public static class S3 {
        private String bucket;
        private String endpoint;
        private String region = "ru-central1";
        private String accessKey;
        private String secretKey;
        private String publicUrl;
    }
}