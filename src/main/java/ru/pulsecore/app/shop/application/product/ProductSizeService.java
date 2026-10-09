package ru.pulsecore.app.shop.application.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.ProductSizeRequest;
import ru.pulsecore.app.shop.application.mapping.ProductSizeMapper;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductSize;
import ru.pulsecore.app.shop.infrastructure.repository.ProductSizeRepository;

@Service
@RequiredArgsConstructor
public class ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    private final ProductSizeMapper productSizeMapper;

    @Transactional
    public ProductSize create(Product product, ProductSizeRequest request) {
        ProductSize size = productSizeMapper.toEntity(request, product);
        return productSizeRepository.save(size);
    }

    @Transactional
    public ProductSize update(ProductSize size, ProductSizeRequest request) {
        productSizeMapper.update(request, size);
        return productSizeRepository.save(size);
    }
}