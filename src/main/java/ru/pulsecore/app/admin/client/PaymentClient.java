package ru.pulsecore.app.admin.client;



import ru.pulsecore.app.shared.dto.response.PricesResponse;


public interface PaymentClient {

    PricesResponse updatePrices(int oneMonth,int twoMonths);

    PricesResponse getPrices();


}
