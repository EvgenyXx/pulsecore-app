package ru.pulsecore.app.notification.application.mail.context;

import java.math.BigDecimal;
import java.util.List;

public record OrderPaidContext(
        String to,
        String customerFirstName,
        String customerLastName,
        Long orderId,
        BigDecimal totalPrice,
        String paymentMethod,        // ← новое
        List<Item> items,
        String deliveryMethod,
        String deliveryCity,
        String deliveryStreet,
        String pickupPhone
) implements MailContext {

    public record Item(
            String productName,
            Integer quantity,
            BigDecimal price
    ) {}
}