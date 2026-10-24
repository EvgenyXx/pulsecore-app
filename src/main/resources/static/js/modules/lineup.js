import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { formatDateShort, capitalizeName } from '../core/utils.js';

let currentPage = 0;
let isLastPage = false;
let isLoading = false;
let scrollContainer = null;

export function loadHallsUIState() {
    const collapsed = localStorage.getItem('hallsCheckboxesCollapsed') === 'true';
    const allSelected = localStorage.getItem('hallsAllSelected') === 'true';
    return { collapsed, allSelected };
}

export function saveHallsUIState(collapsed, allSelected) {
    localStorage.setItem('hallsCheckboxesCollapsed', collapsed);
    localStorage.setItem('hallsAllSelected', allSelected);
}

export async function loadSelectedHalls() {
    try {
        const r = await API.getHalls();
        if (r) {
            state.selectedHalls = r.halls ? r.halls.split(',').map(h => h.trim()).filter(h => h) : [];
        }
    } catch (e) {}
}

export async function saveSelectedHalls() {
    const checkboxes = document.querySelectorAll('#hallsCheckboxes input[type="checkbox"]:checked');
    state.selectedHalls = Array.from(checkboxes).map(cb => cb.value);
    const hallsStr = state.selectedHalls.join(', ');
    try {
        await API.saveHalls(hallsStr);
        closeHallsSheet();
        await refreshLineupsOnly();
    } catch (e) {}
}

export function switchHallsDate(date) {
    state.hallsDate = date;
    const slider = document.querySelector('.date-slider');
    const dates = getDates();
    const activeIndex = dates.indexOf(date);
    if (slider) slider.className = `date-slider pos-${activeIndex}`;

    document.querySelectorAll('.date-tab').forEach(btn => {
        btn.classList.remove('active');
        if (btn.getAttribute('data-date') === date) btn.classList.add('active');
    });

    refreshLineupsOnly();
}

function getDates() {
    const today = new Date();
    const dates = [];
    for (let i = 0; i < 3; i++) {
        const d = new Date(today);
        d.setDate(d.getDate() + i);
        dates.push(d.toISOString().split('T')[0]);
    }
    return dates;
}

function renderTypeBadge(type) {
    if (!type) return '';
    const isNew = String(type).toLowerCase().includes('new') || String(type).toLowerCase() === 'four_pl_new';
    return isNew
        ? '<span class="lineup-type-badge lineup-type-new">New</span>'
        : '<span class="lineup-type-badge lineup-type-std">Standard</span>';
}

function renderSingleCard(l) {
    const playersArray = (l.players || '—')
        .split(',')
        .map(p => capitalizeName(p.trim()))
        .filter(p => p);

    const playersList = playersArray.length > 0
        ? playersArray.map(p => `<span class="lineup-player-name">${p}</span>`).join('')
        : '<span class="text-zinc-600 text-xs">—</span>';

    const isPlayerInLineup = l.player || l.inLineup || l.isPlayer || false;
    const rowClass = isPlayerInLineup ? 'lineup-card is-player' : 'lineup-card';
    const beam = isPlayerInLineup ? '<span class="lineup-beam" aria-hidden="true"></span>' : '';
    const typeBadge = renderTypeBadge(l.type);
    const dateValue = l.date ? formatDateShort(l.date) : '—';

    const mlIcon = l.link
        ? `<a class="lineup-ml-icon" href="${l.link}" target="_blank" rel="noopener" aria-label="Открыть на ML">
               <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                   <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
                   <polyline points="15 3 21 3 21 9"/>
                   <line x1="10" y1="14" x2="21" y2="3"/>
               </svg>
           </a>`
        : '';

    return `
        <div class="${rowClass}">
            ${beam}
            ${mlIcon}
            <div class="lineup-date-row">
                <span class="lineup-meta-label">Дата</span>
                <span class="lineup-meta-value">${dateValue}</span>
            </div>
            <div class="lineup-body">
                <div class="lineup-meta">
                    <div class="lineup-meta-row">
                        <span class="lineup-meta-label">Время</span>
                        <span class="lineup-meta-value lineup-time-value">${l.time || '??:??'}</span>
                    </div>
                    <div class="lineup-meta-row">
                        <span class="lineup-meta-label">Лига</span>
                        <span class="lineup-meta-value lineup-league-value">${l.league || '?'}</span>
                    </div>
                    <div class="lineup-meta-row">
                        <span class="lineup-meta-label">Зал</span>
                        <span class="lineup-meta-value">${l.hall}</span>
                    </div>
                    ${typeBadge ? `<div class="lineup-meta-row"><span class="lineup-meta-label">Формат</span>${typeBadge}</div>` : ''}
                </div>
                <div class="lineup-players-wrapper">
                    <div class="lineup-players-column">${playersList}</div>
                </div>
            </div>
        </div>
    `;
}

export async function refreshLineupsOnly() {
    resetPagination();
    const list = document.getElementById('lineupsList');
    if (!list) return;

    list.innerHTML = '<div id="lineupsLoader" style="text-align:center; padding: 20px; color:#71717a; font-size:12px;">Загрузка...</div>';

    if (scrollContainer) scrollContainer.scrollTop = 0;

    await loadPage();
}

