window.ShopRouter = (function () {

    const PAGES = {
        catalog:    'shopPageCatalog',
        categories: 'shopPageCategories',
        category:   'shopPageCategory',
        product:    'shopPageProduct',
        cart:       'shopPageCart',
        checkout:   'shopPageCheckout',
        orders:     'shopPageOrders'
    };

    const TAB_FOR_PAGE = {
        catalog:    'catalog',
        categories: 'categories',
        category:   'categories',
        product:    'catalog',
        cart:       'cart',
        checkout:   'cart',
        orders:     'orders'
    };

    function hideAll() {
        Object.values(PAGES).forEach(id => {
            const el = document.getElementById(id);
            if (el) el.classList.add('hidden');
        });
    }

    function show(page, param) {
        hideAll();
        const id = PAGES[page] || PAGES.catalog;
        const el = document.getElementById(id);
        if (el) el.classList.remove('hidden');

        const activeTab = TAB_FOR_PAGE[page] || page;
        document.querySelectorAll('.shop-nav-item').forEach(i => {
            i.classList.toggle('active', i.dataset.shopTab === activeTab);
        });

        if (page === 'categories' && window.CategoriesPage) window.CategoriesPage.init();
        if (page === 'category'   && window.CategoryPage)   window.CategoryPage.init(param);
        if (page === 'product'    && window.ProductPage)    window.ProductPage.init(param);
        if (page === 'cart'       && window.CartPage)       window.CartPage.init();
        if (page === 'checkout'   && window.CheckoutPage)   window.CheckoutPage.init();
        if (page === 'orders'     && window.OrdersPage)     window.OrdersPage.init();
    }

    function handleRoute() {
        const hash = (window.location.hash || '#/').replace(/^#\/?/, '');
        const parts = hash.split('/');
        const page = parts[0] || 'catalog';

        if (page === 'category' && parts[1]) {
            show('category', parts[1]);
            return;
        }

        if (page === 'product' && parts[1]) {
            show('product', parts[1]);
            return;
        }

        if (PAGES[page]) show(page);
        else show('catalog');
    }

    window.addEventListener('hashchange', handleRoute);

    function init() {
        const catBack = document.getElementById('categoryBackBtn');
        if (catBack) {
            catBack.addEventListener('click', () => {
                window.location.hash = '#/categories';
            });
        }

        handleRoute();
    }

    return { init, show };
})();