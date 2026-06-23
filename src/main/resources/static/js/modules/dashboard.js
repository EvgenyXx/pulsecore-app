import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { formatMoney, formatDateShort, capitalizeName } from '../core/utils.js';

export async function loadDashboardWidgets() {
    const container = document.getElementById('dashboardWidgets');
    container.innerHTML = `<div class="col-span-full flex flex-col items-center py-16 text-zinc-400"><div class="spinner mb-4"></div><p>Загрузка...</p></div>`;

    try {
        const data = await API.getDashboard(state.playerId);
        state.primaryLeague = data.primaryLeague || 'A';

        document.getElementById('proBadge').classList.remove('hidden');
        document.getElementById('subStatusText').textContent = 'Подписка до ' + new Date(data.subscription.expiresAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
        document.getElementById('pushToggleContainer')?.classList.remove('hidden');
        checkPushStatus();
        loadOnlineCount();

        const lastHtml = data.lastResult
            ? `<div class="widget-card" onclick="window.open('${data.lastResult.tournamentLink}','_blank')"><div class="flex items-center gap-2 mb-1"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20V10"/><path d="M18 20V4"/><path d="M6 20v-4"/></svg><h3 class="font-bold text-sm text-indigo-300">Последний результат</h3></div><p class="text-2xl font-extrabold amount-gold mt-1">${formatMoney(data.lastResult.amount)}</p><p class="text-xs text-zinc-500 mt-auto pt-1">📅 ${data.lastResult.date}</p></div>`
            : `<div class="widget-card items-center justify-center"><div class="text-center"><span class="text-3xl">📭</span><p class="text-zinc-400 text-sm mt-2">Нет результатов</p></div></div>`;

        let lineupHtml = '<div class="widget-card"><div class="flex items-center gap-2 mb-3"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg><h3 class="font-bold text-sm text-white">Ближайшие турниры</h3></div><div class="flex-1 space-y-2">';

        if (data.upcomingLineups?.length > 0) {
            const dates = [...new Set(data.upcomingLineups.map(l => l.date))];
            dates.slice(0, 2).forEach((date, idx) => {
                const label = idx === 0 ? 'Сегодня' : 'Завтра';
                const color = idx === 0 ? 'indigo' : 'blue';
                const items = data.upcomingLineups.filter(l => l.date === date);
                lineupHtml += `<div class="text-xs font-bold text-${color}-400 uppercase tracking-wider mb-2">📌 ${label}, ${formatDateShort(date)}</div>`;
                if (items.some(l => l.inLineup)) {
                    items.filter(l => l.inLineup).forEach(l => {
                        const players = l.players ? l.players.split(', ').map(p => capitalizeName(p)).join(' • ') : '';
                        lineupHtml += `<div class="bg-${color}-500/5 rounded-lg p-2 border border-${color}-500/10 mb-2"><div class="flex items-center justify-between"><span class="text-white text-xs font-medium">⏰ ${l.time || '??:??'}</span><span class="text-[9px] font-bold bg-${color}-500/20 text-${color}-400 px-2 py-0.5 rounded-full">🏆 ${l.league || '?'}</span></div><p class="text-[11px] text-zinc-400 mt-1">👥 ${players || '—'}</p><p class="text-[10px] text-indigo-400 mt-1">✅ Вы в составе</p></div>`;
                    });
                } else {
                    lineupHtml += `<div class="flex flex-col items-center py-3 text-zinc-500"><span class="text-xl mb-1">😴</span><p class="text-xs">Пока не играете</p></div>`;
                }
            });
        } else {
            lineupHtml += `<div class="text-center py-4 text-zinc-500"><span class="text-2xl">📭</span><p class="text-xs">Составы не загружены</p></div>`;
        }
        lineupHtml += `</div></div>`;

        container.innerHTML = lastHtml + lineupHtml;
    } catch (e) {
        container.innerHTML = `
            <div class="col-span-full widget-card rounded-2xl p-8 text-center">
                <div class="w-14 h-14 rounded-full bg-indigo-500/10 flex items-center justify-center mx-auto mb-4">
                    <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                </div>
                <h3 class="text-lg font-bold text-white mb-2">Требуется подписка</h3>
                <p class="text-zinc-400 text-sm mb-5">Оформите подписку чтобы открыть все функции</p>
                <a href="/subscribe" class="inline-block bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-xl px-6 py-3 text-sm transition-all">Оформить подписку</a>
            </div>`;
    }
}

async function loadOnlineCount() {
    try {
        const res = await fetch('/api/online');
        if (res.ok) {
            const data = await res.json();
            document.getElementById('onlineCount').textContent = data.online;
            document.getElementById('onlineCounter').classList.remove('hidden');
        }
    } catch(e) {}
}
setInterval(loadOnlineCount, 10000);

export function goHome() {
    document.getElementById('homePage').style.display = 'block';
    document.getElementById('actionPage').classList.add('hidden');
    highlightNav('nav-home');
    loadDashboardWidgets();
}

export function highlightNav(id) {
    document.querySelectorAll('.nav-item').forEach(e => e.classList.remove('active'));
    document.getElementById(id)?.classList.add('active');
}