package ru.pulsecore.app.shared.api.payment;



import ru.pulsecore.app.shared.dto.response.PricesResponse;


public interface PaymentAdminClient {

    PricesResponse updatePrices(int oneMonth,int twoMonths);

    PricesResponse getPrices();


}
