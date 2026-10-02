window.OrdersApi = (function () {
    const http = window.SellerHttp;

    return {
        getAll: (status) => http.request(
            '/orders' + (status && status !== 'all' ? '?status=' + status : '')
        ),

        getById: (id) => http.request('/orders/' + id),

        updateStatus: (id, status) => http.request('/orders/' + id + '/status', {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status })
        }),

        updatePaymentStatus: (id, paymentStatus) => http.request('/orders/' + id + '/payment-status', {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ paymentStatus })
        })
    };
})();