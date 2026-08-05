package ru.pulsecore.app.modules.admin.infrastructure.clinet;



import ru.pulsecore.app.modules.shared.dto.PricesResponse;


public interface PaymentClient {

    PricesResponse updatePrices(int oneMonth,int twoMonths);


}
