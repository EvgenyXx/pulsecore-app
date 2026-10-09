package ru.pulsecore.app.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "product_image",
        indexes = @Index(name = "idx_product_image_color", columnList = "color_id")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private ProductColor color;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_main", nullable = false)
    @Builder.Default
    private boolean main = false;
}