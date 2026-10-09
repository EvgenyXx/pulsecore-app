package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.shop.api.dto.response.CartDto;
import ru.pulsecore.app.shop.api.dto.response.CartItemDto;
import ru.pulsecore.app.shop.domain.entity.Cart;
import ru.pulsecore.app.shop.domain.entity.CartItem;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductImage;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "items", expression = "java(toItemDtos(cart))")
    @Mapping(target = "totalQuantity", expression = "java(calcTotalQuantity(cart))")
    @Mapping(target = "totalPrice", expression = "java(calcTotalPrice(cart))")
    CartDto toDto(Cart cart);

    default List<CartItemDto> toItemDtos(Cart cart) {
        if (cart.getItems() == null) return List.of();
        return cart.getItems().stream()
                .map(this::toItemDto)
                .toList();
    }

    @Mapping(source = "variant.id", target = "variantId")
    @Mapping(source = "variant.product.id", target = "productId")
    @Mapping(source = "variant.product.name", target = "name")
    @Mapping(source = "variant.product.brand", target = "brand")
    @Mapping(target = "price", expression = "java(resolvePrice(item))")
    @Mapping(source = "variant.stock", target = "stock")
    @Mapping(target = "size", expression = "java(resolveSize(item))")
    @Mapping(target = "color", expression = "java(resolveColor(item))")
    @Mapping(target = "image", expression = "java(resolveImage(item))")
    CartItemDto toItemDto(CartItem item);

    default BigDecimal resolvePrice(CartItem item) {
        return item.getVariant().getProduct().getPrice()
                .add(item.getVariant().getPriceDelta() != null
                        ? item.getVariant().getPriceDelta()
                        : BigDecimal.ZERO);
    }

    default Integer calcTotalQuantity(Cart cart) {
        if (cart.getItems() == null) return 0;
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    default BigDecimal calcTotalPrice(Cart cart) {
        if (cart.getItems() == null) return BigDecimal.ZERO;
        return cart.getItems().stream()
                .map(i -> resolvePrice(i).multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default String resolveSize(CartItem item) {
        ProductVariant v = item.getVariant();
        if (v == null || v.getSize() == null) return null;
        return v.getSize().getSize();
    }

    default String resolveColor(CartItem item) {
        ProductVariant v = item.getVariant();
        if (v == null || v.getColor() == null) return null;
        return v.getColor().getColor();
    }

    default String resolveImage(CartItem item) {
        ProductVariant variant = item.getVariant();
        if (variant == null || variant.getColor() == null) return null;

        ProductColor color = variant.getColor();
        List<ProductImage> images = color.getImages();
        if (images == null || images.isEmpty()) return null;

        return images.stream()
                .filter(ProductImage::isMain)
                .map(ProductImage::getUrl)
                .findFirst()
                .orElseGet(() -> images.get(0).getUrl());
    }
}