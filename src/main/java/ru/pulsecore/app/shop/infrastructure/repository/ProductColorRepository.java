package ru.pulsecore.app.shop.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.shop.domain.entity.ProductColor;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {

    List<ProductColor> findByProductId(Long productId);

    Optional<ProductColor> findByProductIdAndColor(Long productId, String color);

    boolean existsByProductIdAndColor(Long productId, String color);

    void deleteByProductId(Long productId);
}