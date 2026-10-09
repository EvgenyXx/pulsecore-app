package ru.pulsecore.app.shop.application.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.ProductVariantRequest;
import ru.pulsecore.app.shop.application.mapping.ProductVariantMapper;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductSize;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;
import ru.pulsecore.app.shop.infrastructure.repository.ProductVariantRepository;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductVariantMapper variantMapper;

    @Transactional
    public ProductVariant create(Product product,
                                 ProductColor color,
                                 ProductSize size,
                                 ProductVariantRequest request) {
        ProductVariant variant = variantMapper.toEntity(request);
        variant.setProduct(product);
        variant.setColor(color);
        variant.setSize(size);
        return variantRepository.save(variant);
    }

    @Transactional
    public ProductVariant update(ProductVariant variant, ProductVariantRequest request) {
        variantMapper.update(request, variant);
        return variantRepository.save(variant);
    }
}