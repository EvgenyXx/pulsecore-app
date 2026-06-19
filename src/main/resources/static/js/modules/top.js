import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { capitalizeName } from '../core/utils.js';

export async function loadTopWeek(league) {
    const panel = document.getElementById('topWeekPanel');
    const period = state.currentPeriod.toUpperCase();
    const periodLabel = period === 'WEEK' ? '7 дней' : period === 'MONTH' ? '30 дней' : '365 дней';
    const topTitle = period === 'WEEK' ? 'недели' : period === 'MONTH' ? 'за месяц' : 'за год';

    const leagues = ['A', 'B', 'C', 'D', 'SUPER_LEAGUE'];
    const labels = { 'A': 'A', 'B': 'B', 'C': 'C', 'D': 'D', 'SUPER_LEAGUE': 'Супер' };

    let html = `
        <div class="flex items-center justify-between mb-3">
            <h3 class="font-bold text-white text-sm">🏆 Топ ${topTitle}</h3>
            <span class="cursor-pointer text-zinc-400 hover:text-red-400 text-lg" onclick="toggleTopWeek()">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </span>
        </div>
        <div class="flex gap-1.5 flex-wrap mb-3">
            <span class="league-tab period-tab ${state.currentPeriod === 'week' ? 'active' : ''}" onclick="switchPeriod('week')">Неделя</span>
            <span class="league-tab period-tab ${state.currentPeriod === 'month' ? 'active' : ''}" onclick="switchPeriod('month')">Месяц</span>
            <span class="league-tab period-tab ${state.currentPeriod === 'year' ? 'active' : ''}" onclick="switchPeriod('year')">Год</span>
        </div>
        <div class="flex gap-1.5 flex-wrap mb-3" id="leagueTabs">
            <span class="league-tab ${!league ? 'active' : ''}" onclick="switchLeague(null)">Все</span>
    `;
    leagues.forEach(l => {
        const isActive = l === league;
        const isMy = l === state.primaryLeague;
        html += `<span class="league-tab ${isActive ? 'active' : ''} ${isMy ? 'my-league' : ''}" onclick="switchLeague('${l}')">${labels[l]}</span>`;
    });
    html += '</div>';

    html += '<div id="topWeekList" class="min-h-[200px]">';

    try {
        const data = await API.getTop(period, league);
        if (!data.top5 || data.top5.length === 0) {
            html += `<div class="text-center py-6"><span class="text-3xl">📭</span><p class="text-zinc-400 text-sm mt-2">Нет данных за ${periodLabel}</p></div>`;
        } else {
            const medals = ['🥇', '🥈', '🥉'];
            data.top5.forEach((p, i) => {
                const medal = i < 3 ? medals[i] : `${i+1}.`;
                const isMe = (i + 1) === data.playerPosition;
                const bgClass = isMe ? 'bg-indigo-500/10 border-l-2 border-indigo-400 rounded-lg' : '';
                const name = capitalizeName(p.name || '') + (isMe ? ' 👈' : '');
                html += `
                    <div class="flex items-center gap-3 py-2 px-3 ${bgClass}">
                        <span class="text-lg w-8 text-center">${medal}</span>
                        <span class="text-white text-sm font-medium flex-1">${name}</span>
                        <span class="text-zinc-400 text-xs">${p.tournaments} тур.</span>
                    </div>
                `;
            });

            if (data.playerPosition > 5) {
                html += `
                    <div class="border-t border-white/10 mt-3 pt-3 flex items-center gap-3 py-2 px-3 rounded-lg bg-indigo-500/10 border-l-2 border-indigo-400">
                        <span class="text-lg w-8 text-center text-zinc-400">${data.playerPosition}.</span>
                        <span class="text-white text-sm font-medium flex-1">👈 Вы</span>
                        <span class="text-zinc-400 text-xs">${data.playerTournaments} тур.</span>
                    </div>
                `;
            }
        }
    } catch (e) {
        html += `<div class="text-center py-6"><p class="text-red-400 text-sm">❌ Ошибка</p></div>`;
    }

    html += '</div>';
    panel.innerHTML = html;
}

export async function loadTopWeekPreview() {
    try {
        const data = await API.getTop('WEEK', state.primaryLeague || 'A');
        if (data?.top5?.length > 0) {
            document.getElementById('topWeekBanner').classList.remove('hidden');
            document.getElementById('topWeekPreview').textContent = 'Нажмите чтобы посмотреть';
        }
    } catch (e) {}
}

export function toggleTopWeek() {
    const panel = document.getElementById('topWeekPanel');
    const arrow = document.getElementById('topWeekArrow');
    if (panel.classList.contains('hidden')) {
        panel.classList.remove('hidden');
        arrow.textContent = '▲';
        loadTopWeek(null);
    } else {
        panel.classList.add('hidden');
        arrow.textContent = '▼';
    }
}

export function switchLeague(league) {
    loadTopWeek(league);
}

export function switchPeriod(period) {
    state.currentPeriod = period;
    const al = document.querySelector('#leagueTabs .league-tab.active');
    const lg = al && al.textContent !== 'Все' ? al.textContent : null;
    loadTopWeek(lg);
}