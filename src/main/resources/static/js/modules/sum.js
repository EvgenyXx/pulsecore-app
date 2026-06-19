import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { formatMoney } from '../core/utils.js';

export function openEditTournamentModal(id, date, amount) {
    state.editingTournamentResultId = id;
    document.getElementById('editTournamentInfo').textContent = `📅 ${date} | ${formatMoney(amount)}`;
    document.getElementById('editTournamentAmount').value = amount;
    document.getElementById('editTournamentError').classList.add('hidden');
    document.getElementById('editTournamentModal').classList.remove('hidden');
}

export function closeEditTournamentModal() {
    document.getElementById('editTournamentModal').classList.add('hidden');
}

export async function saveTournamentEdit() {
    const amount = parseFloat(document.getElementById('editTournamentAmount').value);
    if (!state.editingTournamentResultId || isNaN(amount)) return;
    try {
        await API.updateResult(state.editingTournamentResultId, amount, 0);
        closeEditTournamentModal();
        executeSum();
    } catch(e) {}
}

export function changePage(page) {
    state.currentSumPage = page;
    executeSum();
}

export async function executeSum() {
    const start = document.getElementById('dateStart').value;
    const end = document.getElementById('dateEnd').value;
    const err = document.getElementById('sumError');
    const res = document.getElementById('actionResult');

    err.classList.add('hidden');
    if (!start && !end) {
        err.textContent = '❌ Выберите дату';
        err.classList.remove('hidden');
        return;
    }

    const params = { page: state.currentSumPage, size: 20 };
    if (start) params.start = start;
    if (end) params.end = end;

    try {
        const data = await API.getSum(params);
        if (!data.tournaments || data.tournaments.length === 0) {
            res.innerHTML = `<div class="text-center py-8"><span class="text-4xl">📭</span><p class="text-gray-400 mt-3">Нет турниров</p></div>`;
            return;
        }

        let paginationHtml = '';
        if (data.totalPages > 1) {
            paginationHtml = `<div class="flex items-center justify-center gap-3 mt-4">
                <button onclick="changePage(${state.currentSumPage - 1})" ${state.currentSumPage === 0 ? 'disabled' : ''} class="px-4 py-2 rounded-xl bg-white/5 hover:bg-white/10 text-white text-sm disabled:opacity-30 disabled:cursor-not-allowed">← Назад</button>
                <span class="text-gray-400 text-xs">${state.currentSumPage + 1} / ${data.totalPages}</span>
                <button onclick="changePage(${state.currentSumPage + 1})" ${state.currentSumPage >= data.totalPages - 1 ? 'disabled' : ''} class="px-4 py-2 rounded-xl bg-white/5 hover:bg-white/10 text-white text-sm disabled:opacity-30 disabled:cursor-not-allowed">Вперёд →</button>
            </div>`;
        }

        // Статистика
        let html = `<div class="widget-card rounded-2xl p-4 mb-4">
            <div class="grid grid-cols-3 gap-3 text-center">
                <div>
                    <div class="text-xs text-gray-400">💰 Общая</div>
                    <div class="text-lg font-bold amount-gold">${formatMoney(data.sum)}</div>
                </div>
                <div>
                    <div class="text-xs text-gray-400">📊 Среднее</div>
                    <div class="text-lg font-bold text-blue-400">${formatMoney(data.average)}</div>
                </div>
                <div>
                    <div class="text-xs text-gray-400">🎯 Турниров</div>
                    <div class="text-lg font-bold text-amber-400">${data.count}</div>
                </div>
            </div>
        </div>`;

        // Список турниров
        html += '<div class="space-y-1.5">';
        data.tournaments.forEach((t, i) => {
            html += `
                <div class="widget-card rounded-xl p-3 flex items-center justify-between text-sm flex-row">
                    <div class="flex items-center gap-3">
                        <span class="text-gray-400 text-xs w-6">${state.currentSumPage * 20 + i + 1}.</span>
                        <span class="text-white">${t.date || '—'}</span>
                    </div>
                    <div class="flex items-center gap-3">
                        <span class="amount-gold font-medium">${formatMoney(t.amount)}</span>
                        <button onclick="openEditTournamentModal(${t.resultId || 0},'${t.date || ''}',${t.amount})" class="bg-gray-700 hover:bg-gray-600 text-white text-xs px-3 py-1.5 rounded-lg">✏️</button>
                    </div>
                </div>`;
        });
        html += '</div>';

        res.innerHTML = html + paginationHtml;
    } catch(e) {
        res.innerHTML = '<p class="text-red-400 text-center py-6">❌ Ошибка соединения</p>';
    }
}