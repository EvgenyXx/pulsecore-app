package ru.pulsecore.app.shop.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StorageConfig {

    private final FileStorageProperties props;

    @Bean
    public S3Client s3Client() {
        FileStorageProperties.S3 s3cfg = props.getS3();

        validateConfig(s3cfg);

        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(s3cfg.getEndpoint()))
                .region(Region.of(s3cfg.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3cfg.getAccessKey(), s3cfg.getSecretKey())
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();

        log.info("S3Client initialized: bucket={}, endpoint={}",
                s3cfg.getBucket(), s3cfg.getEndpoint());

        return client;
    }

    private void validateConfig(FileStorageProperties.S3 s3) {
        if (isBlank(s3.getBucket()))    throw new IllegalStateException("app.storage.s3.bucket не задан");
        if (isBlank(s3.getEndpoint()))  throw new IllegalStateException("app.storage.s3.endpoint не задан");
        if (isBlank(s3.getRegion()))    throw new IllegalStateException("app.storage.s3.region не задан");
        if (isBlank(s3.getAccessKey())) throw new IllegalStateException("S3_ACCESS_KEY не задан");
        if (isBlank(s3.getSecretKey())) throw new IllegalStateException("S3_SECRET_KEY не задан");
        if (isBlank(s3.getPublicUrl())) throw new IllegalStateException("app.storage.s3.public-url не задан");
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}