package ru.pulsecore.app.shop.application.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.AddCartItemRequest;
import ru.pulsecore.app.shop.api.dto.response.CartDto;
import ru.pulsecore.app.shop.application.mapping.CartMapper;
import ru.pulsecore.app.shop.domain.entity.Cart;
import ru.pulsecore.app.shop.domain.entity.CartItem;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;
import ru.pulsecore.app.shop.infrastructure.exception.CartItemNotFoundException;
import ru.pulsecore.app.shop.infrastructure.exception.CartNotFoundException;
import ru.pulsecore.app.shop.infrastructure.exception.ProductVariantNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.CartItemRepository;
import ru.pulsecore.app.shop.infrastructure.repository.CartRepository;
import ru.pulsecore.app.shop.infrastructure.repository.ProductVariantRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemService cartItemService;
    private final CartMapper cartMapper;

    @Transactional
    public CartDto getOrCreate(UUID userId) {
        return cartRepository.findByUserId(userId)
                .map(cartMapper::toDto)
                .orElseGet(() -> cartMapper.toDto(createCart(userId)));
    }


    @Transactional
    public CartDto addItem(UUID userId, AddCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        ProductVariant variant = productVariantRepository.findById(request.variantId())
                .orElseThrow(() -> new ProductVariantNotFoundException(request.variantId()));

        cartItemService.addOrIncrement(cart, variant, request.quantity());

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