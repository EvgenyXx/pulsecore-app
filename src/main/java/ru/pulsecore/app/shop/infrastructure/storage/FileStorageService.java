package ru.pulsecore.app.shop.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pulsecore.app.shop.infrastructure.exception.FileStorageException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final S3Client s3;
    private final FileStorageProperties props;
    private final FileValidator validator;
    private final S3UrlHelper urlHelper;

    public String save(MultipartFile file, String subdir) {
        validator.validate(file);

        String key = buildKey(subdir, file);
        String bucket = props.getS3().getBucket();

        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("S3 upload ok: bucket={}, key={}, size={}", bucket, key, file.getSize());
        } catch (Exception e) {
            log.error("S3 upload error: bucket={}, key={}", bucket, key, e);
            throw new FileStorageException("Не удалось загрузить файл: " + key);
        }

        return urlHelper.buildPublicUrl(key);
    }

    public void delete(String url) {
        String key = urlHelper.extractKey(url);
        if (key == null) return;

        String bucket = props.getS3().getBucket();

        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());

        log.info("S3 delete ok: bucket={}, key={}", bucket, key);
    }

    public void deleteAll(List<String> urls) {
        if (urls == null || urls.isEmpty()) return;
        urls.forEach(this::delete);
    }

    /**
     * Собирает ключ: <subdir>/<uuid>.<ext>
     * Пример: products/af4c9371-...webp
     */
    private String buildKey(String subdir, MultipartFile file) {
        String ext = extractExtension(file.getOriginalFilename());
        return subdir + "/" + UUID.randomUUID() + ext;
    }

    private String extractExtension(String name) {
        if (name == null) return ".jpg";

        int dotIndex = name.lastIndexOf('.');
        if (dotIndex <= 0) return ".jpg";

        return name.substring(dotIndex).toLowerCase();
    }
}