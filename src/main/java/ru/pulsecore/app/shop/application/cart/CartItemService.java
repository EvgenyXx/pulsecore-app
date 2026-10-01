package ru.pulsecore.app.shop.application.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.domain.Cart;
import ru.pulsecore.app.shop.domain.CartItem;
import ru.pulsecore.app.shop.domain.Product;
import ru.pulsecore.app.shop.infrastructure.exception.CartItemException;
import ru.pulsecore.app.shop.infrastructure.repository.CartItemRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartItem addOrIncrement(Cart cart, Product product, int quantity) {
        validateQuantity(quantity);
        validateStock(product, quantity);

        return cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .map(existing -> incrementExisting(existing, product, quantity))
                .orElseGet(() -> createNew(cart, product, quantity));
    }

    @Transactional
    public CartItem setQuantity(CartItem item, int quantity) {
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        validateStock(item.getProduct(), quantity);
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    @Transactional
    public void delete(CartItem item) {
        cartItemRepository.delete(item);
    }

    private CartItem createNew(Cart cart, Product product, int quantity) {
        CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
        return cartItemRepository.save(item);
    }

    private CartItem incrementExisting(CartItem existing, Product product, int addQuantity) {
        int newQty = existing.getQuantity() + addQuantity;
        validateStock(product, newQty);
        existing.setQuantity(newQty);
        return cartItemRepository.save(existing);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new CartItemException("Количество должно быть больше 0");
        }
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new CartItemException(
                    "Недостаточно товара на складе: доступно " + product.getStock()
            );
        }
    }
}