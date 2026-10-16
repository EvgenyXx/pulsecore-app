import { SubscribeAPI } from '../core/subscribe-api.js';
import { formatMoney } from '../core/utils.js';

let selectedPlan = null;
let prices = {};
let isActive = false;

export async function loadPrices() {
    try {
        prices = (await SubscribeAPI.getPrices()).prices;
        renderPlans();
    } catch (e) {
        document.getElementById('pageSubtitle').textContent = 'Ошибка загрузки цен';
    }
}

function getMonthLabel(months) {
    if (months === 1) return 'Месяц';
    const lastDigit = months % 10;
    const lastTwo = months % 100;
    if (lastTwo >= 11 && lastTwo <= 14) return months + ' месяцев';
    if (lastDigit === 1) return months + ' месяц';
    if (lastDigit >= 2 && lastDigit <= 4) return months + ' месяца';
    return months + ' месяцев';
}

function getPricePerMonth(total, months) {
    return Math.round(total / months);
}

function renderPlans() {
    const container = document.getElementById('plansContainer');
    const entries = Object.entries(prices)
        .map(([months, price]) => ({ months: parseInt(months), price }))
        .sort((a, b) => a.months - b.months);

    if (entries.length === 0) {
        container.innerHTML = '<p class="text-zinc-500 text-center" style="grid-column: 1/-1;">Нет доступных тарифов</p>';
        return;
    }

    container.innerHTML = entries.map(plan => {
        const perMonth = getPricePerMonth(plan.price, plan.months);

        return `
            <div class="plan-card" id="plan${plan.months}" onclick="selectPlan(${plan.months})">
                <span class="plan-check">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="20 6 9 17 4 12"/>
                    </svg>
                </span>
                <div class="plan-name">${getMonthLabel(plan.months)}</div>
                <div class="plan-price">${plan.price}<span class="plan-price-cur">₽</span></div>
                <div class="plan-period">${perMonth} ₽ / мес</div>
            </div>
        `;
    }).join('');

    document.getElementById('plansBlock').classList.remove('hidden');
}

export async function loadSubscription() {
    try {
        const sub = await SubscribeAPI.getSubscription();
        isActive = !!(sub && sub.activeNow);

        const status = document.getElementById('subStatus');
        const subtitle = document.getElementById('pageSubtitle');
        const plansTitle = document.getElementById('plansTitle');

        if (isActive) {
            const exp = sub.expiresAt
                ? new Date(sub.expiresAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })
                : '—';

            status.className = 'sub-status is-active';
            status.innerHTML = `
                <div class="sub-status-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="20 6 9 17 4 12"/>
                    </svg>
                </div>
                <div class="sub-status-body">
                    <p class="sub-status-title">Подписка активна</p>
                    <p class="sub-status-text">Действует до ${exp}</p>
                </div>
            `;

            subtitle.textContent = 'Продлите подписку';
            plansTitle.textContent = 'Продлить на';
        } else {
            status.className = 'sub-status is-inactive';
            status.innerHTML = `
                <div class="sub-status-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="2" y="5" width="20" height="14" rx="2"/>
                        <line x1="2" y1="10" x2="22" y2="10"/>
                        <line x1="6" y1="15" x2="10" y2="15"/>
                    </svg>
                </div>
                <div class="sub-status-body">
                    <p class="sub-status-title">Оформите PRO</p>
                    <p class="sub-status-text">Аналитика, H2H, live-трансляции</p>
                </div>
            `;

            subtitle.textContent = 'Оформите подписку';
            plansTitle.textContent = 'Выберите тариф';
        }

        updatePayButton();
    } catch (e) {}
}

function updatePayButton() {
    const wrapper = document.getElementById('payBtnWrapper');
    const btn = document.getElementById('payBtn');
    if (!wrapper || !btn) return;

    if (!selectedPlan) {
        wrapper.classList.add('hidden');
        return;
    }

    const price = prices[selectedPlan];
    const action = isActive ? 'Продлить' : 'Оформить';
    btn.textContent = price ? `${action} · ${formatMoney(price)}` : action;
    btn.disabled = false;
    wrapper.classList.remove('hidden');
}

export function selectPlan(months) {
    selectedPlan = months;

    document.querySelectorAll('.plan-card').forEach(card => card.classList.remove('selected'));
    document.getElementById(`plan${months}`)?.classList.add('selected');

    updatePayButton();
}

export async function handlePay() {
    if (!selectedPlan) return;
    try {
        const data = await SubscribeAPI.createPayment(selectedPlan);
        window.location.href = data.confirmationUrl;
    } catch (e) {
        alert('Ошибка при создании платежа');
    }
}

export async function logout() {
    await SubscribeAPI.logout();
    window.location.href = '/';
}