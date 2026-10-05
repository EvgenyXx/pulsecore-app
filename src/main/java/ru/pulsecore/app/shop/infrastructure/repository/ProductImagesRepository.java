package ru.pulsecore.app.shop.infrastructure.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.shop.domain.entity.ProductImage;

import java.util.List;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);

    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    @Query("SELECT pi.url FROM ProductImage pi WHERE pi.product.id = :productId")
    List<String> findUrlsByProductId(@Param("productId") Long productId);
}
