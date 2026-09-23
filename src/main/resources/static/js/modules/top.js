import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { capitalizeName } from '../core/utils.js';

const LEAGUES = ['A', 'B', 'C', 'D', 'SUPER_LEAGUE'];
const LABELS = {'A': 'A', 'B': 'B', 'C': 'C', 'D': 'D', 'SUPER_LEAGUE': 'Супер'};

// ============ ПОЛНАЯ ЗАГРУЗКА (первый раз) ============

export async function loadTopWeek(league) {
    const panel = document.getElementById('topWeekPanel');
    if (!panel) return;

    state.currentLeague = league;

    const period = state.currentPeriod.toUpperCase();
    const activeLeagueIndex = league === null ? 0 : LEAGUES.indexOf(league) + 1;

    let html = `
        <div class="flex items-center gap-3 mb-5">
            <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-amber-400/20 to-amber-600/10 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fbbf24" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            </div>
            <div>
                <h3 class="text-[17px] font-semibold text-white tracking-tight">Зал славы</h3>
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
            <span class="league-pill ${league === null ? 'active' : ''}" data-league="null" onclick="switchLeague(null)">Все</span>
    `;

    LEAGUES.forEach(l => {
        const isActive = l === league;
        const isMy = l === state.primaryLeague;
        html += `<span class="league-pill ${isActive ? 'active' : ''} ${isMy ? 'my' : ''}" data-league="${l}" onclick="switchLeague('${l}')">${LABELS[l]}</span>`;
    });
    html += '</div>';
    html += '<div id="topWeekList" class="top-list-container"></div>';

    panel.innerHTML = html;

    await refreshTopListOnly(league);
}

// ============ ПЕРЕКЛЮЧЕНИЕ ЛИГИ ============

export function switchLeague(league) {
    state.currentLeague = league;

    // 1. слайдер
    const activeIndex = league === null ? 0 : LEAGUES.indexOf(league) + 1;
    const slider = document.querySelector('.league-slider');
    if (slider) slider.className = `league-slider pos-${activeIndex}`;

    // 2. табы — снимаем active у всех, ставим по data-league
    const pills = document.querySelectorAll('.league-pill');
    pills.forEach(pill => pill.classList.remove('active'));

    const key = league === null ? 'null' : league;
    pills.forEach(pill => {
        if (pill.getAttribute('data-league') === key) {
            pill.classList.add('active');
        }
    });

    // 3. список
    refreshTopListOnly(league);
}

// ============ ПЕРЕКЛЮЧЕНИЕ ПЕРИОДА ============

export function switchPeriod(period) {
    state.currentPeriod = period;

    const slider = document.querySelector('.period-slider');
    if (slider) {
        slider.className = `period-slider ${period === 'week' ? 'pos-0' : period === 'month' ? 'pos-1' : 'pos-2'}`;
    }

    document.querySelectorAll('.period-pill').forEach(pill => {
        pill.classList.remove('active');
    });
    const target = document.querySelector(`.period-pill[onclick*="'${period}'"]`);
    if (target) target.classList.add('active');

    refreshTopListOnly(state.currentLeague);
}

// ============ ОБНОВЛЕНИЕ ТОЛЬКО СПИСКА ============

async function refreshTopListOnly(league) {
    const container = document.getElementById('topWeekList');
    if (!container) return;

    const period = state.currentPeriod.toUpperCase();
    const periodLabel = period === 'WEEK' ? '7 дней' : period === 'MONTH' ? '30 дней' : '365 дней';

    try {
        const data = await API.getTop(period, league);
        const currentPlayerName = state.playerName || '';

        let html = '';

        if (!data.top5 || data.top5.length === 0) {
            html = `<div class="text-center py-10">
                <p class="text-[13px] text-zinc-500 mt-3">Нет данных за ${periodLabel}</p>
            </div>`;
        } else {
            data.top5.forEach((p, i) => {
                const name = capitalizeName(p.name || '');
                const isMe = currentPlayerName && name.toLowerCase() === currentPlayerName.toLowerCase();
                const position = i + 1;

                html += `
                    <div class="rank-card ${isMe ? 'mine' : ''}">
                        <div class="rank-number">${position}</div>
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
                                <span class="text-[15px] font-medium text-white truncate">${currentPlayerName || 'Игрок'}</span>
                                <span class="text-[13px] font-semibold text-zinc-400">${data.playerTournaments || ''}</span>
                            </div>
                        </div>
                    </div>
                `;
            }
        }

        container.style.transition = 'opacity 0.15s ease';
        container.style.opacity = '0';

        setTimeout(() => {
            container.innerHTML = html;
            container.style.transition = 'opacity 0.25s ease';
            requestAnimationFrame(() => {
                container.style.opacity = '1';
            });
        }, 150);

    } catch (e) {
        container.innerHTML = `<div class="text-center py-10">
            <p class="text-[13px] text-red-400">Ошибка загрузки</p>
        </div>`;
    }
}

window.loadTopWeek = loadTopWeek;
window.switchLeague = switchLeague;
window.switchPeriod = switchPeriod;