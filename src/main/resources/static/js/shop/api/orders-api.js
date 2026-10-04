window.OrdersApi = (function () {
    const http = window.Http;

    return {
        create: (body) => http.request('/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        }),

        // Активные: CONFIRMED + ASSEMBLED
        getActive: () => http.request('/orders/active'),

        // Все заказы юзера (включая DONE, CANCELLED, SHIPPED)
        getMyOrders: () => http.request('/orders'),

        getById: (id) => http.request('/orders/' + id)
    };
})();