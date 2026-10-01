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

        return `
            <div class="seller-order-card" data-id="${order.id}">
                <div class="seller-order-header">
                    <div>
                        <div class="seller-order-number">Заказ №${order.id}</div>
                        <div class="seller-order-date">${formatDate(order.createdAt)}</div>
                    </div>
                    <div class="seller-order-badges">
                        ${badgePayment(order.paymentStatus)}
                        ${badgeStatus(order.status)}
                    </div>
                </div>

                <div class="seller-order-items">${items}</div>

                <div class="seller-order-customer">
                    <div class="seller-order-row"><span>Телефон</span><span>${escapeHtml(order.deliveryPhone)}</span></div>
                    ${order.customerName ? `<div class="seller-order-row"><span>Имя</span><span>${escapeHtml(order.customerName)}</span></div>` : ''}
                    <div class="seller-order-row"><span>Город</span><span>${escapeHtml(order.deliveryCity)}</span></div>
                    <div class="seller-order-row"><span>ПВЗ</span><span>${escapeHtml(order.deliveryStreet)}</span></div>
                    ${order.comment ? `<div class="seller-order-row"><span>Комментарий</span><span>${escapeHtml(order.comment)}</span></div>` : ''}
                </div>

                <div class="seller-order-footer">
                    <div class="seller-order-total">${formatPrice(order.totalPrice)} ₽</div>
                    <div class="seller-order-actions">
                        ${renderActions(order.status)}
                        <button class="btn-cancel" data-action="cancel" data-id="${order.id}">Отменить</button>
                    </div>
                </div>
            </div>
        `;
    }

    function renderActions(status) {
        if (status === 'CONFIRMED') {
            return `<button class="btn-action" data-action="advance" data-next="ASSEMBLED">Собран</button>`;
        }
        if (status === 'ASSEMBLED') {
            return `<button class="btn-action" data-action="advance" data-next="SHIPPED">Отправлен</button>`;
        }
        if (status === 'SHIPPED') {
            return `<button class="btn-action" data-action="advance" data-next="DONE">Получен</button>`;
        }
        return '';
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

    function badgePayment(s) {
        const labels = { PENDING: 'Не оплачен', PAID: 'Оплачен', CANCELLED: 'Отменён' };
        const cls = { PENDING: 'pending', PAID: 'paid', CANCELLED: 'cancelled' };
        return `<span class="seller-badge payment-${cls[s] || ''}">${labels[s] || s}</span>`;
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

        container.querySelectorAll('[data-action="cancel"]').forEach(btn => {
            btn.addEventListener('click', async () => {
                if (!confirm('Отменить заказ?')) return;
                const card = btn.closest('.seller-order-card');
                const id = card.dataset.id;

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
            });
        });
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