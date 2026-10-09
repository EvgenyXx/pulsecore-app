window.CartApi = (function () {
    const http = window.Http;

    return {
        getCart: () => http.request('/cart'),

        addItem: (variantId, quantity) => http.request('/cart/items', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ variantId, quantity })
        }),

        updateItem: (itemId, quantity) => http.request('/cart/items/' + itemId, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quantity })
        }),

        removeItem: (itemId) => http.request('/cart/items/' + itemId, {
            method: 'DELETE'
        }),

        clear: () => http.request('/cart', {
            method: 'DELETE'
        })
    };
})();