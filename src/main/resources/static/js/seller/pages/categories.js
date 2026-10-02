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
                <div class="category-item" data-id="${c.id}">
                    <span class="category-item-info">
                        <span class="category-item-name">${escapeHtml(c.name)}</span>
                        <span class="category-item-id">#${c.id}</span>
                    </span>
                    <button type="button" class="category-item-delete" data-action="delete" data-id="${c.id}" title="Удалить">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                            <path d="M10 11v6M14 11v6"/>
                            <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
                        </svg>
                    </button>
                </div>
            `).join('');

            bindDelete(container);
        } catch (e) {
            container.innerHTML = `<p class="muted">Ошибка: ${e.message}</p>`;
        }
    }

    function bindDelete(container) {
        container.querySelectorAll('[data-action="delete"]').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                const name = btn.closest('.category-item').querySelector('.category-item-name').textContent;

                openConfirm({
                    title: 'Удалить категорию?',
                    text: `«${name}» будет удалена. Это действие нельзя отменить.`,
                    okText: 'Удалить',
                    onConfirm: async () => {
                        btn.disabled = true;
                        try {
                            await api.delete(id);
                            await loadList();
                            if (window.ProductNewPage) await window.ProductNewPage.reloadCategories();
                        } catch (e) {
                            alert('Ошибка: ' + e.message);
                            btn.disabled = false;
                        }
                    }
                });
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

    function escapeHtml(s) {
        return String(s || '').replace(/[&<>"']/g, ch => ({
            '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
        }[ch]));
    }

    return { loadList, createCategory };
})();