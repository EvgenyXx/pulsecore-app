import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { formatMoney, formatDateShort, capitalizeName } from '../core/utils.js';

let dashboardLoaded = false;

// Раскрытие матчей турнира в стиле Apple
window.toggleTournamentMatches = async function(resultId, el) {
    const container = el.querySelector('.matches-container');
    if (!container) return;

    if (!container.classList.contains('hidden')) {
        container.style.transition = 'opacity 0.2s ease, transform 0.25s cubic-bezier(0.25, 0.1, 0.25, 1)';
        container.style.opacity = '0';
        container.style.transform = 'translateY(-8px)';

        setTimeout(() => {
            container.classList.add('hidden');
            container.style.opacity = '';
            container.style.transform = '';
            container.style.transition = '';
        }, 250);
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
            ${matches.map((m, i) => `
                <div class="match-card" style="animation-delay: ${i * 60}ms">
                    <div class="flex items-center justify-between gap-3 mb-2">
                        <span class="match-stage-badge">${getStageLabel(m.stage)}</span>
                        <span class="match-score-badge">${m.score || '—'}</span>
                    </div>
                    <div class="match-players-row">
                        <div class="match-player">
                            <span class="match-player-name">${m.player1Name}</span>
                            ${m.winnerName === m.player1Name ? '<span class="match-winner-dot"></span>' : ''}
                        </div>
                        <div class="match-player">
                            <span class="match-player-name">${m.player2Name}</span>
                            ${m.winnerName === m.player2Name ? '<span class="match-winner-dot"></span>' : ''}
                        </div>
                    </div>
                </div>
            `).join('')}
        `;

        container.classList.remove('hidden');
        container.style.opacity = '0';
        container.style.transform = 'translateY(-8px)';
        container.style.transition = 'opacity 0.25s ease, transform 0.35s cubic-bezier(0.25, 0.1, 0.25, 1)';

        requestAnimationFrame(() => {
            container.style.opacity = '1';
            container.style.transform = 'translateY(0)';
        });
    } catch (e) {
        console.error('Ошибка загрузки матчей:', e);
        container.innerHTML = '<p class="text-zinc-500 text-sm py-3">Не удалось загрузить матчи</p>';
        container.classList.remove('hidden');
    }
};

function getStageLabel(stage) {
    switch (stage) {
        case 'GROUP': return 'Группа';
        case 'SEMIFINAL': return 'Полуфинал';
        case 'THIRD_PLACE': return 'За 3-е место';
        case 'FINAL': return 'Финал';
        default: return stage;
    }
}

// Загрузка бейджа отчётов
async function loadReportBadge() {
    try {
        const res = await fetch('/api/tournament/reports/pending', { credentials: 'same-origin' });
        const reports = await res.json();
        document.getElementById('reportBadge').innerHTML = renderReportBadge(reports);
    } catch(e) {}
}

function renderReportBadge(reports) {
    const pending = reports.filter(r => r.status === 'PENDING');
    if (pending.length > 0) {
        return `<span class="pro-badge report-badge cursor-pointer" onclick="openScheduledReports()">
            <svg class="report-badge-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                <polyline points="22,6 12,13 2,6"/>
            </svg>
            <span class="report-badge-count">${pending.length}</span>
        </span>`;
    }
    return '';
}

export async function loadDashboardWidgets() {
    const container = document.getElementById('dashboardWidgets');

    try {
        const data = await API.getDashboard(state.playerId);
        state.primaryLeague = data.primaryLeague || 'A';

        document.getElementById('proBadge').classList.remove('hidden');
        document.getElementById('pushToggleContainer')?.classList.remove('hidden');
        if (typeof checkPushStatus === 'function') checkPushStatus();
        if (typeof loadOnlineCount === 'function') loadOnlineCount();

        loadReportBadge();

        const lastHtml = data.lastResult
            ? `<div class="widget-card apple-card" onclick="toggleTournamentMatches(${data.lastResult.resultId || 0}, this)">
                    <div class="flex items-center gap-3 mb-4">
                        <div class="w-9 h-9 rounded-lg bg-indigo-500/10 flex items-center justify-center flex-shrink-0">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20V10"/><path d="M18 20V4"/><path d="M6 20v-4"/></svg>
                        </div>
                        <div>
                            <h3 class="text-[15px] font-semibold text-white tracking-tight">Последний результат</h3>
                            <p class="text-[11px] text-zinc-500">${data.lastResult.date}</p>
                        </div>
                        <svg class="ml-auto text-zinc-500 transition-transform duration-300" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"/></svg>
                    </div>
                    <p class="text-[28px] font-bold text-transparent bg-clip-text bg-gradient-to-r from-amber-300 to-amber-500">${formatMoney(data.lastResult.amount)}</p>
                    <div class="matches-container hidden mt-3"></div>
                </div>`
            : `<div class="widget-card apple-card items-center justify-center">
                    <div class="text-center">
                        <span class="text-3xl">📭</span>
                        <p class="text-zinc-400 text-sm mt-2">Нет результатов</p>
                    </div>
                </div>`;

        container.innerHTML = lastHtml;
        dashboardLoaded = true;
    } catch (e) {
        container.innerHTML = `
            <div class="col-span-full widget-card rounded-2xl p-8 text-center">
                <div class="w-14 h-14 rounded-full bg-indigo-500/10 flex items-center justify-center mx-auto mb-4">
                    <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                </div>
                <h3 class="text-lg font-bold text-white mb-2">Требуется подписка</h3>
                <p class="text-zinc-400 text-sm mb-5">Оформите подписку</p>
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
    const homePage = document.getElementById('homePage');
    const actionPage = document.getElementById('actionPage');
    actionPage.style.opacity = '0';
    actionPage.style.transition = 'opacity 0.12s ease';
    setTimeout(() => {
        actionPage.classList.add('hidden');
        homePage.style.display = 'block';
        homePage.style.opacity = '0';
        homePage.style.transition = 'opacity 0.12s ease';
        requestAnimationFrame(() => { homePage.style.opacity = '1'; });
    }, 120);
    highlightNav('nav-home');
}

export function highlightNav(id) {
    document.querySelectorAll('.nav-item').forEach(e => e.classList.remove('active'));
    document.getElementById(id)?.classList.add('active');
}