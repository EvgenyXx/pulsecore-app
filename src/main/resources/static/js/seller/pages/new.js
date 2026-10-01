window.ProductNewPage = (function () {
    const api = window.ProductsApi;
    const categoriesApi = window.CategoriesApi;
    const toast = window.Toast;
    const upload = window.Upload;

    let initialized = false;

    function init() {
        if (initialized) return;
        initialized = true;

        upload.init('imagesInput', 'previewGrid');
        reloadCategories();
    }

    async function reloadCategories() {
        const select = document.getElementById('categoryId');
        try {
            const categories = await categoriesApi.getAll();
            select.innerHTML = '<option value="">— Выберите —</option>';
            categories.forEach(c => {
                const opt = document.createElement('option');
                opt.value = c.id;
                opt.textContent = c.name;
                select.appendChild(opt);
            });
        } catch (e) {
            select.innerHTML = '<option value="">Ошибка загрузки</option>';
        }
    }

    async function createProduct() {
        const msg = document.getElementById('message');
        const btn = document.getElementById('submitBtn');

        toast.hide(msg);
        btn.disabled = true;
        btn.textContent = 'Загрузка фото...';

        try {
            const files = upload.getFiles();

            for (const item of files) {
                if (item.uploadedUrl) continue;
                const data = await api.upload(item.file);
                item.uploadedUrl = data.url;
            }

            btn.textContent = 'Создание...';

            const body = {
                name: document.getElementById('name').value.trim(),
                brand: document.getElementById('brand').value.trim() || null,
                description: document.getElementById('description').value.trim() || null,
                price: parseFloat(document.getElementById('price').value),
                stock: parseInt(document.getElementById('stock').value),
                categoryId: parseInt(document.getElementById('categoryId').value),
                images: files.map((item, i) => ({
                    url: item.uploadedUrl,
                    sortOrder: i,
                    main: i === 0
                }))
            };

            await api.create(body);
            toast.show(msg, 'Продукт создан', true);
            resetForm();

            setTimeout(() => {
                window.location.hash = '#/';
            }, 600);

        } catch (e) {
            toast.show(msg, e.message, false);
        } finally {
            btn.disabled = false;
            btn.textContent = 'Создать продукт';
        }
    }

    function resetForm() {
        document.getElementById('productForm').reset();
        upload.reset(document.getElementById('previewGrid'));
    }

    return { init, createProduct, reloadCategories };
})();