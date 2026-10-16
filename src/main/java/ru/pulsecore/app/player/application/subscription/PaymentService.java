package ru.pulsecore.app.player.application.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.domain.Payment;
import ru.pulsecore.app.player.infrastructure.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public boolean existsByPaymentId(String paymentId) {
        return paymentRepository.existsByPaymentId(paymentId);
    }

    public void save(UUID playerId,
                     BigDecimal amount,
                     Integer months,
                     UUID subscriptionId,
                     String paymentId,
                     LocalDateTime paidAt) {
        Payment payment = Payment.builder()
                .playerId(playerId)
                .amount(amount)
                .months(months)
                .subscriptionId(subscriptionId)
                .paymentId(paymentId)
                .paidAt(paidAt)
                .build();
        paymentRepository.save(payment);
    }
}