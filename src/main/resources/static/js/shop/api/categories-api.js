window.CategoriesApi = (function () {
    const http = window.Http;

    return {
        getAll: () => http.request('/categories')
    };
})();