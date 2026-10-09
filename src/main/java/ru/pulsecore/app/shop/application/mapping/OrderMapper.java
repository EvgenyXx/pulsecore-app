package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.api.dto.response.OrderItemDto;
import ru.pulsecore.app.shop.api.dto.response.SellerOrderDto;
import ru.pulsecore.app.shop.domain.entity.Order;
import ru.pulsecore.app.shop.domain.entity.OrderItem;
import ru.pulsecore.app.shop.infrastructure.config.ShopProperties;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // MapStruct генерит имплементацию ТОЛЬКО для этого метода (и toSellerDto).
    // pickup-поля ignored — их заполнит default-обёртка toDto(...).
    @Mapping(target = "items", expression = "java(toItemDtos(order.getItems()))")
    @Mapping(target = "paymentConfirmationUrl", source = "paymentUrl")
    @Mapping(target = "sellerPhone", ignore = true)
    @Mapping(target = "pickupCity", ignore = true)
    @Mapping(target = "pickupAddress", ignore = true)
    OrderDto toDtoRaw(Order order, String paymentUrl);

    // Обычный Java-метод. MapStruct его не трогает. Сам вызывает toDtoRaw и заполняет pickup.
    default OrderDto toDto(Order order, String paymentUrl, ShopProperties props) {
        if (order == null) return null;

        OrderDto dto = toDtoRaw(order, paymentUrl);

        dto.setPickupCity(order.getDeliveryCity());
        dto.setPickupAddress(order.getDeliveryStreet());

        if (props != null && props.getPickup() != null) {
            dto.setSellerPhone(props.getPickup().getPhone());
        }
        return dto;
    }



    default OrderDto toDto(Order order) {
        return toDto(order, null, null);
    }

    @Mapping(target = "items", expression = "java(toItemDtos(order.getItems()))")
    SellerOrderDto toSellerDto(Order order);

    default List<OrderItemDto> toItemDtos(List<OrderItem> items) {
        if (items == null) return List.of();
        return items.stream().map(this::toItemDto).toList();
    }

    OrderItemDto toItemDto(OrderItem item);
}