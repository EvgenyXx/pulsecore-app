package ru.pulsecore.app.shop.application.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.CreateOrderRequest;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.api.dto.response.SellerOrderDto;
import ru.pulsecore.app.shop.application.assembler.OrderAssembler;
import ru.pulsecore.app.shop.application.assembler.OrderItemAssembler;
import ru.pulsecore.app.shop.application.mapping.OrderMapper;
import ru.pulsecore.app.shop.application.payment.OrderPaymentResolver;
import ru.pulsecore.app.shop.application.product.StockService;
import ru.pulsecore.app.shop.domain.*;
import ru.pulsecore.app.shop.infrastructure.exception.OrderException;
import ru.pulsecore.app.shop.infrastructure.exception.OrderNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.CartItemRepository;
import ru.pulsecore.app.shop.infrastructure.repository.OrderRepository;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderAssembler orderAssembler;
    private final OrderItemAssembler orderItemAssembler;
    private final OrderValidator orderValidator;
    private final StockService stockService;
    private final OrderPaymentResolver paymentResolver;

    @Transactional
    public void markPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        stockService.decreaseForOrder(order);
    }

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

    // ===== SELLER =====

    @Transactional(readOnly = true)
    public List<SellerOrderDto> getAllOrders(OrderStatus statusFilter) {
        List<Order> orders = (statusFilter == null)
                ? orderRepository.findAllByOrderByCreatedAtDesc()
                : orderRepository.findByStatusOrderByCreatedAtDesc(statusFilter);

        return orders.stream()
                .map(orderMapper::toSellerDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SellerOrderDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toSellerDto(order);
    }

    @Transactional
    public SellerOrderDto updatePaymentStatus(Long orderId,PaymentStatus paymentStatus){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderException("Заказ отменён — статус нельзя менять");
        }
        order.setPaymentStatus(paymentStatus);
        log.info("Статус заказа №{} изменен на {}",order.getId(),paymentStatus);

        Order save = orderRepository.save(order);
        stockService.decreaseForOrder(order);
        return orderMapper.toSellerDto(save);
    }

    @Transactional
    public SellerOrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderException("Заказ отменён — статус нельзя менять");
        }

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);

        log.info("Заказ id={} → статус {}", orderId, newStatus);

        return orderMapper.toSellerDto(saved);
    }
}