window.OrdersApi = (function () {
    const http = window.Http;

    return {
        create: (body) => http.request('/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        }),

        getMyOrders: () => http.request('/orders'),

        getById: (id) => http.request('/orders/' + id)
    };
})();