window.ProductsApi = (function () {
    const http = window.Http;

    return {
        getAll: () => http.request('/products'),

        getByCategory: (categoryId) => http.request('/products/by-category/' + categoryId),

        getById: (id) => http.request('/products/' + id)
    };
})();