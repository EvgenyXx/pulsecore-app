window.OrdersPage = (function () {
    const ordersApi = window.OrdersApi;

    let activeOrders = [];
    let allOrders = null;
    let currentTab = 'active';
    let initialized = false;

    function init() {
        activeOrders = [];
        allOrders = null;
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
                    <p class="muted">Загрузка...</p>
                </div>
            `;
            bindTabs(container);
            initialized = true;
        }

        try {
            activeOrders = await ordersApi.getActive() || [];
            renderBody();
        } catch (e) {
            setBody(`<div class="empty-state">Ошибка загрузки: ${e.message}</div>`);
        }
    }

    async function switchTab(tab) {
        if (tab === currentTab) return;
        currentTab = tab;

        // плавно двигаем слайдер — без перерисовки
        const slider = document.querySelector('.orders-tabs-slider');
        if (slider) {
            slider.classList.remove('pos-0', 'pos-1');
            slider.classList.add(tab === 'active' ? 'pos-0' : 'pos-1');
        }

        document.querySelectorAll('.orders-tab').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.tab === currentTab);
        });

        if (tab === 'all' && allOrders === null) {
            setBody(`<p class="muted">Загрузка...</p>`);
            try {
                allOrders = await ordersApi.getMyOrders() || [];
            } catch (e) {
                setBody(`<div class="empty-state">Ошибка загрузки: ${e.message}</div>`);
                currentTab = 'active';
                if (slider) {
                    slider.classList.remove('pos-0', 'pos-1');
                    slider.classList.add('pos-0');
                }
                document.querySelectorAll('.orders-tab').forEach(btn => {
                    btn.classList.toggle('active', btn.dataset.tab === 'active');
                });
                return;
            }
        }

        renderBody();
    }

    function setBody(html) {
        const body = document.querySelector('#ordersContent .orders-body');
        if (body) body.innerHTML = html;
    }

    function renderBody() {
        const list = currentTab === 'active' ? activeOrders : (allOrders || []);

        if (list.length === 0) {
            setBody(currentTab === 'active'
                ? `<div class="cart-empty">
                       <div class="cart-empty-icon">📦</div>
                       <p class="cart-empty-text">Активных заказов нет</p>
                       <a href="#/" class="cart-empty-link">Перейти в каталог</a>
                   </div>`
                : `<div class="cart-empty">
                       <div class="cart-empty-icon">📦</div>
                       <p class="cart-empty-text">Заказов пока нет</p>
                       <a href="#/" class="cart-empty-link">Перейти в каталог</a>
                   </div>`);
            return;
        }

        setBody(`<div class="orders-list">${list.map(renderOrder).join('')}</div>`);
        bindActions();
    }

    function renderOrder(order) {
        const items = (order.items || []).map(renderItem).join('');
        const date = formatDate(order.createdAt);

        return `
            <div class="order-card" data-id="${order.id}">
                <div class="order-header" data-action="toggle">
                    <div class="order-header-left">
                        <span class="order-number">Заказ №${order.id}</span>
                        <span class="order-date">${date}</span>
                    </div>
                    <div class="order-header-right">
                        ${renderPaymentBadge(order.paymentStatus)}
                        ${renderStatusBadge(order.status)}
                        <span class="order-arrow">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="6 9 12 15 18 9"/></svg>
                        </span>
                    </div>
                </div>
                <div class="order-items">${items}</div>
                <div class="order-details">
                    ${renderDetails(order)}
                </div>
                <div class="order-footer">
                    <span class="order-total-label">Итого</span>
                    <span class="order-total">${formatPrice(order.totalPrice)} ₽</span>
                </div>
            </div>
        `;
    }

    function renderPaymentBadge(paymentStatus) {
        if (!paymentStatus) return '';
        const labels = { PENDING: 'Не оплачен', PAID: 'Оплачен', CANCELLED: 'Отменён' };
        const cls = { PENDING: 'payment-pending', PAID: 'payment-paid', CANCELLED: 'payment-cancelled' };
        return `<span class="order-payment ${cls[paymentStatus] || ''}">${labels[paymentStatus] || paymentStatus}</span>`;
    }

    function renderStatusBadge(status) {
        if (!status) return '';
        return `<span class="order-status ${statusClass(status)}">${statusLabel(status)}</span>`;
    }

    function renderDetails(order) {
        const rows = [];
        const deliveryLabel = order.deliveryMethod === 'PICKUP' ? 'Самовывоз' : 'СДЭК · ПВЗ';
        rows.push(row('Доставка', deliveryLabel));

        if (order.deliveryMethod === 'PICKUP') {
            if (order.deliveryCity) rows.push(row('Город', escapeHtml(order.deliveryCity)));
            if (order.deliveryStreet) rows.push(row('Адрес', escapeHtml(order.deliveryStreet)));
        } else {
            if (order.deliveryCity) rows.push(row('Город', escapeHtml(order.deliveryCity)));
            if (order.deliveryStreet) rows.push(row('Адрес ПВЗ', escapeHtml(order.deliveryStreet)));
        }

        if (order.paymentMethod) {
            const payLabel = order.paymentMethod === 'ON_DELIVERY'
                ? 'При получении (нал / СБП)'
                : 'Онлайн (ЮKassa)';
            rows.push(row('Оплата', payLabel));
        }

        if (order.deliveryPhone) rows.push(row('Телефон', escapeHtml(order.deliveryPhone)));
        if (order.customerName) rows.push(row('Получатель', escapeHtml(order.customerName)));
        if (order.comment) rows.push(row('Комментарий', escapeHtml(order.comment)));

        return rows.join('');
    }

    function row(label, value) {
        return `
            <div class="order-detail-row">
                <span class="order-detail-label">${label}</span>
                <span class="order-detail-value">${value}</span>
            </div>
        `;
    }

    function renderItem(item) {
        const img = item.productImageUrl
            ? `<img src="${item.productImageUrl}" alt="" loading="lazy">`
            : `<div class="cart-item-placeholder">📷</div>`;

        const brand = item.productBrand
            ? `<span class="cart-item-brand">${escapeHtml(item.productBrand)}</span>`
            : '';

        return `
            <div class="order-item">
                <div class="order-item-image">${img}</div>
                <div class="order-item-body">
                    ${brand}
                    <span class="order-item-name">${escapeHtml(item.productName)}</span>
                    <span class="order-item-qty">${item.quantity} × ${formatPrice(item.productPrice)} ₽</span>
                </div>
            </div>
        `;
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

    function formatDate(iso) {
        if (!iso) return '';
        const d = new Date(iso);
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        const hh = String(d.getHours()).padStart(2, '0');
        const mm = String(d.getMinutes()).padStart(2, '0');
        return `${day}.${month}.${year} · ${hh}:${mm}`;
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

    function bindActions() {
        document.querySelectorAll('.order-header[data-action="toggle"]').forEach(h => {
            if (h.dataset.bound === '1') return;
            h.dataset.bound = '1';
            h.addEventListener('click', () => {
                const card = h.closest('.order-card');
                card.classList.toggle('expanded');
            });
        });
    }

    return { init };
})();