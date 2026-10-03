package ru.pulsecore.app.shop.application.assembler;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shop.domain.entity.CartItem;
import ru.pulsecore.app.shop.domain.entity.OrderItem;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductImage;

@Component
public class OrderItemAssembler {

    public OrderItem toOrderItem(CartItem ci) {
        Product p = ci.getProduct();
        return OrderItem.builder()
                .productId(p.getId())
                .productName(p.getName())
                .productBrand(p.getBrand())
                .productImageUrl(resolveImage(p))
                .productPrice(p.getPrice())
                .quantity(ci.getQuantity())
                .build();
    }

    private String resolveImage(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) return null;
        return product.getImages().stream()
                .filter(ProductImage::isMain)
                .map(ProductImage::getUrl)
                .findFirst()
                .orElseGet(() -> product.getImages().get(0).getUrl());
    }
}