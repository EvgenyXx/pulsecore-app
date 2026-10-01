window.CategoriesPage = (function () {
    const api = window.CategoriesApi;
    const toast = window.Toast;

    async function loadList() {
        const container = document.getElementById('categoriesList');
        try {
            const categories = await api.getAll();

            if (categories.length === 0) {
                container.innerHTML = '<p class="muted">Пока пусто</p>';
                return;
            }

            container.innerHTML = categories.map(c => `
                <div class="category-item">
                    <span>
                        <span class="category-item-name">${c.name}</span>
                        <span class="category-item-id">#${c.id}</span>
                    </span>
                </div>
            `).join('');
        } catch (e) {
            container.innerHTML = `<p class="muted">Ошибка: ${e.message}</p>`;
        }
    }

    async function createCategory() {
        const msg = document.getElementById('categoryMessage');
        const btn = document.getElementById('categorySubmitBtn');
        const input = document.getElementById('categoryName');

        const name = input.value.trim();
        if (!name) return;

        toast.hide(msg);
        btn.disabled = true;
        btn.textContent = 'Создание...';

        try {
            await api.create(name);
            toast.show(msg, 'Категория создана', true);
            input.value = '';
            await loadList();
            if (window.ProductNewPage) await window.ProductNewPage.reloadCategories();
        } catch (e) {
            toast.show(msg, e.message, false);
        } finally {
            btn.disabled = false;
            btn.textContent = 'Создать категорию';
        }
    }

    return { loadList, createCategory };
})();