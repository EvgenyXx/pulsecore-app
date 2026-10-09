package ru.pulsecore.app.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "order_item",
        indexes = @Index(name = "idx_order_item_order", columnList = "order_id")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Снимок product_id — без FK, чтобы не удалялся при удалении товара
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_brand", length = 100)
    private String productBrand;

    @Column(name = "variant_size", length = 50)
    private String variantSize;

    @Column(name = "variant_color", length = 100)
    private String variantColor;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column(name = "product_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal productPrice;

    @Column(nullable = false)
    private Integer quantity;
}