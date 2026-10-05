package ru.pulsecore.app.shop.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pulsecore.app.shop.domain.DeliveryMethod;
import ru.pulsecore.app.shop.domain.OrderStatus;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    // ===== Метаданные заказа =====
    private Long id;
    private OrderStatus status;
    private LocalDateTime createdAt;

    // ===== Состав =====
    private List<OrderItemDto> items;
    private BigDecimal totalPrice;

    // ===== Оплата =====
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    // ===== Получение =====
    private DeliveryMethod deliveryMethod;
    private String pickupCity;
    private String pickupAddress;
    private String sellerPhone;      // телефон магазина для связи

    // ===== Комментарий покупателя =====
    private String comment;

    // ===== Служебное (только для редиректа ЮKassa, в JSON не попадёт если null) =====
    private String paymentConfirmationUrl;
}