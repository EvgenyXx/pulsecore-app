package ru.pulsecore.app.shop.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.shop.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
}
