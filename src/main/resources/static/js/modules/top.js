import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { capitalizeName } from '../core/utils.js';

const LEAGUES = ['A', 'B', 'C', 'D', 'SUPER_LEAGUE'];
const LABELS = {'A': 'A', 'B': 'B', 'C': 'C', 'D': 'D', 'SUPER_LEAGUE': 'Супер'};

let isExpanded = false;
let activeDetailId = null;

function getLeagueBadge(league) {
    if (!league) return '';
    const label = league === 'SUPER_LEAGUE' ? 'SL' : league;
    return `<span class="league-badge">${label}</span>`;
}

export async function loadTopWeek(league) {
    const panel = document.getElementById('topWeekPanel');
    if (!panel) return;

    state.currentLeague = league;
    const activeLeagueIndex = league === null ? 0 : LEAGUES.indexOf(league) + 1;

    let html = `
        <div class="flex items-center justify-between mb-5">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-amber-400/20 to-amber-600/10 flex items-center justify-center flex-shrink-0">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fbbf24" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                </div>
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

    html += `<div id="topWeekWrapper" class="top-list-wrapper"><div id="topWeekList" class="top-list-container"></div></div>`;

    html += `
        <div id="myPositionBlock" class="my-position-block hidden">
            <div class="my-position-label">Твоя позиция</div>
            <div id="myPositionCard"></div>
        </div>
    `;

    html += `
        <div id="topToggleWrapper" class="mt-4 hidden">
            <button id="topToggleBtn" class="top-toggle-btn-full" onclick="toggleTopList()">
                Показать всех
            </button>
        </div>
    `;

    panel.innerHTML = html;

    const list = document.getElementById('topWeekList');
    if (list && !list.dataset.bound) {
        list.dataset.bound = 'true';
        list.addEventListener('click', handleCardClick);
    }

    isExpanded = false;
    activeDetailId = null;
    await refreshTopListOnly(league);
}

export function switchLeague(league) {
    state.currentLeague = league;
    const activeIndex = league === null ? 0 : LEAGUES.indexOf(league) + 1;
    const slider = document.querySelector('.league-slider');
    if (slider) slider.className = `league-slider pos-${activeIndex}`;

    document.querySelectorAll('.league-pill').forEach(pill => pill.classList.remove('active'));
    const key = league === null ? 'null' : league;
    document.querySelectorAll('.league-pill').forEach(pill => {
        if (pill.getAttribute('data-league') === key) pill.classList.add('active');
    });

    activeDetailId = null;
    refreshTopListOnly(league);
}

export function switchPeriod(period) {
    state.currentPeriod = period;
    const slider = document.querySelector('.period-slider');
    if (slider) {
        slider.className = `period-slider ${period === 'week' ? 'pos-0' : period === 'month' ? 'pos-1' : 'pos-2'}`;
    }
    document.querySelectorAll('.period-pill').forEach(pill => pill.classList.remove('active'));
    const target = document.querySelector(`.period-pill[onclick*="'${period}'"]`);
    if (target) target.classList.add('active');

    activeDetailId = null;
    refreshTopListOnly(state.currentLeague);
}

async function refreshTopListOnly(league) {
    const container = document.getElementById('topWeekList');
    const toggleWrapper = document.getElementById('topToggleWrapper');
    const toggleBtn = document.getElementById('topToggleBtn');
    const myPosBlock = document.getElementById('myPositionBlock');
    const myPosCard = document.getElementById('myPositionCard');

    if (!container) return;

    const period = state.currentPeriod.toUpperCase();
    const periodLabel = period === 'WEEK' ? '7 дней' : period === 'MONTH' ? '30 дней' : '365 дней';

    try {
        const data = await API.getTop(period, league);
        const currentPlayerName = state.playerName || '';
        const fullList = data.all || data.top5 || [];

        let html = '';

        if (fullList.length === 0) {
            html = `<div class="text-center py-10">
                <p class="text-[13px] text-zinc-500 mt-3">Нет данных за ${periodLabel}</p>
            </div>`;
            if (toggleWrapper) toggleWrapper.classList.add('hidden');
            if (myPosBlock) myPosBlock.classList.add('hidden');
        } else {
            if (toggleWrapper) {
                toggleWrapper.classList.toggle('hidden', fullList.length <= 5);
            }

            fullList.forEach((p, i) => {
                const name = capitalizeName(p.name || '');
                const isMe = currentPlayerName && name.toLowerCase() === currentPlayerName.toLowerCase();
                const position = i + 1;
                const isFirst = position === 1;
                const badge = getLeagueBadge(p.primaryLeague);

                const isHiddenClass = (!isExpanded && position > 5) ? 'hidden-item' : '';

                const detailJson = p.detailPeriod ? JSON.stringify(p.detailPeriod) : '';
                const safeDetails = detailJson.replace(/"/g, '&quot;');

                // Используем <details> и <summary> для нативного аккордеона
                html += `
                    <details class="rank-card-details hof-item ${isMe ? 'mine' : ''} ${isFirst ? 'first' : ''} ${isHiddenClass}" 
                         data-pos="${position}" 
                         data-details="${safeDetails}"
                         data-player-id="${p.playerId || ''}"
                         data-no-swipe="true">
                        <summary class="rank-card">
                            <div class="rank-number">${position}</div>
                            <div class="flex-1 min-w-0">
                                <div class="flex items-center justify-between gap-2">
                                    <span class="text-[15px] font-medium text-white truncate">${name}</span>
                                    <div class="rank-meta">
                                        ${badge}
                                    </div>
                                </div>
                            </div>
                        </summary>
                        <div class="rank-details-wrapper"></div>
                    </details>
                `;
            });

            if (myPosBlock && myPosCard) {
                if (data.playerPosition > 5 && !isExpanded) {
                    myPosBlock.classList.remove('hidden');
                    const myBadge = data.playerLeague ? getLeagueBadge(data.playerLeague) : '';
                    myPosCard.innerHTML = `
                        <div class="rank-card mine" data-no-swipe="true">
                            <div class="rank-number">${data.playerPosition}</div>
                            <div class="flex-1 min-w-0">
                                <div class="flex items-center justify-between gap-2">
                                    <span class="text-[15px] font-medium text-white truncate">${currentPlayerName || 'Игрок'}</span>
                                    <div class="rank-meta">
                                        ${myBadge}
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                } else {
                    myPosBlock.classList.add('hidden');
                }
            }
        }

        container.innerHTML = html;

        if (isExpanded) {
            container.classList.add('expanded');
            container.classList.remove('collapsed');
            if (toggleBtn) toggleBtn.textContent = 'Свернуть';
        } else {
            container.classList.add('collapsed');
            container.classList.remove('expanded');
            if (toggleBtn) toggleBtn.textContent = 'Показать всех';
        }

    } catch (e) {
        container.innerHTML = `<div class="text-center py-10">
            <p class="text-[13px] text-red-400">Ошибка загрузки</p>
        </div>`;
        if (toggleWrapper) toggleWrapper.classList.add('hidden');
        if (myPosBlock) myPosBlock.classList.add('hidden');
    }
}

