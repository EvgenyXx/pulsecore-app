package ru.pulsecore.app.shared.api.payment;

import ru.pulsecore.app.shared.dto.response.PricesResponse;

/**
 * Клиент payment-модуля для admin (контракт).
 * Реализация — PaymentAdminClientImpl (payment/api/internal).
 */
public interface PaymentAdminClient {

    /** Обновить цены. @throws BaseException 400 при цене <= 0 */
    PricesResponse updatePrices(int oneMonth, int twoMonths);

    /** Текущие цены. Идемпотентно. */
    PricesResponse getPrices();
}