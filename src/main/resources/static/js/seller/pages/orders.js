window.SellerOrdersPage = (function () {
    const api = window.OrdersApi;

    // ===== Статусы заказа =====
    const ORDER_STATUSES = [
        { value: 'CONFIRMED', label: 'Новый' },
        { value: 'ASSEMBLED', label: 'Собран' },
        { value: 'SHIPPED',   label: 'Отправлен' },
        { value: 'DONE',      label: 'Выдан' },
        { value: 'CANCELLED', label: 'Отменён' }
    ];

    const STATUS_META = {
        CONFIRMED: { label: 'Новый',     cls: 'confirmed' },
        ASSEMBLED: { label: 'Собран',    cls: 'assembled' },
        SHIPPED:   { label: 'Отправлен', cls: 'shipped' },
        DONE:      { label: 'Выдан',     cls: 'done' },
        CANCELLED: { label: 'Отменён',   cls: 'cancelled' }
    };

    // ===== Статусы оплаты =====
    const PAYMENT_STATUSES = [
        { value: 'PENDING',   label: 'Не оплачен' },
        { value: 'PAID',      label: 'Оплачен' },
        { value: 'CANCELLED', label: 'Отменён' }
    ];

    const PAYMENT_META = {
        PENDING:   { label: 'Не оплачен', cls: 'pending' },
        PAID:      { label: 'Оплачен',    cls: 'paid' },
        CANCELLED: { label: 'Отменён',    cls: 'cancelled' }
    };

    // ===== Вкладки =====
    const FILTERS = [
        { value: 'CONFIRMED', label: 'Новые' },
        { value: 'ASSEMBLED', label: 'Собранные' },
        { value: 'DONE',      label: 'Выданные' },
        { value: 'all',       label: 'Все' }
    ];

    let activeStatus = 'CONFIRMED';

    function init() {
        renderFilter();
        bindChips();
        load();
    }

    function renderFilter() {
        const filter = document.getElementById('orderFilter');
        if (!filter) return;
        filter.innerHTML = FILTERS.map(f => `
            <button class="order-chip ${f.value === activeStatus ? 'active' : ''}"
                    data-status="${f.value}">${f.label}</button>
        `).join('');
    }

    function bindChips() {
        const filter = document.getElementById('orderFilter');
        if (!filter || filter.dataset.bound) return;
        filter.dataset.bound = '1';

        filter.addEventListener('click', (e) => {
            const chip = e.target.closest('.order-chip');
            if (!chip) return;

            filter.querySelectorAll('.order-chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            activeStatus = chip.dataset.status;
            load();
        });
    }

    async function load() {
        const container = document.getElementById('sellerOrdersList');
        if (!container) return;

        const isEmpty = !container.querySelector('.seller-order-card');
        if (isEmpty) {
            container.innerHTML = `<p class="muted">Загрузка...</p>`;
        }

        try {
            const orders = await api.getAll(activeStatus);

            if (!orders || orders.length === 0) {
                container.innerHTML = `<div class="seller-orders-empty">Заказов нет</div>`;
                return;
            }

            container.innerHTML = orders.map(renderOrder).join('');
            bindActions(container);
        } catch (e) {
            container.innerHTML = `<div class="seller-orders-empty">Ошибка: ${escapeHtml(e.message)}</div>`;
        }
    }

    // ===== КАРТОЧКА =====

    function renderOrder(order) {
        const items = (order.items || []).map(renderItem).join('');
        const status  = STATUS_META[order.status]  || { label: order.status,  cls: '' };
        const payment = PAYMENT_META[order.paymentStatus] || { label: order.paymentStatus, cls: '' };

        return `
            <div class="seller-order-card" data-id="${order.id}">

                <div class="seller-order-head">
                    <div class="seller-order-head-left">
                        <span class="seller-order-number">Заказ №${order.id}</span>
                        <span class="seller-order-date">${formatDate(order.createdAt)}</span>
                    </div>
                    <div class="seller-order-badges">
                        <span class="seller-badge status-${status.cls}" data-badge="status">${status.label}</span>
                        <span class="seller-badge payment-${payment.cls}" data-badge="payment">${payment.label}</span>
                    </div>
                </div>

                <div class="seller-order-items">${items}</div>

                <div class="seller-order-info-block">
                    ${renderRow('user',  'Получатель', customerName(order))}
                    ${renderRow('phone', 'Телефон',    order.deliveryPhone, true)}
                    ${renderRow('pin',   'Получение',  deliveryLine(order))}
                    ${renderRow('card',  'Оплата',     paymentMethodLabel(order.paymentMethod))}
                    ${order.comment ? renderRow('chat', 'Комментарий', order.comment) : ''}
                </div>

                <div class="seller-order-controls">
                    <div class="seller-order-control">
                        <label>Статус заказа</label>
                        <select class="seller-select" data-field="status" data-id="${order.id}">
                            ${ORDER_STATUSES.map(s => `
                                <option value="${s.value}" ${order.status === s.value ? 'selected' : ''}>${s.label}</option>
                            `).join('')}
                        </select>
                    </div>
                    <div class="seller-order-control">
                        <label>Статус оплаты</label>
                        <select class="seller-select" data-field="paymentStatus" data-id="${order.id}">
                            ${PAYMENT_STATUSES.map(s => `
                                <option value="${s.value}" ${order.paymentStatus === s.value ? 'selected' : ''}>${s.label}</option>
                            `).join('')}
                        </select>
                    </div>
                </div>

            </div>
        `;
    }

    function renderRow(icon, label, value, isPhone = false) {
        if (!value) return '';
        const inner = isPhone
            ? `<a href="tel:${String(value).replace(/[^\d+]/g, '')}">${escapeHtml(value)}</a>`
            : escapeHtml(value);
        return `
            <div class="seller-order-row">
                <span class="seller-order-row-icon">${window.SellerIcons?.[icon] || ''}</span>
                <span class="seller-order-row-label">${label}</span>
                <span class="seller-order-row-value">${inner}</span>
            </div>
        `;
    }

    function customerName(order) {
        const parts = [
            order.customerLastName,
            order.customerFirstName,
            order.customerMiddleName
        ].filter(Boolean);
        return parts.length ? parts.join(' ') : '';
    }

    function deliveryLine(order) {
        const m = order.deliveryMethod === 'PICKUP' ? 'Самовывоз' : (order.deliveryMethod || '');
        const parts = [m];
        if (order.deliveryCity)   parts.push(order.deliveryCity);
        if (order.deliveryStreet) parts.push(order.deliveryStreet);
        return parts.filter(Boolean).join(' · ');
    }

    function paymentMethodLabel(m) {
        const labels = { YOOKASSA: 'Онлайн ЮKassa', ON_DELIVERY: 'При получении' };
        return labels[m] || m || '—';
    }

    function renderItem(item) {
        const img = item.productImageUrl
            ? `<img src="${item.productImageUrl}" alt="" loading="lazy">`
            : `<div class="seller-order-item-placeholder">${window.SellerIcons?.camera || ''}</div>`;
        return `
            <div class="seller-order-item">
                <div class="seller-order-item-image">${img}</div>
                <div class="seller-order-item-body">
                    <span class="seller-order-item-name">${escapeHtml(item.productName)}</span>
                    <span class="seller-order-item-qty">${item.quantity} × ${formatPrice(item.productPrice)} ₽</span>
                </div>
            </div>
        `;
    }

    // ===== ДЕЙСТВИЯ =====

    function bindActions(container) {
        container.querySelectorAll('.seller-select').forEach(sel => {
            sel.addEventListener('change', () => onSelectChange(sel));
        });
    }

    async function onSelectChange(sel) {
        const id = sel.dataset.id;
        const field = sel.dataset.field;
        const value = sel.value;
        const card = sel.closest('.seller-order-card');
        const prevValue = sel.dataset.prev || '';

        if (field === 'status' && value === 'CANCELLED') {
            sel.disabled = true;
            openConfirm({
                title: 'Отменить заказ?',
                text: 'Это действие нельзя отменить.',
                okText: 'Отменить',
                onConfirm: async () => {
                    await doUpdate(id, field, value, card, sel);
                },
                onCancel: () => {
                    if (prevValue) sel.value = prevValue;
                    sel.disabled = false;
                }
            });
            return;
        }

        sel.disabled = true;
        await doUpdate(id, field, value, card, sel);
    }

    async function doUpdate(id, field, value, card, sel) {
        try {
            if (field === 'status') {
                await api.updateStatus(id, value);
            } else {
                await api.updatePaymentStatus(id, value);
            }

            updateCardBadges(card, field, value);
            sel.dataset.prev = value;
            sel.disabled = false;
        } catch (e) {
            alert('Ошибка: ' + e.message);
            if (sel.dataset.prev) sel.value = sel.dataset.prev;
            sel.disabled = false;
        }
    }

    function updateCardBadges(card, field, value) {
        if (field === 'status') {
            const meta = STATUS_META[value];
            const badge = card.querySelector('[data-badge="status"]');
            if (badge && meta) {
                badge.className = `seller-badge status-${meta.cls}`;
                badge.textContent = meta.label;
            }
        } else {
            const meta = PAYMENT_META[value];
            const badge = card.querySelector('[data-badge="payment"]');
            if (badge && meta) {
                badge.className = `seller-badge payment-${meta.cls}`;
                badge.textContent = meta.label;
            }
        }
    }

    // ===== МОДАЛКА =====

    function openConfirm({ title, text, okText, onConfirm, onCancel }) {
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

        function handleOk()     { close(); if (onConfirm) onConfirm(); }
        function handleCancel() { close(); if (onCancel)  onCancel(); }

        okBtn.addEventListener('click', handleOk);
        closeEls.forEach(el => el.addEventListener('click', handleCancel));
    }

    // ===== ФОРМАТ =====

    function formatDate(iso) {
        if (!iso) return '';
        const d = new Date(iso);
        const months = ['января','февраля','марта','апреля','мая','июня',
                        'июля','августа','сентября','октября','ноября','декабря'];
        const hh = String(d.getHours()).padStart(2, '0');
        const mm = String(d.getMinutes()).padStart(2, '0');
        return `${d.getDate()} ${months[d.getMonth()]}, ${hh}:${mm}`;
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