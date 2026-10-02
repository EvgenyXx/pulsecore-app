window.ProductsApi = (function () {
    const http = window.SellerHttp;

    return {
        getAll: (categoryId) => http.request(
            '/products' + (categoryId ? '?categoryId=' + categoryId : '')
        ),

        getById: (id) => http.request('/products/' + id),

        create: (body) => http.request('/products', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        }),

        update: (id, body) => http.request('/products/' + id, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        }),

        delete: (id) => http.request('/products/' + id, {
            method: 'DELETE'
        }),

        upload: async (file) => {
            const formData = new FormData();
            formData.append('file', file);

            const res = await fetch('/api/seller/upload', {
                method: 'POST',
                body: formData,
                credentials: 'same-origin'
            });

            if (!res.ok) throw new Error('Ошибка загрузки фото');
            return res.json();
        },

        deleteImage: (imageId) => http.request('/images/' + imageId, {
            method: 'DELETE'
        })
    };
})();