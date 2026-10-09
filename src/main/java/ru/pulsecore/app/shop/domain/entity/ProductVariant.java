package ru.pulsecore.app.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "product_variant",
        uniqueConstraints = @UniqueConstraint(
                name = "product_variant_unique",
                columnNames = {"product_id", "color_id", "size_id"}
        ),
        indexes = {
                @Index(name = "idx_product_variant_product", columnList = "product_id"),
                @Index(name = "idx_product_variant_color", columnList = "color_id"),
                @Index(name = "idx_product_variant_size", columnList = "size_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    private ProductColor color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id")
    private ProductSize size;

    @Column(name = "stock", nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "price_delta", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal priceDelta = BigDecimal.ZERO;
}