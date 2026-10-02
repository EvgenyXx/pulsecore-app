package ru.pulsecore.app.shop.application.order;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.CreateOrderRequest;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.application.assembler.OrderAssembler;
import ru.pulsecore.app.shop.application.assembler.OrderItemAssembler;
import ru.pulsecore.app.shop.application.mapping.OrderMapper;
import ru.pulsecore.app.shop.application.payment.OrderPaymentResolver;
import ru.pulsecore.app.shop.domain.CartItem;
import ru.pulsecore.app.shop.domain.Order;
import ru.pulsecore.app.shop.domain.OrderItem;
import ru.pulsecore.app.shop.infrastructure.exception.OrderNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.CartItemRepository;
import ru.pulsecore.app.shop.infrastructure.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderUserService {

    private final OrderValidator orderValidator;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartItemRepository cartItemRepository;
    private final OrderPaymentResolver paymentResolver;
    private final OrderAssembler orderAssembler;
    private final OrderItemAssembler orderItemAssembler;


    @Transactional
    public OrderDto createOrder(UUID userId, CreateOrderRequest request) {
        orderValidator.validateRequest(request);

        List<CartItem> selected = cartItemRepository.findAllById(request.itemIds());
        orderValidator.validate(userId, selected);

        List<OrderItem> items = selected.stream()
                .map(orderItemAssembler::toOrderItem)
                .toList();

        Order order = orderAssembler.toOrder(userId, request, items);
        Order saved = orderRepository.save(order);

        cartItemRepository.deleteAll(selected);

        log.info("Создан заказ id={}, user={}, total={}, payment={}",
                saved.getId(), userId, saved.getTotalPrice(), saved.getPaymentMethod());

        return paymentResolver.pay(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDto getUserOrder(UUID userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toDto(order);
    }
}
