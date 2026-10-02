package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.OrderPaidContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;
import ru.pulsecore.app.shop.infrastructure.pdf.OrderReceiptPdfGenerator;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaidMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final OrderReceiptPdfGenerator pdfGenerator;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.ORDER_PAID;
    }

    @Override//todo сделать клиента
    public void send(MailContext ctx) {
        OrderPaidContext c = (OrderPaidContext) ctx;

        String paymentMethodLabel = "ON_DELIVERY".equals(c.paymentMethod())
                ? "при получении"
                : "ЮKassa";

        String deliveryBlock;
        if ("PICKUP".equals(c.deliveryMethod())) {
            deliveryBlock = "Забрать заказ можно по адресу: г. " + c.deliveryCity() + ", " + c.deliveryStreet() + "\n"
                    + "Телефон магазина: " + c.pickupPhone() + "\n"
                    + "Чек прикреплён к этому письму";
        } else {
            deliveryBlock = "Заказ будет доставлен в ПВЗ: г. " + c.deliveryCity() + ", " + c.deliveryStreet() + "\n"
                    + "Как только придёт — пришлём SMS\n"
                    + "Чек прикреплён к этому письму";
        }

        String text = templates.format(
                MailTemplate.ORDER_PAID,
                c.customerFirstName(),
                c.orderId(),
                String.format("%,.0f", c.totalPrice()),
                paymentMethodLabel,
                deliveryBlock
        );

        byte[] pdf = pdfGenerator.generate(c);
        String fileName = "receipt-order-" + c.orderId() + ".pdf";

        mailSender.send(
                MailFormat.PDF,
                c.to(),
                "PulseCore — Чек по заказу №" + c.orderId(),
                text,
                fileName,
                pdf
        );

        log.info("Чек по заказу №{} отправлен на {}", c.orderId(), c.to());
    }
}