package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.shop.api.dto.response.CartDto;
import ru.pulsecore.app.shop.api.dto.response.CartItemDto;
import ru.pulsecore.app.shop.domain.Cart;
import ru.pulsecore.app.shop.domain.CartItem;

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

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.brand", target = "brand")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.stock", target = "stock")
    @Mapping(target = "image", expression = "java(resolveImage(item))")
    CartItemDto toItemDto(CartItem item);

    default Integer calcTotalQuantity(Cart cart) {
        if (cart.getItems() == null) return 0;
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    default BigDecimal calcTotalPrice(Cart cart) {
        if (cart.getItems() == null) return BigDecimal.ZERO;
        return cart.getItems().stream()
                .map(i -> i.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default String resolveImage(CartItem item) {
        if (item.getProduct() == null
                || item.getProduct().getImages() == null
                || item.getProduct().getImages().isEmpty()) {
            return null;
        }
        return item.getProduct().getImages().stream()
                .filter(img -> img.isMain())
                .map(img -> img.getUrl())
                .findFirst()
                .orElseGet(() -> item.getProduct().getImages().get(0).getUrl());
    }
}