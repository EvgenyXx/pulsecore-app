package ru.pulsecore.app.payment.api;

public final class PaymentApi {

    private PaymentApi() {}

    public static final String BASE_PATH = "/api/payment";


    /**
     * Оплата и подписка.
     * PRICES — /payment/prices, цены.
     * PAY — /payment/pay, оплата.
     * WEBHOOK — /payment/webhook, вебхук.
     * Payment.
     */
    public static final String PRICES = "/prices";
    public static final String PAY = "/pay";
    public static final String WEBHOOK = "/webhook";
}