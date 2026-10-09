package ru.pulsecore.app.shop.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.shop.domain.entity.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
              AND (
                    LOWER(p.name)        LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(p.brand)       LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """)
    Page<Product> search(@Param("query") String query, Pageable pageable);

}
