window.CheckoutPage = (function () {
    const cart = window.CartStore;
    const ordersApi = window.OrdersApi;
    const toast = window.Toast;
    const SELECTED_KEY = 'pulsecore_cart_selected';

    const PICKUP_CITY = 'Краснодар';
    const PICKUP_ADDRESS = 'ул. Красная, 100';
    const PICKUP_PHONE = '+7 918 133-91-88';

    const DELIVERY_METHOD = 'PICKUP';
    let paymentMethod = 'YOOKASSA';

    function init() {
        render();
    }

    function getSelectedItems() {
        let ids = new Set();
        try {
            const raw = localStorage.getItem(SELECTED_KEY);
            if (raw) ids = new Set(JSON.parse(raw).map(String));
        } catch (e) {}

        const items = cart.getAll();
        return Object.values(items).filter(it => ids.has(String(it.variantId)));
    }

    function render() {
        const container = document.getElementById('checkoutContent');
        if (!container) return;

        const selected = getSelectedItems();

        if (selected.length === 0) {
            container.innerHTML = `
                <div class="cart-empty">
                    <div class="cart-empty-icon">📦</div>
                    <p class="cart-empty-text">Нет выбранных товаров</p>
                    <a href="#/cart" class="cart-empty-link">В корзину</a>
                </div>
            `;
            return;
        }

        const total = selected.reduce((sum, i) => sum + i.price * i.qty, 0);

        container.innerHTML = `
            <div class="checkout-layout">
                <form id="checkoutForm" class="checkout-form">
                    <div class="checkout-section">
                        <h2 class="checkout-section-title">Получатель</h2>
                        <div class="field">
                            <label for="coFirstName">Имя</label>
                            <input id="coFirstName" type="text" maxlength="100" placeholder="Иван">
                        </div>
                        <div class="field">
                            <label for="coLastName">Фамилия</label>
                            <input id="coLastName" type="text" maxlength="100" placeholder="Иванов">
                        </div>
                        <div class="field">
                            <label for="coMiddleName">Отчество</label>
                            <input id="coMiddleName" type="text" maxlength="100" placeholder="Иванович">
                        </div>
                        <div class="field">
                            <label for="coPhone">Телефон <span class="req">*</span></label>
                            <input id="coPhone" type="tel" maxlength="30" required placeholder="+7 999 123-45-67">
                        </div>
                    </div>

                    <div class="checkout-section">
                        <h2 class="checkout-section-title">Способ получения</h2>
                        <div class="checkout-radio-group">
                            <label class="checkout-radio">
                                <input type="radio" name="deliveryMethod" value="PICKUP" checked disabled>
                                <span>Самовывоз</span>
                            </label>
                        </div>
                    </div>

                    <div class="checkout-section">
                        <h2 class="checkout-section-title">Самовывоз</h2>
                        <p class="checkout-pickup-info">
                            <strong>${PICKUP_CITY}, ${PICKUP_ADDRESS}</strong><br>
                            Телефон: ${PICKUP_PHONE}<br>
                            Заберите заказ после подтверждения готовности.
                        </p>
                    </div>

                    <div class="checkout-section">
                        <h2 class="checkout-section-title">Способ оплаты</h2>
                        <div class="checkout-radio-group" id="paymentGroup">
                            <label class="checkout-radio">
                                <input type="radio" name="paymentMethod" value="YOOKASSA" checked>
                                <span>Онлайн (ЮKassa)</span>
                            </label>
                            <label class="checkout-radio">
                                <input type="radio" name="paymentMethod" value="ON_DELIVERY">
                                <span>При получении (наличные / перевод СБП)</span>
                            </label>
                        </div>
                    </div>

                    <div class="checkout-section">
                        <h2 class="checkout-section-title">Комментарий</h2>
                        <div class="field">
                            <textarea id="coComment" rows="3" maxlength="2000"
                                      placeholder="Позвонить за час"></textarea>
                        </div>
                    </div>

                    <button type="submit" class="checkout-submit" id="coSubmit">
                        Оформить заказ · ${formatPrice(total)} ₽
                    </button>
                    <p id="coMessage" class="message hidden"></p>
                </form>

                <div class="checkout-summary">
                    <h2 class="checkout-section-title">Заказ · ${selected.length}</h2>
                    <div class="checkout-items">
                        ${selected.map(renderItem).join('')}
                    </div>
                    <div class="checkout-total-row">
                        <span>Итого</span>
                        <span class="checkout-total">${formatPrice(total)} ₽</span>
                    </div>
                </div>
            </div>
        `;

        bindPayment();
        document.getElementById('checkoutForm').addEventListener('submit', onSubmit);
    }

    function bindPayment() {
        const group = document.getElementById('paymentGroup');
        group.querySelectorAll('input[name="paymentMethod"]').forEach(radio => {
            radio.addEventListener('change', () => {
                paymentMethod = radio.value;
            });
        });
    }

    function renderItem(item) {
        const img = item.image
            ? `<img src="${item.image}" alt="">`
            : `<div class="cart-item-placeholder">📷</div>`;

        const variantLine = buildVariantLine(item);

        return `
            <div class="checkout-item">
                <div class="checkout-item-image">${img}</div>
                <div class="checkout-item-body">
                    <span class="checkout-item-name">${escapeHtml(item.name)}</span>
                    ${variantLine}
                    <span class="checkout-item-qty">${item.qty} × ${formatPrice(item.price)} ₽</span>
                </div>
            </div>
        `;
    }

    function buildVariantLine(item) {
        const parts = [];
        if (item.size) parts.push(item.size);
        if (item.color) parts.push(item.color);

        if (parts.length === 0) return '';

        return `<span class="checkout-item-variant">${escapeHtml(parts.join(' · '))}</span>`;
    }

    async function onSubmit(e) {
        e.preventDefault();

        const msg = document.getElementById('coMessage');
        const btn = document.getElementById('coSubmit');

        toast.hide(msg);

        const phone = document.getElementById('coPhone').value.trim();
        if (!phone) {
            toast.show(msg, 'Телефон обязателен', false);
            return;
        }

        btn.disabled = true;
        btn.textContent = 'Создание заказа...';

        try {
            const selected = getSelectedItems();
            const itemIds = selected.map(it => it.id);

            const body = {
                itemIds,
                customerFirstName: document.getElementById('coFirstName').value.trim() || null,
                customerLastName: document.getElementById('coLastName').value.trim() || null,
                customerMiddleName: document.getElementById('coMiddleName').value.trim() || null,
                phone,
                comment: document.getElementById('coComment').value.trim() || null,
                paymentMethod,
                deliveryMethod: DELIVERY_METHOD
            };

            const order = await ordersApi.create(body);

            if (order.paymentConfirmationUrl) {
                window.location.href = order.paymentConfirmationUrl;
            } else {
                toast.show(msg, 'Заказ оформлен', true);
                setTimeout(() => window.location.hash = '#/orders', 800);
            }
        } catch (err) {
            if (err.problems && err.problems.length > 0) {
                toast.showPopupProblems(err.message, err.problems);
            } else {
                toast.show(msg, err.message || 'Ошибка', false);
            }
            btn.disabled = false;
            btn.textContent = 'Попробовать снова';
        }
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