function handleCardClick(e) {
    const summary = e.target.closest('.rank-card');
    if (!summary) return;

    const detailsEl = summary.parentElement;
    const wrapper = detailsEl.querySelector('.rank-details-wrapper');

    // Если уже открыто — браузер закроет сам (или мы просто очистим)
    if (!detailsEl.open) {
        e.preventDefault(); // Блокируем стандартное открытие, чтобы вставить контент
        detailsEl.open = true;

        const detailJson = detailsEl.getAttribute('data-details');
        if (!detailJson || detailJson === '""') return;

        const data = JSON.parse(detailJson);

        wrapper.innerHTML = `
            <div class="rd-grid">
                <div class="rd-item">
                    <span class="rd-label">ТУРНИРЫ</span>
                    <span class="rd-value">${data.tournaments || 0}</span>
                </div>
                <div class="rd-item">
                    <span class="rd-label">ЗАРАБОТАЛ</span>
                    <span class="rd-value text-emerald-400">${(data.totalAmount || 0).toLocaleString()} ₽</span>
                </div>
                <div class="rd-item">
                    <span class="rd-label">СРЕДНИЙ ЧЕК</span>
                    <span class="rd-value">${(data.averageAmount || 0).toLocaleString()} ₽</span>
                </div>
                <div class="rd-item">
                    <span class="rd-label">МАТЧИ</span>
                    <span class="rd-value">${data.matchesCount || 0}</span>
                </div>
                <div class="rd-item">
                    <span class="rd-label">ПОБЕДЫ</span>
                    <span class="rd-value text-emerald-400">${data.wins || 0}</span>
                </div>
                <div class="rd-item">
                    <span class="rd-label">ПОРАЖЕНИЯ</span>
                    <span class="rd-value text-red-400">${data.loses || 0}</span>
                </div>
                <div class="rd-item rd-full">
                    <span class="rd-label">ВИНРЕЙТ</span>
                    <span class="rd-value">${data.matchesCount > 0 ? Math.round((data.wins / data.matchesCount) * 100) : 0}%</span>
                </div>
            </div>
        `;

        // Закрываем остальные
        document.querySelectorAll('.rank-card-details').forEach(el => {
            if (el !== detailsEl) el.open = false;
        });

        const container = document.getElementById('topWeekList');
        container.classList.remove('collapsed');
        container.classList.add('expanded');
    }
}

window.toggleTopList = function() {
    const container = document.getElementById('topWeekList');
    const btn = document.getElementById('topToggleBtn');
    const myPosBlock = document.getElementById('myPositionBlock');

    if (!container || !btn) return;

    document.querySelectorAll('.rank-card-details').forEach(el => el.open = false);

    if (container.classList.contains('collapsed')) {
        isExpanded = true;
        container.classList.remove('collapsed');
        container.classList.add('expanded');
        btn.textContent = 'Свернуть';

        container.querySelectorAll('.hidden-item').forEach(el => el.classList.remove('hidden-item'));
        if (myPosBlock) myPosBlock.classList.add('hidden');

    } else {
        isExpanded = false;
        container.classList.remove('expanded');
        container.classList.add('collapsed');
        btn.textContent = 'Показать всех';

        container.scrollTo({ top: 0, behavior: 'smooth' });

        setTimeout(() => {
            const items = container.querySelectorAll('.hof-item');
            items.forEach((el) => {
                const pos = parseInt(el.getAttribute('data-pos'));
                if (pos > 5) {
                    el.classList.add('hidden-item');
                }
            });
            refreshTopListOnly(state.currentLeague);
        }, 400);
    }
};

window.loadTopWeek = loadTopWeek;
window.switchLeague = switchLeague;
window.switchPeriod = switchPeriod;