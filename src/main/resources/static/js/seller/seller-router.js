window.SellerRouter = (function () {

    const PAGES = {
        list:       'page-list',
        new:        'page-new',
        edit:       'page-edit',
        orders:     'page-orders',
        categories: 'page-categories'
    };

    const TAB_FOR_PAGE = {
        list:       'list',
        new:        'list',
        edit:       'list',
        orders:     'orders',
        categories: 'categories'
    };

    function hideAll() {
        Object.values(PAGES).forEach(id => {
            const el = document.getElementById(id);
            if (el) el.classList.add('hidden');
        });
    }

    function show(page, param) {
        hideAll();
        const id = PAGES[page] || PAGES.list;
        const el = document.getElementById(id);
        if (el) el.classList.remove('hidden');

        const activeTab = TAB_FOR_PAGE[page] || page;
        document.querySelectorAll('.seller-nav-item').forEach(i => {
            i.classList.toggle('active', i.dataset.sellerTab === activeTab);
        });

        if (page === 'list'       && window.ProductListPage)   window.ProductListPage.init();
        if (page === 'new'        && window.ProductNewPage)    window.ProductNewPage.init();
        if (page === 'edit'       && window.ProductEditPage)   window.ProductEditPage.load(param);
        if (page === 'orders'     && window.SellerOrdersPage)  window.SellerOrdersPage.init();
        if (page === 'categories' && window.CategoriesPage)    window.CategoriesPage.loadList();
    }

    function handleRoute() {
        const hash = (window.location.hash || '#/').replace(/^#\/?/, '');
        const parts = hash.split('/');
        const page = parts[0] || 'list';

        if (page === 'edit' && parts[1]) {
            show('edit', parts[1]);
            return;
        }

        if (PAGES[page]) show(page);
        else show('list');
    }

    window.addEventListener('hashchange', handleRoute);

    function init() {
        document.querySelectorAll('[data-back]').forEach(btn => {
            btn.addEventListener('click', () => {
                window.location.hash = '#/';
            });
        });
        handleRoute();
    }

    return { init, show };
})();