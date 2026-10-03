window.SellerOrdersPage = (function () {
    const api = window.OrdersApi;
    const loader = window.Loader || {
        show: (el, t) => el.innerHTML = `<p class="muted">${t || 'Загрузка...'}</p>`,
        empty: (el, t) => el.innerHTML = `<div class="empty-state">${t || 'Пусто'}</div>`
    };

    // Статусы заказа (без SHIPPED — скрыт, но остаётся в enum)
    const ORDER_STATUSES = [
        { value: 'CONFIRMED', label: 'Новый' },
        { value: 'ASSEMBLED', label: 'Собран' },
        { value: 'DONE',      label: 'Выдан' },
        { value: 'CANCELLED', label: 'Отменён' }
    ];

    // Статусы оплаты
    const PAYMENT_STATUSES = [
        { value: 'PENDING', label: 'Не оплачен' },
        { value: 'PAID',    label: 'Оплачен' }
    ];

    const FILTERS = [
        { value: 'CONFIRMED', label: 'Новые' },
        { value: 'ASSEMBLED', label: 'Собранные' },
        { value: 'DONE',      label: 'Выданные' },
        { value: 'CANCELLED', label: 'Отменённые' },
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

        loader.show(container);

        try {
            const orders = await api.getAll(activeStatus);
            if (!orders || orders.length === 0) {
                loader.empty(container, 'Заказов нет');
                return;
            }
            container.innerHTML = orders.map(renderOrder).join('');
            bindActions(container);
        } catch (e) {
            loader.empty(container, 'Ошибка: ' + e.message);
        }
    }

    function renderOrder(order) {
        const items = (order.items || []).map(renderItem).join('');

        return `
            <div class="seller-order-card" data-id="${order.id}">
                <div class="seller-order-header">
                    <div>
                        <div class="seller-order-number">Заказ №${order.id}</div>
                        <div class="seller-order-date">${formatDate(order.createdAt)}</div>
                    </div>
                    <div class="seller-order-total">${formatPrice(order.totalPrice)} ₽</div>
                </div>

                <div class="seller-order-items">${items}</div>

                <div class="seller-order-customer">
                    ${renderCustomerName(order)}
                    <div class="seller-order-row"><span>Телефон</span><span>${escapeHtml(order.deliveryPhone)}</span></div>
                    <div class="seller-order-row"><span>Город</span><span>${escapeHtml(order.deliveryCity)}</span></div>
                    <div class="seller-order-row"><span>Адрес</span><span>${escapeHtml(order.deliveryStreet)}</span></div>
                    ${order.comment ? `<div class="seller-order-row"><span>Комментарий</span><span>${escapeHtml(order.comment)}</span></div>` : ''}
                </div>

                <div class="seller-order-info">
                    <div class="seller-order-row">
                        <span>Способ получения</span>
                        <span>${labelDelivery(order.deliveryMethod)}</span>
                    </div>
                    <div class="seller-order-row">
                        <span>Способ оплаты</span>
                        <span>${labelPaymentMethod(order.paymentMethod)}</span>
                    </div>
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

    function renderCustomerName(order) {
        const parts = [
            order.customerLastName,
            order.customerFirstName,
            order.customerMiddleName
        ].filter(Boolean);

        if (parts.length === 0) return '';
        return `<div class="seller-order-row"><span>Получатель</span><span>${escapeHtml(parts.join(' '))}</span></div>`;
    }

    function renderItem(item) {
        const img = item.productImageUrl
            ? `<img src="${item.productImageUrl}" alt="">`
            : `<div class="cart-item-placeholder">📷</div>`;
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

    function labelDelivery(m) {
        return m === 'PICKUP' ? 'Самовывоз' : (m || '—');
    }

    function labelPaymentMethod(m) {
        const labels = { YOOKASSA: 'Онлайн (ЮKassa)', ON_DELIVERY: 'При получении (нал / СБП)' };
        return labels[m] || m || '—';
    }

    function bindActions(container) {
        container.querySelectorAll('.seller-select').forEach(sel => {
            sel.addEventListener('change', () => onSelectChange(sel));
        });
    }

    async function onSelectChange(sel) {
        const id = sel.dataset.id;
        const field = sel.dataset.field;
        const value = sel.value;

        const previous = sel.dataset.previous || sel.querySelector('option[selected]')?.value;
        const fieldLabel = field === 'status' ? 'статус заказа' : 'статус оплаты';

        // Подтверждение для отмены
        if (value === 'CANCELLED' && field === 'status') {
            sel.disabled = true;
            openConfirm({
                title: 'Отменить заказ?',
                text: 'Это действие нельзя отменить.',
                okText: 'Отменить',
                onConfirm: async () => {
                    await doUpdate(sel, id, field, value, fieldLabel);
                },
                onCancel: () => {
                    // откатить select назад
                    if (previous) sel.value = previous;
                    sel.disabled = false;
                }
            });
            return;
        }

        sel.disabled = true;
        await doUpdate(sel, id, field, value, fieldLabel);
    }

    async function doUpdate(sel, id, field, value, fieldLabel) {
        try {
            if (field === 'status') {
                await api.updateStatus(id, value);
            } else {
                await api.updatePaymentStatus(id, value);
            }

            toast(`Заказ №${id}: ${fieldLabel} изменён на «${sel.options[sel.selectedIndex].text}»`, true);
            load();
        } catch (e) {
            alert('Ошибка: ' + e.message);
            sel.disabled = false;
        }
    }

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

        function handleOk() {
            close();
            if (onConfirm) onConfirm();
        }

        function handleCancel() {
            close();
            if (onCancel) onCancel();
        }

        okBtn.addEventListener('click', handleOk);
        closeEls.forEach(el => el.addEventListener('click', handleCancel));
    }

    function toast(msg, ok) {
        if (window.Toast && window.Toast.show) {
            window.Toast.show(document.getElementById('sellerOrdersList'), msg, ok);
        } else {
            console.log(msg);
        }
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

    return { init };
})();