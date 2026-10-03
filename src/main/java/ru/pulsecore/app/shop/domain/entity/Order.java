package ru.pulsecore.app.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.pulsecore.app.shop.domain.DeliveryMethod;
import ru.pulsecore.app.shop.domain.OrderStatus;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_user", columnList = "user_id"),
                @Index(name = "idx_order_status", columnList = "status"),
                @Index(name = "idx_order_created", columnList = "created_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false, length = 20)
    private DeliveryMethod deliveryMethod;



    @Column(name = "customer_first_name", length = 100)
    private String customerFirstName;

    @Column(name = "customer_last_name", length = 100)
    private String customerLastName;

    @Column(name = "customer_middle_name", length = 100)
    private String customerMiddleName;

    @Column(name = "delivery_phone", nullable = false, length = 30)
    private String deliveryPhone;

    @Column(name = "delivery_city", nullable = false, length = 100)
    private String deliveryCity;

    @Column(name = "delivery_street", nullable = false, length = 300)
    private String deliveryStreet;

    @Column(columnDefinition = "TEXT")
    private String comment;

    // ===== Сумма =====

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // ===== Оплата =====

    @Column(name = "yookassa_payment_id", length = 100)
    private String yookassaPaymentId;

    // ===== Товары =====

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    // ===== Даты =====

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}