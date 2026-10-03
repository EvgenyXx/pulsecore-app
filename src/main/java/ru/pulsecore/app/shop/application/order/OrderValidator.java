package ru.pulsecore.app.shop.application.order;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shop.api.dto.request.CreateOrderRequest;
import ru.pulsecore.app.shop.domain.entity.CartItem;
import ru.pulsecore.app.shop.domain.DeliveryMethod;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.infrastructure.exception.OrderException;

import java.util.List;
import java.util.UUID;

@Component
public class OrderValidator {

    public void validate(UUID userId, List<CartItem> selected) {
        validateNotEmpty(selected);
        validateOwnership(selected, userId);
        validateStock(selected);
    }

//    public void validateRequest(CreateOrderRequest request) {
//        if (request.deliveryMethod() == DeliveryMethod.CDEK) {
//            if (request.city() == null || request.city().isBlank()) {
//                throw new OrderException("Город обязателен для СДЭК");
//            }
//            if (request.street() == null || request.street().isBlank()) {
//                throw new OrderException("Адрес ПВЗ обязателен для СДЭК");
//            }
//            if (request.paymentMethod() == PaymentMethod.ON_DELIVERY) {
//                throw new OrderException("СДЭК не поддерживает оплату при получении");
//            }
//        }
//    }

    private void validateNotEmpty(List<CartItem> selected) {
        if (selected.isEmpty()) {
            throw new OrderException("Товары не найдены");
        }
    }

    private void validateOwnership(List<CartItem> selected, UUID userId) {
        boolean allMine = selected.stream()
                .allMatch(ci -> ci.getCart().getUserId().equals(userId));
        if (!allMine) {
            throw new OrderException("Некорректные товары в заказе");
        }
    }

    private void validateStock(List<CartItem> selected) {
        for (CartItem ci : selected) {
            if (ci.getQuantity() > ci.getProduct().getStock()) {
                throw new OrderException(
                    "Недостаточно товара на складе: " + ci.getProduct().getName()
                );
            }
        }
    }
}