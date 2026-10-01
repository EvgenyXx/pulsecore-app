window.CategoriesPage = (function () {
    const categoriesApi = window.CategoriesApi;

    async function init() {
        const container = document.getElementById('categoriesList');
        if (!container) return;

        container.innerHTML = '<p class="muted">Загрузка...</p>';

        try {
            const categories = await categoriesApi.getAll();
            if (!categories || categories.length === 0) {
                container.innerHTML = '<p class="muted">Категорий нет</p>';
                return;
            }

            container.innerHTML = categories.map(c => `
                <a class="category-item" href="#/category/${c.id}">
                    <span>${escapeHtml(c.name)}</span>
                    <span class="category-item-arrow">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
                    </span>
                </a>
            `).join('');
        } catch (e) {
            container.innerHTML = '<p class="muted">Ошибка: ' + e.message + '</p>';
        }
    }

    function escapeHtml(s) {
        return String(s).replace(/[&<>"']/g, ch => ({
            '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
        }[ch]));
    }

    return { init };
})();