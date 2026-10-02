package ru.pulsecore.app.shop.infrastructure.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.pulsecore.app.notification.application.mail.context.OrderPaidContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderReceiptPdfGenerator {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TemplateEngine templateEngine;

    public byte[] generate(OrderPaidContext c) {
        // 1. Собираем переменные для шаблона
        Context ctx = new Context();
        ctx.setVariable("orderId", c.orderId());
        ctx.setVariable("date", LocalDateTime.now().format(DATE_FMT));
        ctx.setVariable("customerName", fullName(c));
        ctx.setVariable("email", c.to());
        ctx.setVariable("items", mapItems(c));
        ctx.setVariable("totalPrice", formatPrice(c.totalPrice()));
        ctx.setVariable("paymentLabel", paymentLabel(c.paymentMethod()));
        ctx.setVariable("deliveryLabel", "PICKUP".equals(c.deliveryMethod())
                ? "Самовывоз" : "СДЭК · ПВЗ");
        ctx.setVariable("deliveryAddress", "г. " + nullSafe(c.deliveryCity())
                + ", " + nullSafe(c.deliveryStreet()));
        ctx.setVariable("pickupPhone", "PICKUP".equals(c.deliveryMethod())
                ? c.pickupPhone() : null);

        // 2. Рендерим HTML
        String html = templateEngine.process("order-receipt", ctx);

        // 3. HTML → PDF
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);

            // Шрифт для кириллицы
            builder.useFont(
                    () -> {
                        try {
                            return new ClassPathResource("fonts/DejaVuSans.ttf").getInputStream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    "DejaVu Sans"
            );
            builder.useFont(
                    () -> {
                        try {
                            return new ClassPathResource("fonts/DejaVuSans-Bold.ttf").getInputStream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    "DejaVu Sans",
                    700,
                    PdfRendererBuilder.FontStyle.NORMAL,
                    true
            );

            builder.toStream(out);
            builder.run();

            return out.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка генерации PDF чека для заказа {}", c.orderId(), e);
            throw new RuntimeException("Не удалось сгенерировать чек", e);
        }
    }

    // ===== helpers =====

    private List<Map<String, Object>> mapItems(OrderPaidContext c) {
        return c.items().stream()
                .map(i -> Map.<String, Object>of(
                        "name", i.productName(),
                        "quantity", i.quantity(),
                        "total", formatPrice(i.price().multiply(BigDecimal.valueOf(i.quantity())))
                ))
                .toList();
    }

    private String fullName(OrderPaidContext c) {
        StringBuilder sb = new StringBuilder();
        if (c.customerLastName() != null)  sb.append(c.customerLastName()).append(' ');
        if (c.customerFirstName() != null) sb.append(c.customerFirstName());
        String s = sb.toString().trim();
        return s.isEmpty() ? "—" : s;
    }

    private String paymentLabel(String method) {
        if ("ON_DELIVERY".equals(method)) return "при получении (нал / СБП)";
        if ("YOOKASSA".equals(method))    return "ЮKassa";
        return "—";
    }

    private String nullSafe(String s) {
        return s == null ? "—" : s;
    }

    private String formatPrice(BigDecimal v) {
        return String.format("%,.0f ₽", v);
    }
}