window.SellerOrdersPage = (function () {
    const api = window.OrdersApi;
    const loader = window.Loader || { show: (el, t) => el.innerHTML = `<p class="muted">${t || 'Загрузка...'}</p>`, empty: (el, t) => el.innerHTML = `<div class="empty-state">${t || 'Пусто'}</div>` };

    let activeStatus = 'CONFIRMED';

    function init() {
        bindChips();
        load();
    }

    function bindChips() {
        const filter = document.getElementById('orderFilter');
        if (!filter || filter.dataset.bound) return;
        filter.dataset.bound = '1';

        filter.querySelectorAll('.order-chip').forEach(chip => {
            chip.addEventListener('click', () => {
                filter.querySelectorAll('.order-chip').forEach(c => c.classList.remove('active'));
                chip.classList.add('active');
                activeStatus = chip.dataset.status;
                load();
            });
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

        const isPickup = order.deliveryMethod === 'PICKUP';
        const streetLabel = isPickup ? 'Адрес самовывоза' : 'ПВЗ';

        return `
            <div class="seller-order-card" data-id="${order.id}">
                <div class="seller-order-header">
                    <div>
                        <div class="seller-order-number">Заказ №${order.id}</div>
                        <div class="seller-order-date">${formatDate(order.createdAt)}</div>
                    </div>
                    <div class="seller-order-badges">
                        ${badgeStatus(order.status)}
                    </div>
                </div>

                <div class="seller-order-items">${items}</div>

                <div class="seller-order-customer">
                    ${renderCustomerName(order)}
                    <div class="seller-order-row"><span>Телефон</span><span>${escapeHtml(order.deliveryPhone)}</span></div>
                    <div class="seller-order-row"><span>Город</span><span>${escapeHtml(order.deliveryCity)}</span></div>
                    <div class="seller-order-row"><span>${streetLabel}</span><span>${escapeHtml(order.deliveryStreet)}</span></div>
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
                    <div class="seller-order-row">
                        <span>Статус оплаты</span>
                        <span class="${paymentClass(order.paymentStatus)}">${labelPayment(order.paymentStatus)}</span>
                    </div>
                </div>

                <div class="seller-order-footer">
                    <div class="seller-order-total">${formatPrice(order.totalPrice)} ₽</div>
                    <div class="seller-order-actions">
                        ${renderActions(order)}
                        <button class="btn-cancel" data-action="cancel" data-id="${order.id}">Отменить</button>
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

    function renderActions(order) {
        const buttons = [];

        const isPickup = order.deliveryMethod === 'PICKUP';
        const isOnDelivery = order.paymentMethod === 'ON_DELIVERY';
        const isUnpaid = order.paymentStatus !== 'PAID';
        const isDone = order.status === 'DONE';
        const isCancelled = order.status === 'CANCELLED';

        if (isCancelled || isDone) return buttons.join('');

        if (isOnDelivery && isUnpaid) {
            buttons.push(
                `<button class="btn-action btn-paid" data-action="mark-paid" data-id="${order.id}">Оплачено</button>`
            );
        }

        if (isPickup) {
            if (order.status === 'CONFIRMED') {
                buttons.push(
                    `<button class="btn-action" data-action="advance" data-next="ASSEMBLED">Собран</button>`
                );
            } else if (order.status === 'ASSEMBLED') {
                if (order.paymentStatus === 'PAID' || !isOnDelivery) {
                    buttons.push(
                        `<button class="btn-action" data-action="advance" data-next="DONE">Выдан</button>`
                    );
                }
            }
            return buttons.join('');
        }

        if (order.status === 'CONFIRMED') {
            buttons.push(`<button class="btn-action" data-action="advance" data-next="ASSEMBLED">Собран</button>`);
        } else if (order.status === 'ASSEMBLED') {
            buttons.push(`<button class="btn-action" data-action="advance" data-next="SHIPPED">Отправлен</button>`);
        } else if (order.status === 'SHIPPED') {
            buttons.push(`<button class="btn-action" data-action="advance" data-next="DONE">Получен</button>`);
        }

        return buttons.join('');
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
        const labels = { PICKUP: 'Самовывоз', CDEK: 'СДЭК · ПВЗ' };
        return labels[m] || m || '—';
    }

    function labelPaymentMethod(m) {
        const labels = { YOOKASSA: 'Онлайн (ЮKassa)', ON_DELIVERY: 'При получении (нал / СБП)' };
        return labels[m] || m || '—';
    }

    function labelPayment(s) {
        const labels = { PENDING: 'Не оплачен', PAID: 'Оплачен', CANCELLED: 'Отменён' };
        return labels[s] || s || '—';
    }

    function paymentClass(s) {
        if (s === 'PAID') return 'order-info-ok';
        if (s === 'CANCELLED') return 'order-info-bad';
        return 'order-info-warn';
    }

    function badgeStatus(s) {
        const labels = { CONFIRMED: 'Собирается', ASSEMBLED: 'Собран', SHIPPED: 'Отправлен', DONE: 'Получен', CANCELLED: 'Отменён' };
        return `<span class="seller-badge status-${(s || '').toLowerCase()}">${labels[s] || s}</span>`;
    }

    function bindActions(container) {
        container.querySelectorAll('[data-action="advance"]').forEach(btn => {
            btn.addEventListener('click', async () => {
                const card = btn.closest('.seller-order-card');
                const id = card.dataset.id;
                const next = btn.dataset.next;

                btn.disabled = true;
                btn.textContent = '...';

                try {
                    await api.updateStatus(id, next);
                    load();
                } catch (e) {
                    alert('Ошибка: ' + e.message);
                    btn.disabled = false;
                    btn.textContent = 'Повторить';
                }
            });
        });

        container.querySelectorAll('[data-action="mark-paid"]').forEach(btn => {
            btn.addEventListener('click', () => {
                const card = btn.closest('.seller-order-card');
                const id = card.dataset.id;

                openConfirm({
                    title: 'Подтвердить оплату?',
                    text: 'Заказ будет помечен как оплаченный. Товар спишется со склада.',
                    okText: 'Оплачено',
                    onConfirm: async () => {
                        btn.disabled = true;
                        btn.textContent = '...';
                        try {
                            await api.updatePaymentStatus(id, 'PAID');
                            load();
                        } catch (e) {
                            alert('Ошибка: ' + e.message);
                            btn.disabled = false;
                            btn.textContent = 'Оплачено';
                        }
                    }
                });
            });
        });

        container.querySelectorAll('[data-action="cancel"]').forEach(btn => {
            btn.addEventListener('click', () => {
                const card = btn.closest('.seller-order-card');
                const id = card.dataset.id;

                openConfirm({
                    title: 'Отменить заказ?',
                    text: 'Это действие нельзя отменить.',
                    okText: 'Отменить',
                    onConfirm: async () => {
                        btn.disabled = true;
                        btn.textContent = '...';
                        try {
                            await api.updateStatus(id, 'CANCELLED');
                            load();
                        } catch (e) {
                            alert('Ошибка: ' + e.message);
                            btn.disabled = false;
                            btn.textContent = 'Отменить';
                        }
                    }
                });
            });
        });
    }

    function openConfirm({ title, text, okText, onConfirm }) {
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

        okBtn.addEventListener('click', handleOk);
        closeEls.forEach(el => el.addEventListener('click', close));
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