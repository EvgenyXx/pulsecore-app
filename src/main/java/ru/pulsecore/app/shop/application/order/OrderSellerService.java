package ru.pulsecore.app.shop.application.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.response.SellerOrderDto;
import ru.pulsecore.app.shop.application.mapping.OrderMapper;
import ru.pulsecore.app.shop.domain.*;
import ru.pulsecore.app.shop.infrastructure.exception.OrderException;
import ru.pulsecore.app.shop.infrastructure.exception.OrderNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.OrderRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSellerService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderPaidService orderPaidService;


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
    public SellerOrderDto updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {

        if (paymentStatus == PaymentStatus.PAID) {
            Order paid = orderPaidService.markPaid(orderId);
            return orderMapper.toSellerDto(paid);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderException("Заказ отменён — статус нельзя менять");
        }
        order.setPaymentStatus(paymentStatus);
        log.info("Статус заказа №{} изменен на {}", order.getId(), paymentStatus);

         Order saved = orderRepository.save(order);

        return orderMapper.toSellerDto(saved);
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