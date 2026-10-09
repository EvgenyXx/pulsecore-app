window.OrdersPage = (function () {
    const ordersApi = window.OrdersApi;
    const ICONS = window.ShopIcons;

    // PWA — 10, десктоп — 20
    const PAGE_SIZE = window.innerWidth < 768 ? 10 : 20;

    let activeOrders = [];
    let allOrders = [];
    let allOrdersPage = 0;
    let allOrdersHasMore = false;
    let currentTab = 'active';
    let initialized = false;

    function init() {
        activeOrders = [];
        allOrders = [];
        allOrdersPage = 0;
        allOrdersHasMore = false;
        currentTab = 'active';
        initialized = false;
        load();
    }

    async function load() {
        const container = document.getElementById('ordersContent');
        if (!container) return;

        if (!initialized) {
            container.innerHTML = `
                <div class="orders-tabs">
                    <div class="orders-tabs-slider pos-0"></div>
                    <span class="orders-tab active" data-tab="active">Активные</span>
                    <span class="orders-tab" data-tab="all">Все заказы</span>
                </div>
                <div class="orders-body">
                    <div class="orders-pane" data-pane="active">
                        <p class="muted">Загрузка...</p>
                    </div>
                    <div class="orders-pane" data-pane="all" hidden>
                        <p class="muted">Загрузка...</p>
                    </div>
                </div>
            `;
            bindTabs(container);
            initialized = true;
        }

        try {
            activeOrders = await ordersApi.getActive() || [];
            renderActivePane();
            applyTabVisibility();
        } catch (e) {
            setPane('active', `<div class="empty-state">Ошибка загрузки: ${e.message}</div>`);
        }
    }

    async function switchTab(tab) {
        if (tab === currentTab) return;
        currentTab = tab;

        const slider = document.querySelector('.orders-tabs-slider');
        if (slider) {
            slider.classList.remove('pos-0', 'pos-1');
            slider.classList.add(tab === 'active' ? 'pos-0' : 'pos-1');
        }

        document.querySelectorAll('.orders-tab').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.tab === currentTab);
        });

        if (tab === 'all' && allOrders.length === 0 && allOrdersPage === 0) {
            setPane('all', `<p class="muted">Загрузка...</p>`);
            applyTabVisibility();
            try {
                await loadAllOrdersPage(0);
                renderAllPane();
            } catch (e) {
                setPane('all', `<div class="empty-state">Ошибка загрузки: ${e.message}</div>`);
                currentTab = 'active';
                if (slider) {
                    slider.classList.remove('pos-0', 'pos-1');
                    slider.classList.add('pos-0');
                }
                document.querySelectorAll('.orders-tab').forEach(btn => {
                    btn.classList.toggle('active', btn.dataset.tab === 'active');
                });
                applyTabVisibility();
                return;
            }
        }

        applyTabVisibility();
    }

    // ===== ПАГИНАЦИЯ =====

    async function loadAllOrdersPage(page) {
        const response = await ordersApi.getMyOrders(page, PAGE_SIZE);
        const content = response.content || [];
        allOrdersPage = page;

        if (page === 0) {
            allOrders = content;
        } else {
            allOrders = allOrders.concat(content);
        }

        allOrdersHasMore = !response.last;
    }

    function bindLoadMore(container) {
        const btn = container.querySelector('#loadMoreOrdersBtn');
        if (!btn) return;
        btn.addEventListener('click', async () => {
            btn.disabled = true;
            btn.textContent = 'Загрузка...';
            try {
                await loadAllOrdersPage(allOrdersPage + 1);
                renderAllPane();
            } catch (e) {
                btn.disabled = false;
                btn.textContent = 'Ошибка, попробуйте ещё раз';
            }
        });
    }

    // ===== РЕНДЕР ПАНЕЛЕЙ =====

    function setPane(pane, html) {
        const el = document.querySelector(`#ordersContent .orders-pane[data-pane="${pane}"]`);
        if (el) el.innerHTML = html;
    }

    function applyTabVisibility() {
        const panes = document.querySelectorAll('#ordersContent .orders-pane');
        panes.forEach(p => {
            p.hidden = p.dataset.pane !== currentTab;
        });
    }

    function renderActivePane() {
        if (activeOrders.length === 0) {
            setPane('active', `
                <div class="cart-empty">
                    <div class="cart-empty-icon">${ICONS.box}</div>
                    <p class="cart-empty-text">Активных заказов нет</p>
                    <a href="#/" class="cart-empty-link">Перейти в каталог</a>
                </div>
            `);
            return;
        }

        setPane('active', `<div class="orders-list">${activeOrders.map(renderOrder).join('')}</div>`);
    }

    function renderAllPane() {
        if (allOrders.length === 0) {
            setPane('all', `
                <div class="cart-empty">
                    <div class="cart-empty-icon">${ICONS.box}</div>
                    <p class="cart-empty-text">Заказов пока нет</p>
                    <a href="#/" class="cart-empty-link">Перейти в каталог</a>
                </div>
            `);
            return;
        }

        const loadMoreHtml = allOrdersHasMore
            ? `<button type="button" id="loadMoreOrdersBtn" class="orders-load-more">Показать ещё</button>`
            : '';

        setPane('all', `<div class="orders-list">${allOrders.map(renderOrder).join('')}</div>${loadMoreHtml}`);

        if (allOrdersHasMore) {
            bindLoadMore(document.getElementById('ordersContent'));
        }
    }

    // ===== КАРТОЧКА ЗАКАЗА =====

    function renderOrder(order) {
        const items = (order.items || []).map(renderItem).join('');
        const date = formatDateShort(order.createdAt);
        const time = formatTime(order.createdAt);

        return `
            <div class="order-card" data-id="${order.id}">

                <div class="order-head">
                    <div class="order-head-left">
                        <span class="order-number">Заказ №${order.id}</span>
                        <span class="order-date">${date}, ${time}</span>
                    </div>
                    ${renderStatusBadge(order.status)}
                </div>

                <div class="order-items">${items}</div>

                <div class="order-section">
                    ${renderPickupRow(order)}
                    ${renderSellerPhoneRow(order)}
                </div>

                <div class="order-section">
                    ${renderPaymentRow(order)}
                </div>

                ${order.comment ? `
                <div class="order-section">
                    <div class="order-row">
                        <span class="order-row-icon">${ICONS.chat}</span>
                        <span class="order-row-text">${escapeHtml(order.comment)}</span>
                    </div>
                </div>` : ''}

                <div class="order-total-row">
                    <span class="order-total-label">Итого</span>
                    <span class="order-total-value">${formatPrice(order.totalPrice)} ₽</span>
                </div>

            </div>
        `;
    }

    // ===== ПОЗИЦИЯ =====

    function renderItem(item) {
        const img = item.productImageUrl
            ? `<img src="${item.productImageUrl}" alt="" loading="lazy">`
            : `<div class="order-item-placeholder">${ICONS.camera}</div>`;

        const brand = item.productBrand
            ? `<span class="order-item-brand">${escapeHtml(item.productBrand)}</span>`
            : '';

        const variantLine = buildVariantLine(item);

        const sum = formatPrice(Number(item.productPrice) * Number(item.quantity));

        return `
            <div class="order-item">
                <div class="order-item-image">${img}</div>
                <div class="order-item-info">
                    ${brand}
                    <span class="order-item-name">${escapeHtml(item.productName)}</span>
                    ${variantLine}
                    <div class="order-item-row">
                        <span class="order-item-qty">${item.quantity} × ${formatPrice(item.productPrice)} ₽</span>
                        <span class="order-item-sum">${sum} ₽</span>
                    </div>
                </div>
            </div>
        `;
    }

    function buildVariantLine(item) {
        const parts = [];
        if (item.variantSize) parts.push(item.variantSize);
        if (item.variantColor) parts.push(item.variantColor);

        if (parts.length === 0) return '';

        return `<span class="order-item-variant">${escapeHtml(parts.join(' · '))}</span>`;
    }

    // ===== ПОЛУЧЕНИЕ =====

    function renderPickupRow(order) {
        const method = order.deliveryMethod === 'PICKUP' ? 'Самовывоз'
                     : order.deliveryMethod === 'CDEK'   ? 'СДЭК · ПВЗ'
                     : order.deliveryMethod || '';

        const parts = [method];
        if (order.pickupCity) parts.push(order.pickupCity);
        if (order.pickupAddress) parts.push(order.pickupAddress);

        const text = parts.filter(Boolean).join(' · ');

        return `
            <div class="order-row">
                <span class="order-row-icon">${ICONS.pin}</span>
                <span class="order-row-text">${escapeHtml(text)}</span>
            </div>
        `;
    }

    function renderSellerPhoneRow(order) {
        if (!order.sellerPhone) return '';
        const tel = String(order.sellerPhone).replace(/[^\d+]/g, '');
        return `
            <div class="order-row">
                <span class="order-row-icon">${ICONS.phone}</span>
                <span class="order-row-text">
                    <span class="order-row-hint">Телефон магазина:</span>
                    <a class="order-row-link" href="tel:${tel}">${escapeHtml(order.sellerPhone)}</a>
                </span>
            </div>
        `;
    }

    // ===== ОПЛАТА =====

    function renderPaymentRow(order) {
        const methodLabel = order.paymentMethod === 'ON_DELIVERY' ? 'При получении'
                          : order.paymentMethod === 'YOOKASSA'    ? 'Онлайн ЮKassa'
                          : order.paymentMethod || '';

        const statusLabel = {
            PENDING:   'Не оплачен',
            PAID:      'Оплачен',
            CANCELLED: 'Отменён'
        }[order.paymentStatus] || order.paymentStatus || '';

        const statusClass = {
            PENDING:   'is-pending',
            PAID:      'is-paid',
            CANCELLED: 'is-cancelled'
        }[order.paymentStatus] || '';

        return `
            <div class="order-row">
                <span class="order-row-icon">${ICONS.card}</span>
                <span class="order-row-text">
                    Оплата: <strong>${escapeHtml(methodLabel)}</strong>
                    <span class="order-row-sep">·</span>
                    <span class="order-payment-status ${statusClass}">${escapeHtml(statusLabel)}</span>
                </span>
            </div>
        `;
    }

    // ===== БЕЙДЖИ =====

    function renderStatusBadge(status) {
        if (!status) return '';
        return `<span class="order-status ${statusClass(status)}">${statusLabel(status)}</span>`;
    }

    function statusLabel(status) {
        const labels = {
            CONFIRMED: 'Собирается', ASSEMBLED: 'Собран',
            SHIPPED: 'Отправлен', DONE: 'Получен', CANCELLED: 'Отменён'
        };
        return labels[status] || status;
    }

    function statusClass(status) {
        const map = {
            CONFIRMED: 'confirmed', ASSEMBLED: 'assembled',
            SHIPPED: 'shipped', DONE: 'done', CANCELLED: 'cancelled'
        };
        return map[status] || '';
    }

    // ===== ФОРМАТ =====

    function formatDateShort(iso) {
        if (!iso) return '';
        const d = new Date(iso);
        const months = ['января','февраля','марта','апреля','мая','июня',
                        'июля','августа','сентября','октября','ноября','декабря'];
        return `${d.getDate()} ${months[d.getMonth()]}`;
    }

    function formatTime(iso) {
        if (!iso) return '';
        const d = new Date(iso);
        const hh = String(d.getHours()).padStart(2, '0');
        const mm = String(d.getMinutes()).padStart(2, '0');
        return `${hh}:${mm}`;
    }

    function formatPrice(v) {
        return Number(v).toLocaleString('ru-RU');
    }

    function escapeHtml(s) {
        return String(s || '').replace(/[&<>"']/g, ch => ({
            '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
        }[ch]));
    }

    function bindTabs(container) {
        container.querySelectorAll('.orders-tab').forEach(btn => {
            btn.addEventListener('click', () => switchTab(btn.dataset.tab));
        });
    }

    return { init };
})();