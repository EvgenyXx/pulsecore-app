window.ProductEditPage = (function () {
    const api = window.ProductsApi;
    const categoriesApi = window.CategoriesApi;
    const toast = window.Toast;
    const upload = window.Upload;

    let currentId = null;

    function init() {
        upload.init('editImagesInput', 'editPreviewGrid');
        bindDeleteBtn();
    }

    function bindDeleteBtn() {
        const btn = document.getElementById('editDeleteBtn');
        if (!btn || btn.dataset.bound) return;
        btn.dataset.bound = '1';

        btn.addEventListener('click', () => {
            if (!currentId) return;

            openConfirm({
                title: 'Удалить товар?',
                text: 'Это действие нельзя отменить. Все фото и данные будут удалены.',
                okText: 'Удалить',
                onConfirm: async () => {
                    btn.disabled = true;
                    btn.textContent = 'Удаление...';

                    try {
                        await api.delete(currentId);
                        toast.show(document.getElementById('editMessage'), 'Товар удалён', true);
                        setTimeout(() => {
                            window.location.hash = '#/';
                        }, 600);
                    } catch (e) {
                        toast.show(document.getElementById('editMessage'), e.message, false);
                        btn.disabled = false;
                        btn.textContent = 'Удалить товар';
                    }
                }
            });
        });
    }

    function openConfirm({ title, text, okText, onConfirm }) {
        const modal = document.getElementById('confirmModal');
        const titleEl = document.getElementById('confirmModalTitle');
        const textEl = document.getElementById('confirmModalText');
        const okBtn = document.getElementById('confirmModalOk');
        const closeEls = modal.querySelectorAll('[data-confirm-close]');

        titleEl.textContent = title;
        textEl.textContent = text;
        okBtn.textContent = okText;

        modal.classList.remove('hidden');

        function close() {
            modal.classList.add('hidden');
            okBtn.removeEventListener('click', handleOk);
            closeEls.forEach(el => el.removeEventListener('click', close));
        }

        function handleOk() {
            close();
            if (onConfirm) onConfirm();
        }

        okBtn.addEventListener('click', handleOk);
        closeEls.forEach(el => el.addEventListener('click', close));
    }

    async function load(id) {
        init();
        currentId = id;

        const msg = document.getElementById('editMessage');
        toast.hide(msg);

        // Сброс кнопки удаления
        const delBtn = document.getElementById('editDeleteBtn');
        if (delBtn) {
            delBtn.disabled = false;
            delBtn.textContent = 'Удалить товар';
        }

        try {
            const [product, categories] = await Promise.all([
                api.getById(id),
                categoriesApi.getAll()
            ]);

            document.getElementById('editTitle').textContent = product.name || 'Редактирование';
            document.getElementById('editName').value = product.name || '';
            document.getElementById('editBrand').value = product.brand || '';
            document.getElementById('editDescription').value = product.description || '';
            document.getElementById('editPrice').value = product.price || '';
            document.getElementById('editStock').value = product.stock || 0;

            const select = document.getElementById('editCategoryId');
            select.innerHTML = '';
            categories.forEach(c => {
                const opt = document.createElement('option');
                opt.value = c.id;
                opt.textContent = c.name;
                if (String(c.id) === String(product.categoryId)) opt.selected = true;
                select.appendChild(opt);
            });

            upload.setFiles((product.images || []).map(img => ({
                id: img.id,
                url: img.url
            })));

        } catch (e) {
            toast.show(msg, 'Ошибка загрузки: ' + e.message, false);
        }
    }

    async function save() {
        const msg = document.getElementById('editMessage');
        const btn = document.getElementById('editSubmitBtn');

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

            btn.textContent = 'Сохранение...';

            const body = {
                name: document.getElementById('editName').value.trim(),
                brand: document.getElementById('editBrand').value.trim() || null,
                description: document.getElementById('editDescription').value.trim() || null,
                price: parseFloat(document.getElementById('editPrice').value),
                stock: parseInt(document.getElementById('editStock').value),
                categoryId: parseInt(document.getElementById('editCategoryId').value),
                images: files.map((item, i) => ({
                    url: item.uploadedUrl,
                    sortOrder: i,
                    main: i === 0
                }))
            };

            await api.update(currentId, body);
            toast.show(msg, 'Сохранено', true);
        } catch (e) {
            toast.show(msg, e.message, false);
        } finally {
            btn.disabled = false;
            btn.textContent = 'Сохранить';
        }
    }

    return { load, save };
})();