package ru.pulsecore.app.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "cart_item",
    indexes = {
        @Index(name = "idx_cart_item_cart", columnList = "cart_id"),
        @Index(name = "idx_cart_item_variant", columnList = "variant_id")
    },
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cart_item_cart_variant",
        columnNames = {"cart_id", "variant_id"}
    )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(nullable = false)
    private Integer quantity;
}