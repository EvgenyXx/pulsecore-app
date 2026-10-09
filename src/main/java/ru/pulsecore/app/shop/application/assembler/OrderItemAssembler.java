package ru.pulsecore.app.shop.application.assembler;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shop.domain.entity.CartItem;
import ru.pulsecore.app.shop.domain.entity.OrderItem;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductImage;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;

import java.math.BigDecimal;

@Component
public class OrderItemAssembler {

    public OrderItem toOrderItem(CartItem ci) {
        ProductVariant variant = ci.getVariant();
        Product p = variant.getProduct();

        BigDecimal unitPrice = p.getPrice().add(
                variant.getPriceDelta() != null ? variant.getPriceDelta() : BigDecimal.ZERO
        );

        return OrderItem.builder()
                .productId(p.getId())
                .variantId(variant.getId())
                .productName(p.getName())
                .productBrand(p.getBrand())
                .variantSize(resolveSize(variant))
                .variantColor(resolveColor(variant))
                .productImageUrl(resolveImage(variant))
                .productPrice(unitPrice)
                .quantity(ci.getQuantity())
                .build();
    }

    private String resolveSize(ProductVariant variant) {
        if (variant.getSize() == null) return null;
        return variant.getSize().getSize();
    }

    private String resolveColor(ProductVariant variant) {
        if (variant.getColor() == null) return null;
        return variant.getColor().getColor();
    }

    private String resolveImage(ProductVariant variant) {
        if (variant.getColor() == null) return null;

        ProductColor color = variant.getColor();
        if (color.getImages() == null || color.getImages().isEmpty()) return null;

        return color.getImages().stream()
                .filter(ProductImage::isMain)
                .map(ProductImage::getUrl)
                .findFirst()
                .orElseGet(() -> color.getImages().get(0).getUrl());
    }
}