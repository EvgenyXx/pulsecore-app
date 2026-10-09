package ru.pulsecore.app.shop.application.order;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shop.domain.entity.CartItem;
import ru.pulsecore.app.shop.infrastructure.exception.OrderException;
import java.util.List;
import java.util.UUID;

@Component
public class OrderValidator {

    public void validate(UUID userId, List<CartItem> selected) {
        validateNotEmpty(selected);
        validateOwnership(selected, userId);
    }

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

}