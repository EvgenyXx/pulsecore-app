package ru.pulsecore.app.shop.infrastructure.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.shop.domain.entity.Order;
import ru.pulsecore.app.shop.domain.OrderStatus;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.domain.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, OrderStatus status);


    @EntityGraph(attributePaths = {"items"})
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findByIdAndUserId(Long id, UUID userId);

    @EntityGraph(attributePaths = {"items"})
    List<Order> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"items"})
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);


    List<Order> findByPaymentMethodAndPaymentStatusAndStatusAndCreatedAtBefore(
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            OrderStatus status,
            LocalDateTime threshold
    );


}