import {API} from '../core/api.js';
import {state} from '../core/state.js';
import {formatMoney} from '../core/utils.js';

export function openEditTournamentModal(id, date, amount) {
    state.editingTournamentResultId = id;
    document.getElementById('editTournamentInfo').textContent = `${date} · ${formatMoney(amount)}`;
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
    } catch (e) {
    }
}

export function changePage(page) {
    state.currentSumPage = page;
    executeSum();
}

window.toggleTournamentMatches = async function (resultId, el) {
    const container = el.querySelector('.matches-container');
    if (!container) return;

    if (!container.classList.contains('hidden')) {
        container.classList.add('hidden');
        return;
    }

    try {
        const response = await fetch(`/api/tournament/matches/by-result/${resultId}`, {
            credentials: 'same-origin'
        });

        if (!response.ok) throw new Error('HTTP ' + response.status);

        const matches = await response.json();

        container.innerHTML = `
            <div class="matches-divider"></div>
            ${matches.map((m, i) => {
            const winnerName = m.winnerName || m.winnerShortName;
            const p1Name = m.player1Name || m.player1ShortName;
            const p2Name = m.player2Name || m.player2ShortName;
            const isPlayer1Winner = winnerName === p1Name;
            const isPlayer2Winner = winnerName === p2Name;

            return `
                <div class="match-card" style="animation-delay: ${i * 50}ms">
                    <div class="match-stage-badge">${getStageLabel(m.stage)}</div>
                    <div class="match-score-row-centered">
                        <span class="match-player-name ${isPlayer1Winner ? 'winner' : ''}">${p1Name}</span>
                        <span class="match-score-badge">${m.score || '—'}</span>
                        <span class="match-player-name ${isPlayer2Winner ? 'winner' : ''}">${p2Name}</span>
                    </div>
                </div>
                `;
        }).join('')}
        `;

        container.classList.remove('hidden');
    } catch (e) {
        console.error('Ошибка загрузки матчей:', e);
        container.innerHTML = '<p class="text-zinc-500 text-sm">Не удалось загрузить матчи</p>';
        container.classList.remove('hidden');
    }
};

function getStageLabel(stage) {
    switch (stage) {
        case 'GROUP':
            return 'Группа';
        case 'SEMIFINAL':
            return 'Полуфинал';
        case 'THIRD_PLACE':
            return 'За 3-е место';
        case 'FINAL':
            return 'Финал';
        default:
            return stage;
    }
}

export async function executeSum() {
    const start = document.getElementById('dateStart').value;
    const end = document.getElementById('dateEnd').value;
    const err = document.getElementById('sumError');
    const res = document.getElementById('actionResult');

    err.classList.add('hidden');
    if (!start && !end) {
        err.textContent = 'Выберите дату';
        err.classList.remove('hidden');
        return;
    }

    const params = {page: state.currentSumPage, size: 20};
    if (start) params.start = start;
    if (end) params.end = end;

    try {
        const data = await API.getSum(params);
        if (!data.tournaments || data.tournaments.length === 0) {
            res.innerHTML = `<div class="text-center py-8"><span class="text-4xl">📭</span><p class="text-gray-400 mt-3">Нет турниров</p></div>`;
            return;
        }

        // Сводная карточка в стиле Apple
        let html = `<div class="apple-card mb-4">
            <div class="grid grid-cols-3 gap-3">
                <div class="sum-stat">
                    <div class="sum-stat-label">Сумма</div>
                    <div class="sum-stat-value gold">${formatMoney(data.sum)}</div>
                </div>
                <div class="sum-stat">
                    <div class="sum-stat-label">Среднее</div>
                    <div class="sum-stat-value blue">${formatMoney(data.average)}</div>
                </div>
                <div class="sum-stat">
                    <div class="sum-stat-label">Турниры</div>
                    <div class="sum-stat-value">${data.count}</div>
                </div>
            </div>
        </div>`;

        // Список турниров
        html += '<div class="space-y-1.5">';
        data.tournaments.forEach((t, i) => {
            html += `
                <div class="apple-card tournament-row" onclick="toggleTournamentMatches(${t.resultId || 0}, this)">
                    <div class="flex items-center justify-between">
                        <div class="flex items-center gap-3">
                            <span class="tournament-number">${state.currentSumPage * 20 + i + 1}</span>
                            <span class="tournament-date">${t.date || '—'}</span>
                        </div>
                        <div class="flex items-center gap-3">
                            <span class="tournament-amount">${formatMoney(t.amount)}</span>
                            <button onclick="event.stopPropagation(); openEditTournamentModal(${t.resultId || 0},'${t.date || ''}',${t.amount})" class="edit-btn">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                            </button>
                        </div>
                    </div>
                    <div class="matches-container hidden"></div>
                </div>`;
        });
        html += '</div>';

        // Пагинация в стиле Apple
        if (data.totalPages > 1) {
            html += `<div class="flex items-center justify-center gap-4 mt-4">
                <button onclick="changePage(${state.currentSumPage - 1})" ${state.currentSumPage === 0 ? 'disabled' : ''} class="pagination-btn">Назад</button>
                <span class="pagination-info">${state.currentSumPage + 1} / ${data.totalPages}</span>
                <button onclick="changePage(${state.currentSumPage + 1})" ${state.currentSumPage >= data.totalPages - 1 ? 'disabled' : ''} class="pagination-btn">Вперёд</button>
            </div>`;
        }

        res.innerHTML = html;
    } catch (e) {
        res.innerHTML = '<p class="text-red-400 text-center py-6">Ошибка соединения</p>';
    }
}