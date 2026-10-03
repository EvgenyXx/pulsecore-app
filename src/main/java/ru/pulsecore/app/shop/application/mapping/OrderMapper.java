package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.api.dto.response.OrderItemDto;
import ru.pulsecore.app.shop.api.dto.response.SellerOrderDto;
import ru.pulsecore.app.shop.domain.entity.Order;
import ru.pulsecore.app.shop.domain.entity.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", expression = "java(toItemDtos(order.getItems()))")
    @Mapping(target = "paymentConfirmationUrl", source = "paymentUrl")
    OrderDto toDto(Order order, String paymentUrl);

    default OrderDto toDto(Order order) {
        return toDto(order, null);
    }

    @Mapping(target = "items", expression = "java(toItemDtos(order.getItems()))")
    SellerOrderDto toSellerDto(Order order);

    default List<OrderItemDto> toItemDtos(List<OrderItem> items) {
        if (items == null) return List.of();
        return items.stream().map(this::toItemDto).toList();
    }

    OrderItemDto toItemDto(OrderItem item);
}