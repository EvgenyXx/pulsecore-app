package ru.pulsecore.app.admin.infrastructure.clinet;



import ru.pulsecore.app.shared.dto.PricesResponse;


public interface PaymentClient {

    PricesResponse updatePrices(int oneMonth,int twoMonths);


}
