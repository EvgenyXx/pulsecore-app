window.CategoriesApi = (function () {
    const http = window.SellerHttp;

    return {
        getAll: () => http.request('/categories'),

        create: (name) => http.request('/categories', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name })
        }),

        delete: (id) => http.request('/categories/' + id, {
            method: 'DELETE'
        })
    };
})();