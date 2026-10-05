package ru.pulsecore.app.shop.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SellerApi {

    public static final String BASE_PATH = "/api/seller";

    // ----- Orders -----
    public static final String ORDERS = "/orders";
    public static final String ORDER = "/orders/{orderId}";
    public static final String ORDER_STATUS = "/orders/{orderId}/status";
    public static final String ORDER_PAYMENT_STATUS = "/orders/{orderId}/payment-status";


    // ----- Products -----
    public static final String PRODUCTS = "/products";
    public static final String PRODUCT = "/products/{productId}";
    public static final String PRODUCTS_BY_CATEGORY = "/products/by-category/{categoryId}";


    // ----- Images -----
    public static final String PRODUCT_IMAGE = "/images/{imageId}";

    // ----- Categories -----
    public static final String CATEGORIES = "/categories";
    public static final String CATEGORY = "/categories/{categoryId}";

    // ----- Upload -----
    public static final String UPLOAD = "/upload";
    public static final String PARAM_FILE = "file";

}