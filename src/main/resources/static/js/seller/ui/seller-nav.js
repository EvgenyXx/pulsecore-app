/**
 * Нижняя панель селлера. Рендерится через SellerIcons.
 * Активная подсветка — по хэшу. Слушает hashchange.
 */
window.SellerNav = (function () {
    const ICONS = window.SellerIcons;

    const ITEMS = [
        { tab: 'app',        href: '/dashboard',    label: 'PulseCore', icon: 'navApp',         external: true },
        { tab: 'list',       href: '#/',            label: 'Товары',    icon: 'navProducts' },
        { tab: 'orders',     href: '#/orders',      label: 'Заказы',    icon: 'navOrders' },
        { tab: 'categories', href: '#/categories',  label: 'Категории', icon: 'navCategories' }
    ];

    function render() {
        const nav = document.getElementById('sellerNav');
        if (!nav) return;

        nav.innerHTML = ITEMS.map(item => `
            <a href="${item.href}"
               class="seller-nav-item${item.external ? ' seller-nav-item-external' : ''}"
               data-seller-tab="${item.tab}">
                <span class="seller-nav-icon">${ICONS[item.icon] || ''}</span>
                <span class="seller-nav-label">${item.label}</span>
            </a>
        `).join('');

        highlight();
    }

    function highlight() {
        const hash = (window.location.hash || '#/').replace(/^#\/?/, '');
        const first = hash.split('/')[0] || '';

        let activeTab = 'list';
        if (first === 'orders')          activeTab = 'orders';
        else if (first === 'categories') activeTab = 'categories';
        else if (first === '' || first === 'list' || first === 'new' || first === 'edit') activeTab = 'list';

        document.querySelectorAll('.seller-nav-item').forEach(el => {
            el.classList.toggle('active', el.dataset.sellerTab === activeTab);
        });
    }

    function init() {
        render();
        window.addEventListener('hashchange', highlight);
    }

    return { init, highlight, render };
})();