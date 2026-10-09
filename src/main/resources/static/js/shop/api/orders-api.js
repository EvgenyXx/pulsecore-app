window.OrdersApi = (function () {
    const http = window.Http;

    return {
        create: (body) => http.request('/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        }),

        // Проверка корзины перед оформлением. 200 — ок, 400 — problems[]
        validate: (itemIds) => http.request('/orders/validate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itemIds })
        }),

        // Активные: CONFIRMED + ASSEMBLED
        getActive: () => http.request('/orders/active'),

        // Все заказы юзера, с пагинацией
        getMyOrders: (page = 0, size = 20) =>
            http.request('/orders?page=' + page + '&size=' + size),

        getById: (id) => http.request('/orders/' + id)
    };
})();