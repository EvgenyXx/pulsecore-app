package ru.pulsecore.app.shop.infrastructure.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.shop.domain.entity.ProductSize;

import java.util.List;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize,Long> {
    List<ProductSize> findByProductId(Long id);
}
