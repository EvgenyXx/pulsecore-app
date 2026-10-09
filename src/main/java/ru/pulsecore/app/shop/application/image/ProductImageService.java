package ru.pulsecore.app.shop.application.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.ProductColorRequest;
import ru.pulsecore.app.shop.application.mapping.ProductImagesMapper;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductImage;
import ru.pulsecore.app.shop.infrastructure.exception.FileStorageException;
import ru.pulsecore.app.shop.infrastructure.repository.ProductImagesRepository;
import ru.pulsecore.app.shop.infrastructure.storage.FileStorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {

    private final ProductImagesMapper productImagesMapper;
    private final ProductImagesRepository productImagesRepository;
    private final FileStorageService fileStorageService;

    public ProductImage create(ProductColor color, ProductColorRequest.ImageRequest request) {
        ProductImage productImage = productImagesMapper.toEntity(request);
        productImage.setColor(color);
        return productImage;
    }

    @Transactional
    public void delete(Long imageId) {
        ProductImage image = getById(imageId);
        fileStorageService.delete(image.getUrl());
        productImagesRepository.delete(image);
    }

    public ProductImage getById(Long imageId) {
        return productImagesRepository.findById(imageId)
                .orElseThrow(() -> new FileStorageException("Фото не найдено: " + imageId));
    }
}