async function loadPage() {
    if (isLoading || isLastPage) return;
    isLoading = true;

    try {
        const pageData = await API.getMyLineups(state.hallsDate, currentPage, 7);
        const items = pageData.content || [];
        const list = document.getElementById('lineupsList');

        if (list) {
            if (currentPage === 0) list.innerHTML = '';
            const loader = document.getElementById('lineupsLoader');
            if (loader) loader.remove();

            let chunkHtml = '';
            items.forEach(l => { chunkHtml += renderSingleCard(l); });

            const wrapper = document.createElement('div');
            wrapper.innerHTML = chunkHtml;

            requestAnimationFrame(() => {
                requestAnimationFrame(() => {
                    while (wrapper.firstChild) {
                        list.appendChild(wrapper.firstChild);
                    }
                });
            });
        }

        isLastPage = pageData.last !== undefined ? pageData.last : (items.length < 7);
        currentPage++;
    } catch (e) {
        console.error('Ошибка загрузки:', e);
    } finally {
        isLoading = false;
    }
}

function resetPagination() {
    currentPage = 0;
    isLastPage = false;
    isLoading = false;
}

function bindScroll() {
    scrollContainer = document.getElementById('hallsScrollArea');
    if (!scrollContainer) return;

    scrollContainer.addEventListener('scroll', () => {
        if (scrollContainer.scrollHeight - scrollContainer.scrollTop <= scrollContainer.clientHeight + 100) {
            loadPage();
        }
    });
}

// ============ ШТОРКА ЗАЛОВ (ВЫНОСИМ В BODY) ============
window.openHallsSheet = function() {
    const overlay = document.getElementById('hallsSheetOverlay');
    if (overlay) {
        document.body.appendChild(overlay); // Физически переносим в конец body, чтобы перекрыть всё
        overlay.classList.add('open');
    }
    const bottomTab = document.getElementById('bottomTab');
    if (bottomTab) bottomTab.style.display = 'none'; // Скрываем навигацию
};

window.closeHallsSheet = function() {
    const overlay = document.getElementById('hallsSheetOverlay');
    if (overlay) overlay.classList.remove('open');

    const bottomTab = document.getElementById('bottomTab');
    if (bottomTab) bottomTab.style.display = '';
};

export function toggleHallsCheckboxes() {}

export function toggleAllHalls() {
    const allCheckbox = document.getElementById('allHallsCheckbox');
    const checkboxes = document.querySelectorAll('#hallsCheckboxes input[type="checkbox"]');
    const selectAll = allCheckbox.checked;
    checkboxes.forEach(cb => { cb.checked = selectAll; cb.parentElement.classList.toggle('selected', selectAll); });
}

export async function loadHallsContent() {
    const c = document.getElementById('actionContent');
    c.innerHTML = '';

    await loadSelectedHalls();

    let allHalls = [];
    try {
        const data = await API.getAllLineups(state.hallsDate);
        allHalls = data.sort();
    } catch (e) {}

    const dates = getDates();
    const activeDateIndex = dates.indexOf(state.hallsDate) >= 0 ? dates.indexOf(state.hallsDate) : 0;

    let html = '<div class="halls-main-container">';

    // Шапка: Даты + Кнопка фильтра
    html += '<div class="halls-header">';
    html += `<div class="date-switcher-container">
        <div class="date-switcher">
            <div class="date-slider pos-${activeDateIndex}"></div>`;
    dates.forEach((d, idx) => {
        const label = idx === 0 ? 'Сегодня' : idx === 1 ? 'Завтра' : 'Послезавтра';
        html += `<button onclick="switchHallsDate('${d}')" data-date="${d}" class="date-tab ${d === state.hallsDate ? 'active' : ''}">
            <span class="date-tab-label">${label}</span>
            <span class="date-tab-date">${formatDateShort(d)}</span>
        </button>`;
    });
    html += `</div></div>`;

    // Кнопка фильтра (воронка)
    html += `<button class="halls-filter-btn" onclick="openHallsSheet()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/></svg>
    </button>`;

    html += '</div>';

    html += `<div class="halls-scroll-area" id="hallsScrollArea">
        <div id="lineupsList"></div>
    </div>`;

    html += '</div>';

    // Шторка с залами (пока в actionContent, но JS перенесет её в body при открытии)
    html += `<div class="halls-sheet-overlay" id="hallsSheetOverlay" onclick="if(event.target===this)closeHallsSheet()">
        <div class="halls-sheet" onclick="event.stopPropagation()">
            <div class="halls-sheet-handle"></div>
            <h3 class="halls-sheet-title">Выберите залы</h3>
            <div class="flex items-center justify-end mb-3">
                <label class="hall-select-all" onclick="event.stopPropagation()">
                    <input type="checkbox" id="allHallsCheckbox" ${state.selectedHalls.length === allHalls.length ? 'checked' : ''} onchange="toggleAllHalls()" class="accent-indigo-500 w-4 h-4">
                    <span class="text-xs text-zinc-300 select-none">Выбрать все</span>
                </label>
            </div>
            <div class="grid grid-cols-2 gap-2" id="hallsCheckboxes">`;
    allHalls.forEach(hall => {
        const checked = state.selectedHalls.includes(hall);
        html += `<label class="hall-checkbox-card ${checked ? 'selected' : ''}"><input type="checkbox" value="${hall}" ${checked ? 'checked' : ''} onchange="this.parentElement.classList.toggle('selected', this.checked)"><span class="text-sm text-white">${hall}</span></label>`;
    });
    html += `</div>
            <button onclick="saveSelectedHalls()" class="apple-save-btn">Применить</button>
        </div>
    </div>`;

    c.innerHTML = html;

    bindScroll();
    if (state.selectedHalls.length > 0) {
        await refreshLineupsOnly();
    } else {
        document.getElementById('lineupsList').innerHTML = '<div class="text-center py-8 text-gray-500"><span class="text-2xl">📭</span><p class="text-xs">Выберите залы</p></div>';
    }
}
// v2