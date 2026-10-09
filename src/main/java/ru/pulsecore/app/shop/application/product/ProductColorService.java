package ru.pulsecore.app.shop.application.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.ProductColorRequest;
import ru.pulsecore.app.shop.application.mapping.ProductColorMapper;
import ru.pulsecore.app.shop.application.mapping.ProductImagesMapper;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductImage;
import ru.pulsecore.app.shop.infrastructure.repository.ProductColorRepository;
import ru.pulsecore.app.shop.infrastructure.repository.ProductImagesRepository;
import ru.pulsecore.app.shop.infrastructure.storage.FileStorageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductColorService {

    private final ProductColorRepository productColorRepository;
    private final ProductImagesMapper productImagesMapper;
    private final ProductColorMapper productColorMapper;
    private final ProductImagesRepository productImageRepository;
    private final FileStorageService fileStorageService;


    @Transactional
    public ProductColor create(Product product, ProductColorRequest request) {
        ProductColor color = productColorMapper.toEntity(request, product);

        if (request.images() != null) {
            for (ProductColorRequest.ImageRequest imgReq : request.images()) {
                ProductImage img = productImagesMapper.toEntity(imgReq);
                img.setColor(color);
                color.getImages().add(img);
            }
        }

        return productColorRepository.save(color);
    }

    @Transactional
    public ProductColor update(ProductColor color, ProductColorRequest request) {
        productColorMapper.update(request, color);

        if (request.images() != null) {
            // 1. Собрать url старых фото
            List<String> oldUrls = color.getImages().stream()
                    .map(ProductImage::getUrl)
                    .toList();

            // 2. Очистить в памяти (orphanRemoval удалит из БД)
            color.getImages().clear();

            // 3. Добавить новые
            for (ProductColorRequest.ImageRequest imgReq : request.images()) {
                ProductImage img = productImagesMapper.toEntity(imgReq);
                img.setColor(color);
                color.getImages().add(img);
            }

            // 4. Сохранить в БД
            ProductColor saved = productColorRepository.save(color);

            // 5. Удалить старые файлы из облака — только те, что больше нигде не используются
            cleanupOrphanImages(oldUrls);

            return saved;
        }

        return productColorRepository.save(color);
    }

    private void cleanupOrphanImages(List<String> urls) {
        if (urls.isEmpty()) return;

        // flush нужен, чтобы удаление старых product_image уже было в БД
        productColorRepository.flush();

        for (String url : urls) {
            long refCount = productImageRepository.countByUrl(url);
            if (refCount == 0) {
                fileStorageService.delete(url);
            }
        }
    }
}