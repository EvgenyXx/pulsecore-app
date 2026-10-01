package ru.pulsecore.app.shop.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ShopApi {

    public static final String BASE_PATH = "/api/shop";

    public static final String CREATE_PRODUCT = "/products";
    public static final String PRODUCTS = "/products";
    public static final String GET_PRODUCT = "/products/{productId}";
    public static final String PRODUCT = "/products/{productId}";

    public static final String PRODUCT_IMAGE = "/images/{imageId}";

    public static final String UPLOAD = "/upload";
    public static final String PARAM_FILE = "file";

    public static final String CATEGORIES = "/categories";

    public static final String CART = "/cart";
    public static final String CART_ITEMS = "/items";
    public static final String CART_ITEM = "/items/{itemId}";

    public static final String ORDERS = "/orders";
    public static final String ORDER = "/orders/{orderId}";


}