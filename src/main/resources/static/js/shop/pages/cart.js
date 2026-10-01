window.CartPage = (function () {
    const cart = window.CartStore;
    const SELECTED_KEY = 'pulsecore_cart_selected';

    let selectedIds = new Set();

    function init() {
        loadSelected();
        render();
        window.addEventListener('cart:change', onCartChange);
        window.addEventListener('hashchange', onRouteChange);
        onRouteChange();
    }

    function loadSelected() {
        try {
            const raw = localStorage.getItem(SELECTED_KEY);
            if (raw) {
                const arr = JSON.parse(raw);
                selectedIds = new Set(arr.map(String));
            }
        } catch (e) {
            selectedIds = new Set();
        }
    }

    function saveSelected() {
        localStorage.setItem(SELECTED_KEY, JSON.stringify([...selectedIds]));
    }

    function onCartChange() {
        syncSelectedWithCart();
        render();
    }

    function syncSelectedWithCart() {
        const items = cart.getAll();
        const keys = Object.keys(items);
        [...selectedIds].forEach(id => {
            if (!items[id]) selectedIds.delete(id);
        });
        if (selectedIds.size === 0 && keys.length > 0) {
            keys.forEach(id => selectedIds.add(String(id)));
        }
        saveSelected();
    }

    function onRouteChange() {
        updateBodyClass();
    }

    function render() {
        const container = document.getElementById('cartContent');
        if (!container) return;

        const items = cart.getAll();
        const list = Object.values(items);

        if (list.length === 0) {
            container.innerHTML = `
                <div class="cart-empty">
                    <div class="cart-empty-icon">🛒</div>
                    <p class="cart-empty-text">Корзина пуста</p>
                    <a href="#/" class="cart-empty-link">Перейти в каталог</a>
                </div>
            `;
            removeFloatingCheckout();
            updateBodyClass();
            return;
        }

        if (selectedIds.size === 0) {
            list.forEach(it => selectedIds.add(String(it.productId)));
            saveSelected();
        }

        container.innerHTML = `
            <div class="cart-list">
                ${list.map(renderItem).join('')}
            </div>
            <div class="cart-list-bottom-spacer"></div>
        `;

        bindItemActions(container);
        bindCheckboxActions(container);
        renderFloatingCheckout();
        updateBodyClass();
    }

    function renderItem(item) {
        const img = item.image
            ? `<img src="${item.image}" alt="${escapeHtml(item.name)}" loading="lazy">`
            : `<div class="cart-item-placeholder">📷</div>`;

        const brand = item.brand
            ? `<span class="cart-item-brand">${escapeHtml(item.brand)}</span>`
            : '';

        const isSelected = selectedIds.has(String(item.productId));

        return `
            <div class="cart-item ${isSelected ? 'selected' : ''}" data-id="${item.productId}">
                <label class="cart-item-checkbox">
                    <input type="checkbox" data-action="select" ${isSelected ? 'checked' : ''}>
                    <span class="cart-item-checkmark"></span>
                </label>
                <div class="cart-item-image">${img}</div>
                <div class="cart-item-body">
                    ${brand}
                    <span class="cart-item-name">${escapeHtml(item.name)}</span>
                    <span class="cart-item-price">${formatPrice(item.price)} ₽</span>
                    <div class="cart-item-actions">
                        <div class="cart-stepper" data-id="${item.productId}">
                            <button class="cart-step-btn" type="button" data-action="minus">−</button>
                            <span class="cart-step-qty">${item.qty}</span>
                            <button class="cart-step-btn" type="button" data-action="plus">+</button>
                        </div>
                        <button class="cart-item-remove" type="button" data-action="remove" aria-label="Удалить">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-2 14a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/></svg>
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    function bindItemActions(container) {
        container.querySelectorAll('.cart-item').forEach(item => {
            const id = item.dataset.id;
            item.querySelectorAll('[data-action="plus"], [data-action="minus"], [data-action="remove"]').forEach(btn => {
                btn.addEventListener('click', () => {
                    const action = btn.dataset.action;
                    if (action === 'plus') cart.increment(id);
                    else if (action === 'minus') cart.decrement(id);
                    else if (action === 'remove') {
                        selectedIds.delete(String(id));
                        saveSelected();
                        cart.remove(id);
                    }
                });
            });
        });
    }

    function bindCheckboxActions(container) {
        container.querySelectorAll('[data-action="select"]').forEach(cb => {
            cb.addEventListener('change', () => {
                const item = cb.closest('.cart-item');
                const id = String(item.dataset.id);
                if (cb.checked) selectedIds.add(id);
                else selectedIds.delete(id);
                item.classList.toggle('selected', cb.checked);
                saveSelected();
                renderFloatingCheckout();
            });
        });
    }

    function getSelectedItems() {
        const items = cart.getAll();
        return Object.values(items).filter(it => selectedIds.has(String(it.productId)));
    }

    function renderFloatingCheckout() {
        let bar = document.getElementById('floatingCheckout');
        if (!bar) {
            bar = document.createElement('button');
            bar.id = 'floatingCheckout';
            bar.className = 'floating-checkout';
            bar.type = 'button';
            document.body.appendChild(bar);
            bar.addEventListener('click', onCheckoutClick);
        }

        const selected = getSelectedItems();
        const count = selected.length;
        const total = selected.reduce((sum, i) => sum + i.price * i.qty, 0);

        bar.disabled = count === 0;
        bar.classList.toggle('disabled', count === 0);

        bar.innerHTML = `
            <span class="floating-checkout-left">
                <span class="floating-checkout-label">К оформлению</span>
                <span class="floating-checkout-dot">·</span>
                <span class="floating-checkout-count">${count}</span>
            </span>
            <span class="floating-checkout-right">
                <svg class="floating-checkout-wallet" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/>
                    <path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/>
                    <path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/>
                </svg>
                <span class="floating-checkout-total">${formatPrice(total)} ₽</span>
            </span>
        `;
    }

    function onCheckoutClick() {
        const selected = getSelectedItems();
        if (selected.length === 0) return;
        window.location.hash = '#/checkout';
    }

    function removeFloatingCheckout() {
        const bar = document.getElementById('floatingCheckout');
        if (bar) bar.remove();
    }

    function updateBodyClass() {
        const hash = (window.location.hash || '#/').replace(/^#\/?/, '');
        const isCart = hash === 'cart' || hash.startsWith('cart');
        const hasItems = cart.getCount() > 0;
        document.body.classList.toggle('cart-checkout-visible', isCart && hasItems);
    }

    function formatPrice(v) {
        return Number(v).toLocaleString('ru-RU');
    }

    function escapeHtml(s) {
        return String(s || '').replace(/[&<>"']/g, ch => ({
            '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
        }[ch]));
    }

    return { init };
})();