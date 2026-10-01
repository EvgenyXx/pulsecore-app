package ru.pulsecore.app.shop.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SellerApi {

    public static final String BASE_PATH = "/api/seller";

    public static final String ORDERS = "/orders";
    public static final String ORDER = "/{orderId}";
    public static final String ORDER_STATUS = "/{orderId}/status";
}