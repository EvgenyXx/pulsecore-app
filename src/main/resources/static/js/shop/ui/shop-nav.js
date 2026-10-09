/**
 * Нижняя панель магазина. Рендерится через ShopIcons.
 * Активная подсветка — по хэшу. Слушает hashchange.
 */
window.ShopNav = (function () {
    const ICONS = window.ShopIcons;

    const ITEMS = [
        { tab: 'app',        href: '/dashboard',    label: 'PulseCore', icon: 'navApp',         external: true },
        { tab: 'catalog',    href: '#/',            label: 'Каталог',   icon: 'navCatalog' },
        { tab: 'categories', href: '#/categories',  label: 'Категории', icon: 'navCategories' },
        { tab: 'cart',       href: '#/cart',        label: 'Корзина',   icon: 'navCart' },
        { tab: 'orders',     href: '#/orders',      label: 'Заказы',    icon: 'navOrders' }
    ];

    function icon(key) {
        return ICONS[key] || '';
    }

    function render() {
        const nav = document.getElementById('shopNav');
        if (!nav) return;

        nav.innerHTML = ITEMS.map(item => `
            <a href="${item.href}"
               class="shop-nav-item${item.external ? ' shop-nav-item-external' : ''}"
               data-shop-tab="${item.tab}">
                <span class="shop-nav-icon">
                    ${icon(item.icon)}
                    ${item.tab === 'cart'
                        ? `<span class="shop-nav-badge hidden" id="cartBadge">0</span>`
                        : ''}
                </span>
                <span class="shop-nav-label">${item.label}</span>
            </a>
        `).join('');

        highlight();
    }

    function highlight() {
        const hash = (window.location.hash || '#/').replace(/^#\/?/, '');
        const first = hash.split('/')[0] || '';

        let activeTab = 'catalog';

        if (first === 'categories' || first === 'category') {
            activeTab = 'categories';
        } else if (first === 'cart' || first === 'checkout') {
            activeTab = 'cart';
        } else if (first === 'orders') {
            activeTab = 'orders';
        } else if (first === 'product') {
            const backTo = sessionStorage.getItem('productBackTo') || '#/';
            activeTab = backTo.startsWith('#/category') ? 'categories' : 'catalog';
        } else if (first === '' || first === 'catalog') {
            activeTab = 'catalog';
        }

        document.querySelectorAll('.shop-nav-item').forEach(el => {
            el.classList.toggle('active', el.dataset.shopTab === activeTab);
        });
    }

    function init() {
        render();
        window.addEventListener('hashchange', highlight);
    }

    return { init, highlight, render };
})();