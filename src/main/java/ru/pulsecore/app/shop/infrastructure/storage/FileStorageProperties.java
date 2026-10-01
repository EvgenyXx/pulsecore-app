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

    private String uploadDir = "uploads";
    private long maxFileSize = 5 * 1024 * 1024;
    private List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/webp");
}