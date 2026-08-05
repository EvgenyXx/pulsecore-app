package ru.pulsecore.app.modules.payment_modules.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.admin_modules.infrastructure.clinet.PaymentClient;
import ru.pulsecore.app.modules.payment_modules.application.PriceService;
import ru.pulsecore.app.modules.shared.dto.PricesResponse;

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
