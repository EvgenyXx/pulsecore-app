package ru.pulsecore.app.shop.application.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.domain.entity.Order;
import ru.pulsecore.app.shop.domain.entity.OrderItem;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.infrastructure.exception.OrderException;
import ru.pulsecore.app.shop.infrastructure.exception.ProductNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final ProductRepository productRepository;

    /**
     * Списать количество товара со склада по заказу.
     * Проверяет, что stock хватает. Если нет — OrderException, транзакция откатится.
     */
    @Transactional
    public void decreaseForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new OrderException(
                        "Недостаточно товара на складе: " + product.getName()
                );
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            log.info("Списано: productId={}, qty={}, остаток={}",
                    product.getId(), item.getQuantity(), product.getStock());
        }
    }

    @Transactional
    public void increaseForOrder(Order order) {
        List<Product> updated = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));

            product.setStock(product.getStock() + item.getQuantity());
            updated.add(product);

            log.info("Возврат на склад: productId={}, qty={}, остаток={}",
                    product.getId(), item.getQuantity(), product.getStock());
        }

        productRepository.saveAll(updated);
    }
}