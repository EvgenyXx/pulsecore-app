import { AdminAPI } from '../core/admin-api.js';

export async function loadCurrentPrices() {
    const msg = document.getElementById('pricesMsg');
    msg.classList.add('hidden');

    try {
        const prices = await AdminAPI.getPrices();
        document.getElementById('price1Input').value = prices[1] || 0;
        document.getElementById('price2Input').value = prices[2] || 0;
        msg.textContent = '✅ Текущие цены загружены';
        msg.className = 'text-xs text-center mt-3 text-emerald-400';
        msg.classList.remove('hidden');
    } catch (e) {
        msg.textContent = '❌ Ошибка загрузки цен';
        msg.className = 'text-xs text-center mt-3 text-red-400';
        msg.classList.remove('hidden');
    }
}

export async function updatePrices() {
    const msg = document.getElementById('pricesMsg');
    const btn = document.getElementById('updatePricesBtn');
    const p1 = parseInt(document.getElementById('price1Input').value);
    const p2 = parseInt(document.getElementById('price2Input').value);

    if (!p1 || !p2 || p1 < 1 || p2 < 1) {
        msg.textContent = '❌ Введите корректные цены';
        msg.className = 'text-xs text-center mt-3 text-red-400';
        msg.classList.remove('hidden');
        return;
    }

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-sm"></span> Сохраняем...';
    msg.classList.add('hidden');

    try {
        await AdminAPI.updatePrices({ '1': p1, '2': p2 });
        msg.textContent = '✅ Цены обновлены';
        msg.className = 'text-xs text-center mt-3 text-emerald-400';
        msg.classList.remove('hidden');
    } catch (e) {
        msg.textContent = '❌ Ошибка';
        msg.className = 'text-xs text-center mt-3 text-red-400';
        msg.classList.remove('hidden');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '💾 Сохранить цены';
    }
}