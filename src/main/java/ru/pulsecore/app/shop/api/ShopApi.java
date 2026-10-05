package ru.pulsecore.app.shop.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ShopApi {

    public static final String BASE_PATH = "/api/shop";

    // ----- Products -----
    public static final String PRODUCTS = "/products";
    public static final String PRODUCT = "/products/{productId}";
    public static final String PRODUCTS_BY_CATEGORY = "/products/by-category/{categoryId}";


    // ----- Categories -----
    public static final String CATEGORIES = "/categories";

    // ----- Cart -----
    public static final String CART = "/cart";
    public static final String CART_ITEMS = "/cart/items";
    public static final String CART_ITEM = "/cart/items/{itemId}";

    // ----- Orders -----
    public static final String ORDERS = "/orders";
    public static final String ORDERS_ACTIVE = "/orders/active";
    public static final String ORDER = "/orders/{orderId}";

}