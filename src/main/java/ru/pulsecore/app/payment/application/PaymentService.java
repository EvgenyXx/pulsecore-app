package ru.pulsecore.app.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.payment.domain.Payment;
import ru.pulsecore.app.payment.infrastructure.PaymentRepository;


import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void save(UUID playerId, BigDecimal amount, Integer months) {
        Payment payment = Payment.builder()
                .playerId(playerId)
                .amount(amount)
                .months(months)
                .build();
        paymentRepository.save(payment);
    }
}