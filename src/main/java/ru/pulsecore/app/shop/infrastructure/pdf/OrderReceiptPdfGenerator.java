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
import java.io.InputStream;
import java.io.UncheckedIOException;
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

    private static final String FONT_REGULAR = "fonts/DejaVuSans.ttf";
    private static final String FONT_BOLD    = "fonts/DejaVuSans-Bold.ttf";
    private static final String FONT_FAMILY  = "DejaVu Sans";

    private final TemplateEngine templateEngine;

    public byte[] generate(OrderPaidContext c) {
        Context ctx = buildContext(c);
        String html = templateEngine.process("order-receipt", ctx);
        return renderPdf(html, c.orderId());
    }

    // ===== 1. Контекст для шаблона =====

    private Context buildContext(OrderPaidContext c) {
        Context ctx = new Context();
        ctx.setVariable("orderId", c.orderId());
        ctx.setVariable("date", LocalDateTime.now().format(DATE_FMT));
        ctx.setVariable("customerName", fullName(c));
        ctx.setVariable("email", c.to());
        ctx.setVariable("items", mapItems(c));
        ctx.setVariable("totalPrice", formatPrice(c.totalPrice()));
        ctx.setVariable("paymentLabel", paymentLabel(c.paymentMethod()));
        ctx.setVariable("deliveryLabel", deliveryLabel(c.deliveryMethod()));
        ctx.setVariable("deliveryAddress", deliveryAddress(c));
        ctx.setVariable("pickupPhone", pickupPhone(c));
        return ctx;
    }

    private List<Map<String, Object>> mapItems(OrderPaidContext c) {
        return c.items().stream()
                .map(i -> Map.<String, Object>of(
                        "name", i.productName(),
                        "quantity", i.quantity(),
                        "total", formatPrice(lineTotal(i))
                ))
                .toList();
    }

    private BigDecimal lineTotal(OrderPaidContext.Item i) {
        return i.price().multiply(BigDecimal.valueOf(i.quantity()));
    }

    // ===== 2. HTML → PDF =====

    private byte[] renderPdf(String html, Long orderId) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            registerFonts(builder);

            builder.toStream(out);
            builder.run();

            return out.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка генерации PDF чека для заказа {}", orderId, e);
            throw new RuntimeException("Не удалось сгенерировать чек", e);
        }
    }

    private void registerFonts(PdfRendererBuilder builder) {
        builder.useFont(() -> openFont(FONT_REGULAR), FONT_FAMILY);
        builder.useFont(
                () -> openFont(FONT_BOLD),
                FONT_FAMILY,
                700,
                PdfRendererBuilder.FontStyle.NORMAL,
                true
        );
    }

    private InputStream openFont(String path) {
        try {
            return new ClassPathResource(path).getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException("Шрифт не найден: " + path, e);
        }
    }

    // ===== 3. Helpers для контекста =====

    private String fullName(OrderPaidContext c) {
        StringBuilder sb = new StringBuilder();
        if (c.customerLastName() != null)  sb.append(c.customerLastName()).append(' ');
        if (c.customerFirstName() != null) sb.append(c.customerFirstName());
        String s = sb.toString().trim();
        return s.isEmpty() ? "—" : s;
    }

    private String deliveryLabel(String method) {
        return "PICKUP".equals(method) ? "Самовывоз" : "СДЭК · ПВЗ";
    }

    private String deliveryAddress(OrderPaidContext c) {
        return "г. " + nullSafe(c.deliveryCity()) + ", " + nullSafe(c.deliveryStreet());
    }

    private String pickupPhone(OrderPaidContext c) {
        return "PICKUP".equals(c.deliveryMethod()) ? c.pickupPhone() : null;
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