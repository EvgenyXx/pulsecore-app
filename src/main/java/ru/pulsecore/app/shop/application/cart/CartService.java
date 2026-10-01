package ru.pulsecore.app.shop.application.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.AddCartItemRequest;
import ru.pulsecore.app.shop.api.dto.response.CartDto;
import ru.pulsecore.app.shop.application.mapping.CartMapper;
import ru.pulsecore.app.shop.domain.Cart;
import ru.pulsecore.app.shop.domain.CartItem;
import ru.pulsecore.app.shop.domain.Product;
import ru.pulsecore.app.shop.infrastructure.exception.CartItemNotFoundException;
import ru.pulsecore.app.shop.infrastructure.exception.CartNotFoundException;
import ru.pulsecore.app.shop.infrastructure.exception.ProductNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.CartItemRepository;
import ru.pulsecore.app.shop.infrastructure.repository.CartRepository;
import ru.pulsecore.app.shop.infrastructure.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemService cartItemService;
    private final CartMapper cartMapper;

    @Transactional
    public CartDto getOrCreate(UUID userId) {
        return cartRepository.findByUserId(userId)
                .map(cartMapper::toDto)
                .orElseGet(() -> cartMapper.toDto(createCart(userId)));
    }


    public Cart getByUserId(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(CartNotFoundException::new);
    }

    @Transactional
    public CartDto addItem(UUID userId, AddCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        cartItemService.addOrIncrement(cart, product, request.quantity());

        return reloadCartDto(cart.getId());
    }

    @Transactional
    public CartDto updateItem(UUID userId, Long itemId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(CartNotFoundException::new);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new CartItemNotFoundException(itemId);
        }

        cartItemService.setQuantity(item, quantity);

        return reloadCartDto(cart.getId());
    }

    @Transactional
    public void removeItem(UUID userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(CartNotFoundException::new);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new CartItemNotFoundException(itemId);
        }

        cartItemService.delete(item);
    }

    @Transactional
    public Cart createCart(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    private CartDto reloadCartDto(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(CartNotFoundException::new);
        return cartMapper.toDto(cart);
    }
}