import {API} from '../core/api.js';
import {state} from '../core/state.js';
import {capitalizeName} from '../core/utils.js';

function isPro() {
    const badge = document.getElementById('proBadge');
    return badge && !badge.classList.contains('hidden');
}

export async function loadTopWeek(league) {
    const panel = document.getElementById('topWeekPanel');
    if (!panel) return;

    const period = state.currentPeriod.toUpperCase();
    const periodLabel = period === 'WEEK' ? '7 дней' : period === 'MONTH' ? '30 дней' : '365 дней';

    if (!isPro()) {
        panel.innerHTML = `
            <div class="text-center py-12">
                <div class="w-16 h-16 rounded-full bg-indigo-500/10 flex items-center justify-center mx-auto mb-5">
                    <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                </div>
                <h3 class="text-[17px] font-semibold text-white mb-1.5">Требуется подписка</h3>
                <p class="text-[13px] text-zinc-500 mb-5">Оформите подписку чтобы видеть топ игроков</p>
                <a href="/subscribe" class="inline-block bg-indigo-600 hover:bg-indigo-500 text-white font-medium rounded-full px-7 py-2.5 text-[15px] transition-all">Оформить подписку</a>
            </div>`;
        return;
    }

    const leagues = ['A', 'B', 'C', 'D', 'SUPER_LEAGUE'];
    const labels = {'A': 'A', 'B': 'B', 'C': 'C', 'D': 'D', 'SUPER_LEAGUE': 'Супер'};

    const activeLeagueIndex = league === null ? 0 : leagues.indexOf(league) + 1;
    const existingList = document.getElementById('topWeekList');

    let html = `
        <div class="flex items-center gap-3 mb-5">
            <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-amber-400/20 to-amber-600/10 flex items-center justify-center flex-shrink-0">
                <span class="text-lg">🏆</span>
            </div>
            <div>
                <h3 class="text-[17px] font-semibold text-white tracking-tight">Зал славы</h3>
                <p class="text-[12px] text-zinc-500">Топ игроков</p>
            </div>
        </div>

        <div class="period-switcher mb-4">
            <div class="period-slider ${state.currentPeriod === 'week' ? 'pos-0' : state.currentPeriod === 'month' ? 'pos-1' : 'pos-2'}"></div>
            <span class="period-pill ${state.currentPeriod === 'week' ? 'active' : ''}" onclick="switchPeriod('week')">Неделя</span>
            <span class="period-pill ${state.currentPeriod === 'month' ? 'active' : ''}" onclick="switchPeriod('month')">Месяц</span>
            <span class="period-pill ${state.currentPeriod === 'year' ? 'active' : ''}" onclick="switchPeriod('year')">Год</span>
        </div>
        
        <div class="league-switcher mb-5">
            <div class="league-slider pos-${activeLeagueIndex}"></div>
            <span class="league-pill ${!league ? 'active' : ''}" onclick="switchLeague(null)">Все</span>
    `;

    leagues.forEach(l => {
        const isActive = l === league;
        const isMy = l === state.primaryLeague;
        html += `<span class="league-pill ${isActive ? 'active' : ''} ${isMy ? 'my' : ''}" onclick="switchLeague('${l}')">${labels[l]}</span>`;
    });
    html += '</div>';
    html += '<div id="topWeekList" class="top-list-container">';

    try {
        const data = await API.getTop(period, league);
        if (!data.top5 || data.top5.length === 0) {
            html += `<div class="text-center py-10">
                <span class="text-4xl">📭</span>
                <p class="text-[13px] text-zinc-500 mt-3">Нет данных за ${periodLabel}</p>
            </div>`;
        } else {
            data.top5.forEach((p, i) => {
                const isMe = (i + 1) === data.playerPosition;
                const name = capitalizeName(p.name || '');

                html += `
                    <div class="rank-card ${isMe ? 'mine' : ''}">
                        <div class="rank-number">${i + 1}</div>
                        <div class="flex-1 min-w-0">
                            <div class="flex items-center justify-between gap-2">
<span class="text-[15px] font-medium text-white truncate">${name}</span>
                                <span class="text-[13px] font-semibold text-zinc-400">${p.tournaments}</span>
                            </div>
                        </div>
                    </div>
                `;
            });

            if (data.playerPosition > 5) {
                html += `
                    <div class="rank-card mine mt-3">
                        <div class="rank-number">${data.playerPosition}</div>
                        <div class="flex-1 min-w-0">
                            <div class="flex items-center justify-between gap-2">
                                <span class="text-[15px] font-medium text-white">Вы</span>
                                <span class="text-[13px] font-semibold text-zinc-400">${data.playerTournaments}</span>
                            </div>
                        </div>
                    </div>
                `;
            }
        }
    } catch (e) {
        html += `<div class="text-center py-10">
            <p class="text-[13px] text-red-400">Ошибка загрузки</p>
        </div>`;
    }

    html += '</div>';

    if (existingList) {
        existingList.style.transition = 'opacity 0.15s ease';
        existingList.style.opacity = '0';

        setTimeout(() => {
            panel.innerHTML = html;
            const newList = document.getElementById('topWeekList');
            newList.style.opacity = '0';
            newList.style.transition = 'opacity 0.25s ease';

            requestAnimationFrame(() => {
                newList.style.opacity = '1';
            });
        }, 150);
    } else {
        panel.innerHTML = html;
    }
}

export function switchLeague(league) {
    const leagues = ['A', 'B', 'C', 'D', 'SUPER_LEAGUE'];
    const activeIndex = league === null ? 0 : leagues.indexOf(league) + 1;
    const slider = document.querySelector('.league-slider');
    if (slider) {
        slider.className = `league-slider pos-${activeIndex}`;
    }

    document.querySelectorAll('.league-pill').forEach(pill => {
        pill.classList.remove('active');
    });
    if (event?.target) {
        event.target.classList.add('active');
    }

    loadTopWeek(league);
}

export function switchPeriod(period) {
    state.currentPeriod = period;
    const slider = document.querySelector('.period-slider');
    if (slider) {
        slider.className = `period-slider ${period === 'week' ? 'pos-0' : period === 'month' ? 'pos-1' : 'pos-2'}`;
    }

    document.querySelectorAll('.period-pill').forEach(pill => {
        pill.classList.remove('active');
    });
    if (event?.target) {
        event.target.classList.add('active');
    }

    const al = document.querySelector('.league-pill.active');
    const lg = al && al.textContent !== 'Все' ? al.textContent : null;
    loadTopWeek(lg);
}

window.loadTopWeek = loadTopWeek;
window.switchLeague = switchLeague;
window.switchPeriod = switchPeriod;