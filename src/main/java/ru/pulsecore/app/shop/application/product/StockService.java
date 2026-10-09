package ru.pulsecore.app.shop.application.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.response.OrderProblem;
import ru.pulsecore.app.shop.domain.entity.*;
import ru.pulsecore.app.shop.infrastructure.exception.CartItemException;
import ru.pulsecore.app.shop.infrastructure.exception.OrderException;
import ru.pulsecore.app.shop.infrastructure.exception.ProductVariantNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.ProductVariantRepository;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final ProductVariantRepository variantRepository;

    public void ensureAvailableForOrder(List<CartItem> items) {
        if (items == null || items.isEmpty()) return;

        List<OrderProblem> problems = new ArrayList<>();

        for (CartItem item : items) {
            OrderProblem problem = checkItem(item);
            if (problem != null) problems.add(problem);
        }

        if (!problems.isEmpty()) {
            throw new OrderException("Некоторые товары недоступны", problems);
        }
    }


    public void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new CartItemException();
        }
    }

    public void validateStock(ProductVariant variant, int quantity) {
        Integer stock = variant.getStock();
        if (stock == null || stock < quantity) {
            throw new CartItemException(stock);
        }
    }

    @Transactional
    public void decreaseForOrder(Order order) {
        List<ProductVariant> updated = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            ProductVariant variant = resolveVariant(item);

            if (variant.getStock() < item.getQuantity()) {
                throw new OrderException(
                        "Недостаточно товара на складе: " + item.getProductName()
                );
            }

            variant.setStock(variant.getStock() - item.getQuantity());
            updated.add(variant);

            log.info("Списано: variantId={}, qty={}, остаток={}",
                    variant.getId(), item.getQuantity(), variant.getStock());
        }

        variantRepository.saveAll(updated);
    }

    @Transactional
    public void increaseForOrder(Order order) {
        List<ProductVariant> updated = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            ProductVariant variant = resolveVariant(item);

            variant.setStock(variant.getStock() + item.getQuantity());
            updated.add(variant);

            log.info("Возврат на склад: variantId={}, qty={}, остаток={}",
                    variant.getId(), item.getQuantity(), variant.getStock());
        }

        variantRepository.saveAll(updated);
    }

    private ProductVariant resolveVariant(OrderItem item) {
        return variantRepository.findById(item.getVariantId())
                .orElseThrow(() -> new ProductVariantNotFoundException(item.getVariantId()));
    }


    private OrderProblem checkItem(CartItem item) {
        ProductVariant variant = item.getVariant();

        OrderProblem inactive = checkInactive(variant);
        if (inactive != null) return inactive;

        return checkStock(variant, item.getQuantity());
    }

    private OrderProblem checkInactive(ProductVariant variant) {
        Product product = variant.getProduct();
        if (product != null && !product.isActive()) {
            return new OrderProblem(
                    product.getName(),
                    buildVariantLabel(variant),
                    null,
                    OrderProblem.ProblemType.INACTIVE
            );
        }
        return null;
    }

    private OrderProblem checkStock(ProductVariant variant, int quantity) {
        Integer stock = variant.getStock();

        if (stock == null || stock <= 0) {
            return new OrderProblem(
                    variant.getProduct().getName(),
                    buildVariantLabel(variant),
                    0,
                    OrderProblem.ProblemType.OUT_OF_STOCK
            );
        }

        if (stock < quantity) {
            return new OrderProblem(
                    variant.getProduct().getName(),
                    buildVariantLabel(variant),
                    stock,
                    OrderProblem.ProblemType.STOCK_SHORTAGE
            );
        }

        return null;
    }

    private String buildVariantLabel(ProductVariant variant) {
        List<String> parts = new ArrayList<>();
        if (variant.getColor() != null) parts.add(variant.getColor().getColor());
        if (variant.getSize() != null) parts.add(variant.getSize().getSize());
        return parts.isEmpty() ? null : String.join(" · ", parts);
    }
}