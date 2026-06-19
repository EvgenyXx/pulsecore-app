import { SubscribeAPI } from '../core/subscribe-api.js';
import { formatMoney } from '../core/utils.js';

let selectedPlan = null;
let prices = {};

export async function loadPrices() {
    try {
        prices = await SubscribeAPI.getPrices();
        renderPlans();
        document.getElementById('pageSubtitle').textContent = 'Выберите тариф';
    } catch (e) {
        document.getElementById('pageSubtitle').textContent = 'Ошибка загрузки цен';
    }
}

function getMonthLabel(months) {
    if (months === 1) return '1 месяц';
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

function getDiscountPercent(price1, priceN, months) {
    const fullPrice = price1 * months;
    return Math.round((1 - priceN / fullPrice) * 100);
}

function renderPlans() {
    const container = document.getElementById('plansContainer');
    const entries = Object.entries(prices)
        .map(([months, price]) => ({ months: parseInt(months), price }))
        .sort((a, b) => a.months - b.months);

    if (entries.length === 0) {
        container.innerHTML = '<p class="text-zinc-500 text-center col-span-full">Нет доступных тарифов</p>';
        return;
    }

    const basePrice = entries[0].price;
    const mostPopular = entries.length > 1 ? entries[entries.length - 1] : entries[0];

    container.innerHTML = entries.map((plan, index) => {
        const isPopular = plan.months === mostPopular.months && entries.length > 1;
        const discount = plan.months > 1 ? getDiscountPercent(basePrice, plan.price, plan.months) : 0;
        const perMonth = getPricePerMonth(plan.price, plan.months);
        const fullPrice = basePrice * plan.months;
        const hasDiscount = plan.months > 1 && plan.price < fullPrice;

        return `
            <div class="plan-card text-center ${isPopular ? 'popular' : ''}" id="plan${plan.months}" onclick="selectPlan(${plan.months})">
                <div class="text-3xl mb-4">${index === 0 ? '⭐' : '🌟'}</div>
                <h3 class="text-lg font-bold text-white mb-1">${getMonthLabel(plan.months)}</h3>
                <p class="text-xs text-zinc-500 mb-3">
                    ${hasDiscount ? 'Экономия ' + discount + '%' : 'Полный доступ ко всем функциям'}
                </p>
                <p class="text-3xl font-bold amount-gold">${plan.price} ₽</p>
                <p class="text-xs text-zinc-500 mt-1">
                    ${hasDiscount ? '<span class="price-old">' + fullPrice + ' ₽</span> ' : ''}
                    ${perMonth} ₽ / месяц
                </p>
            </div>
        `;
    }).join('');

    document.getElementById('plansBlock').classList.remove('hidden');
}

export async function loadSubscription() {
    try {
        const sub = await SubscribeAPI.getSubscription();
        if (sub && sub.active) {
            document.getElementById('activeSubBlock').classList.remove('hidden');
            document.getElementById('plansBlock').classList.add('hidden');
            const exp = sub.expiresAt ? new Date(sub.expiresAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' }) : 'неизвестно';
            document.getElementById('activeSubText').textContent = `Действует до ${exp}`;
            document.getElementById('pageSubtitle').textContent = 'Активна';
        }
    } catch (e) {}
}

export function selectPlan(months) {
    selectedPlan = months;
    document.querySelectorAll('.plan-card').forEach(card => card.classList.remove('selected'));
    document.getElementById(`plan${months}`).classList.add('selected');
    document.getElementById('payBtn').disabled = false;
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