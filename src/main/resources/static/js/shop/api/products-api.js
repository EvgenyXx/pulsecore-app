window.ProductsApi = (function () {
    const http = window.Http;

    return {
        getAll: (categoryId) => http.request(
            '/products' + (categoryId ? '?categoryId=' + categoryId : '')
        ),

        getById: (id) => http.request('/products/' + id)
    };
})();