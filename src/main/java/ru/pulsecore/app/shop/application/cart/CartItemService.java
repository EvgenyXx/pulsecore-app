package ru.pulsecore.app.shop.application.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.application.product.StockService;
import ru.pulsecore.app.shop.domain.entity.Cart;
import ru.pulsecore.app.shop.domain.entity.CartItem;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;
import ru.pulsecore.app.shop.infrastructure.repository.CartItemRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final StockService stockService;

    @Transactional
    public void addOrIncrement(Cart cart, ProductVariant variant, int quantity) {
        stockService.validateQuantity(quantity);
        stockService.validateStock(variant, quantity);

        cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId())
                .map(existing -> incrementExisting(existing, variant, quantity))
                .orElseGet(() -> createNew(cart, variant, quantity));
    }

    @Transactional
    public void setQuantity(CartItem item, int quantity) {
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return;
        }

        stockService.validateStock(item.getVariant(), quantity);
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void delete(CartItem item) {
        cartItemRepository.delete(item);
    }

    private CartItem createNew(Cart cart, ProductVariant variant, int quantity) {
        CartItem item = CartItem.builder()
                .cart(cart)
                .variant(variant)
                .quantity(quantity)
                .build();
        return cartItemRepository.save(item);
    }

    private CartItem incrementExisting(CartItem existing, ProductVariant variant, int addQuantity) {
        int newQty = existing.getQuantity() + addQuantity;
        stockService.validateStock(variant, newQty);
        existing.setQuantity(newQty);
        return cartItemRepository.save(existing);
    }

}