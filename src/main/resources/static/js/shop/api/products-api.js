window.ProductsApi = (function () {
    const http = window.Http;

    return {
        getAll: (page = 0, size = 20) =>
            http.request('/products?page=' + page + '&size=' + size),

        getByCategory: (categoryId, page = 0, size = 20) =>
            http.request('/products/by-category/' + categoryId + '?page=' + page + '&size=' + size),

        getById: (id) => http.request('/products/' + id),

        search: (query, page = 0, size = 20) =>
            http.request('/products/search?q=' + encodeURIComponent(query) + '&page=' + page + '&size=' + size)
    };
})();