package ru.pulsecore.app.shop.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.shop.domain.entity.ProductImage;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImage,Long> {
}
