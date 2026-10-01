window.ProductListPage = (function () {
    const api = window.ProductsApi;
    let allProducts = [];
    let searchQuery = '';

    async function init() {
        const grid = document.getElementById('sellerProductsGrid');
        grid.innerHTML = '<p class="muted">Загрузка...</p>';

        bindSearch();

        try {
            allProducts = await api.getAll();
            render();
        } catch (e) {
            grid.innerHTML = '<p class="muted">Ошибка: ' + e.message + '</p>';
        }
    }

    function bindSearch() {
        const input = document.getElementById('sellerSearch');
        if (!input) return;
        input.addEventListener('input', () => {
            searchQuery = input.value.trim().toLowerCase();
            render();
        });
    }

    function render() {
        const grid = document.getElementById('sellerProductsGrid');

        let list = allProducts;
        if (searchQuery) {
            list = list.filter(p => {
                const name = (p.name || '').toLowerCase();
                const brand = (p.brand || '').toLowerCase();
                return name.includes(searchQuery) || brand.includes(searchQuery);
            });
        }

        if (list.length === 0) {
            grid.innerHTML = `<div class="empty-state">${searchQuery ? 'Ничего не найдено' : 'Товаров пока нет'}</div>`;
            return;
        }

        grid.innerHTML = list.map(renderCard).join('');

        grid.querySelectorAll('.seller-product-card').forEach(card => {
            card.addEventListener('click', () => {
                window.location.hash = '#/edit/' + card.dataset.id;
            });
        });
    }

    function renderCard(p) {
        const img = p.mainImageUrl || (p.images && p.images[0]) || null;
        const imageBlock = img
            ? `<img src="${img}" alt="${p.name}">`
            : `<div class="seller-product-placeholder">📷</div>`;

        const badge = p.stock > 0
            ? `<span class="seller-product-badge active">В наличии</span>`
            : `<span class="seller-product-badge out">Нет</span>`;

        const brand = p.brand ? `<span class="seller-product-brand">${capitalize(p.brand)}</span>` : '';

        return `
            <div class="seller-product-card" data-id="${p.id}">
                <div class="seller-product-image">
                    ${imageBlock}
                    ${badge}
                </div>
                <div class="seller-product-body">
                    ${brand}
                    <span class="seller-product-name">${p.name}</span>
                    <span class="seller-product-price">${formatPrice(p.price)} ₽</span>
                    <span class="seller-product-stock">Остаток: ${p.stock} шт.</span>
                </div>
            </div>
        `;
    }

    function formatPrice(v) {
        return Number(v).toLocaleString('ru-RU');
    }

    function capitalize(s) {
        if (!s) return '';
        return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
    }

    return { init };
})();