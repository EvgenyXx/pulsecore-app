package ru.pulsecore.app.payment.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.admin.infrastructure.clinet.PaymentClient;
import ru.pulsecore.app.payment.application.PriceService;
import ru.pulsecore.app.shared.dto.PricesResponse;

@Service
@RequiredArgsConstructor
public class AdminPaymentClientImp implements PaymentClient {

    private final PriceService priceService;

    @Override
    @Transactional
    public PricesResponse updatePrices(int oneMonth, int twoMonths) {
        priceService.update(oneMonth, twoMonths);
        return priceService.getPricesData();
    }

}
