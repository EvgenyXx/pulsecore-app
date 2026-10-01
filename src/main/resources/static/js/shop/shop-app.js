(function () {
    document.addEventListener('DOMContentLoaded', async () => {
        await window.CartStore.init();

        window.CatalogPage.init();

        const updateBadge = () => {
            const badge = document.getElementById('cartBadge');
            if (!badge) return;
            const count = window.CartStore.getCount();
            if (count > 0) {
                badge.textContent = count > 99 ? '99+' : count;
                badge.classList.remove('hidden');
            } else {
                badge.classList.add('hidden');
            }
        };
        updateBadge();
        window.addEventListener('cart:change', updateBadge);

        window.ShopRouter.init();
    });
})();