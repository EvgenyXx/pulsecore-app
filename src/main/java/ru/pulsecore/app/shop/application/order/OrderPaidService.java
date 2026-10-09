package ru.pulsecore.app.shop.application.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.OrderPaidContext;
import ru.pulsecore.app.shared.dispetcher.MailDispatcher;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.shop.application.product.StockService;
import ru.pulsecore.app.shop.domain.entity.Order;
import ru.pulsecore.app.shop.domain.entity.OrderItem;
import ru.pulsecore.app.shop.domain.OrderStatus;
import ru.pulsecore.app.shop.domain.PaymentStatus;
import ru.pulsecore.app.shop.infrastructure.client.PlayerClient;
import ru.pulsecore.app.shop.infrastructure.config.ShopProperties;
import ru.pulsecore.app.shop.infrastructure.exception.OrderException;
import ru.pulsecore.app.shop.infrastructure.exception.OrderNotFoundException;
import ru.pulsecore.app.shop.infrastructure.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaidService {

    private final StockService stockService;
    private final OrderRepository orderRepository;
    private final MailDispatcher mailDispatcher;
    private final PlayerClient playerClient;
    private final ShopProperties properties;

    @Transactional
    public void markPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            log.info("Заказ уже оплачен №: {}", order.getId());
            return ;
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderException("Заказ отменён — оплату нельзя подтвердить");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);

        //todo добавить поле для сохранение юкасса айди платежа что бы потом можно быдо оформлять отмену заказа
        //todo что бы пользователю возвращались средства
        stockService.decreaseForOrder(order);
        Order saved = orderRepository.save(order);

        sendEvent(saved);

    }

    private void sendEvent(Order order) {
        PlayerData playerData = playerClient.getPlayer(order.getUserId());

        mailDispatcher.send(
                playerData.email(),
                new MailContent(
                        MailTypes.ORDER_PAID,
                        new OrderPaidContext(
                                playerData.email(),
                                order.getCustomerFirstName(),
                                order.getCustomerLastName(),
                                order.getId(),
                                order.getTotalPrice(),
                                order.getPaymentMethod().name(),
                                mapItems(order),
                                order.getDeliveryMethod().name(),
                                order.getDeliveryCity(),
                                order.getDeliveryStreet(),
                                properties.getPickup().getPhone()
                        )
                )
        );
    }

    private List<OrderPaidContext.Item> mapItems(Order order) {
        List<OrderPaidContext.Item> items = new ArrayList<>();
        for (OrderItem orderItem : order.getItems()) {
            items.add(new OrderPaidContext.Item(
                    orderItem.getProductName(),
                    orderItem.getQuantity(),
                    orderItem.getProductPrice()
            ));
        }
        return items;
    }
}