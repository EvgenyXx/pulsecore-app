package ru.pulsecore.app.shop.infrastructure.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.shop.domain.Order;
import ru.pulsecore.app.shop.domain.OrderStatus;
import ru.pulsecore.app.shop.domain.PaymentMethod;
import ru.pulsecore.app.shop.domain.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items"})
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findByIdAndUserId(Long id, UUID userId);

    @EntityGraph(attributePaths = {"items"})
    List<Order> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"items"})
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);


    @Modifying
    @Query("""
                UPDATE Order o
                SET o.status = :cancelled,
                    o.updatedAt = :now
                WHERE o.paymentMethod = :method
                  AND o.paymentStatus = :payment
                  AND o.status = :status
                  AND o.createdAt < :threshold
            """)
    int cancelUnpaidBefore(
            @Param("method") PaymentMethod method,
            @Param("payment") PaymentStatus payment,
            @Param("status") OrderStatus status,
            @Param("threshold") LocalDateTime threshold,
            @Param("cancelled") OrderStatus cancelled,
            @Param("now") LocalDateTime now
    );


}