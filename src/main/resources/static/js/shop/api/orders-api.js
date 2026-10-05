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

        // Все заказы юзера, с пагинацией
        getMyOrders: (page = 0, size = 20) =>
            http.request('/orders?page=' + page + '&size=' + size),

        getById: (id) => http.request('/orders/' + id)
    };
})();