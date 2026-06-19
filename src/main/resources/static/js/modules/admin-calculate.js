import { AdminAPI } from '../core/admin-api.js';
import { formatMoney } from '../core/utils.js';

export async function adminCalculate() {
    const name = document.getElementById('calcName').value.trim();
    const startDate = document.getElementById('calcStartDate').value;
    const endDate = document.getElementById('calcEndDate').value;
    const btn = document.getElementById('calcBtn');
    const errorEl = document.getElementById('calcError');
    const resultEl = document.getElementById('calcResult');
    const loadingEl = document.getElementById('calcLoading');

    errorEl.classList.add('hidden');
    resultEl.classList.add('hidden');

    if (!name || !startDate) {
        errorEl.textContent = '❌ Заполните имя и дату начала';
        errorEl.classList.remove('hidden');
        return;
    }

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-sm"></span> Считаем...';
    loadingEl.classList.remove('hidden');

    try {
        const data = await AdminAPI.calculatePlayer({ name, startDate, endDate: endDate || startDate });

        if (!data.tournaments || data.tournaments.length === 0) {
            errorEl.textContent = 'ℹ️ Турниры не найдены';
            errorEl.classList.remove('hidden');
            return;
        }

        document.getElementById('calcTotal').textContent = formatMoney(data.totalAmount);
        document.getElementById('calcCount').textContent = data.tournamentCount;

        const tbody = document.getElementById('calcTableBody');
        tbody.innerHTML = data.tournaments.map((t, i) => {
            let d = t.date || '—';
            if (d.includes(' ')) d = d.split(' ')[0];
            try { const dt = new Date(d); if (!isNaN(dt.getTime())) d = dt.toLocaleDateString('ru-RU'); } catch (e) {}
            const cls = t.hasRemoved
                ? 'result-row hover:bg-white/5 border-l-2 border-amber-500/60 bg-amber-500/5'
                : 'result-row hover:bg-white/5';
            return `<tr class="${cls}" style="animation-delay:${i * 0.05}s">
                <td class="text-gray-400">${i + 1}</td>
                <td class="text-white text-sm">${d}</td>
                <td class="text-gray-300 text-sm">${t.tournamentTitle || '—'}</td>
                <td class="${t.hasRemoved ? 'text-amber-400' : 'text-emerald-400'} font-medium">${formatMoney(t.amount)}</td>
            </tr>`;
        }).join('');

        resultEl.classList.remove('hidden');
    } catch (e) {
        errorEl.textContent = '❌ Ошибка';
        errorEl.classList.remove('hidden');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '🧮 Посчитать';
        loadingEl.classList.add('hidden');
    }
}