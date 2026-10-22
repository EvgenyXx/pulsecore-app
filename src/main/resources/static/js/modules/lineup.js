import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { formatDateShort, capitalizeName } from '../core/utils.js';

let currentPage = 0;
let isLastPage = false;
let isLoading = false;

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
    const container = document.getElementById('lineupsContainer');
    if (!container) return;

    container.innerHTML = '<div id="lineupsList"></div><div id="lineupsLoader" style="text-align:center; padding: 20px; color:#71717a; font-size:12px; display:none;">Загрузка...</div>';
    await loadPage();
}

async function loadPage() {
    if (isLoading || isLastPage) return;
    isLoading = true;

    const loader = document.getElementById('lineupsLoader');
    if (loader) loader.style.display = 'block';

    try {
        const pageData = await API.getMyLineups(state.hallsDate, currentPage, 7);
        const items = pageData.content || [];

        const list = document.getElementById('lineupsList');
        if (list && items.length > 0) {
            let chunkHtml = '';
            items.forEach(l => { chunkHtml += renderSingleCard(l); });
            list.insertAdjacentHTML('beforeend', chunkHtml);
        }

        isLastPage = pageData.last !== undefined ? pageData.last : (items.length < 7);
        currentPage++;

    } catch (e) {
        console.error('Ошибка загрузки составов:', e);
    } finally {
        isLoading = false;
        if (loader) loader.style.display = 'none';
    }
}

function resetPagination() {
    currentPage = 0;
    isLastPage = false;
    isLoading = false;
}

if (window._lineupScrollHandler) {
    window.removeEventListener('scroll', window._lineupScrollHandler);
}
window._lineupScrollHandler = function() {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 300) {
        loadPage();
    }
};
window.addEventListener('scroll', window._lineupScrollHandler);


export function toggleAllHalls() {
    const allCheckbox = document.getElementById('allHallsCheckbox');
    const checkboxes = document.querySelectorAll('#hallsCheckboxes input[type="checkbox"]');
    const selectAll = allCheckbox.checked;
    checkboxes.forEach(cb => { cb.checked = selectAll; cb.parentElement.classList.toggle('selected', selectAll); });
    const wrapper = document.getElementById('hallsCheckboxesWrapper');
    saveHallsUIState(wrapper.classList.contains('hidden'), selectAll);
}

export function toggleHallsCheckboxes() {
    const wrapper = document.getElementById('hallsCheckboxesWrapper');
    const arrow = document.getElementById('hallsToggleArrow');
    if (!wrapper || !arrow) return;
    wrapper.classList.toggle('hidden');
    arrow.classList.toggle('rotate-180');
    const allSelected = document.getElementById('allHallsCheckbox')?.checked || false;
    saveHallsUIState(wrapper.classList.contains('hidden'), allSelected);
}

export async function loadHallsContent() {
    const c = document.getElementById('actionContent');
    c.innerHTML = '<div class="text-center py-8"></div>';

    await loadSelectedHalls();
    const uiState = loadHallsUIState();

    let allHalls = [];
    try {
        // ТЕПЕРЬ ЖДЕМ МАССИВ СТРОК
        const data = await API.getAllLineups(state.hallsDate);
        allHalls = data.sort();
    } catch (e) {}

    let html = '';
    const dates = getDates();
    const activeDateIndex = dates.indexOf(state.hallsDate) >= 0 ? dates.indexOf(state.hallsDate) : 0;

    html += '<div class="date-switcher mb-5">';
    html += `<div class="date-slider pos-${activeDateIndex}"></div>`;
    dates.forEach((d, idx) => {
        const label = idx === 0 ? 'Сегодня' : idx === 1 ? 'Завтра' : 'Послезавтра';
        html += `<button onclick="switchHallsDate('${d}')" data-date="${d}" class="date-tab ${d === state.hallsDate ? 'active' : ''}">
            <span class="date-tab-label">${label}</span>
            <span class="date-tab-date">${formatDateShort(d)}</span>
        </button>`;
    });
    html += '</div>';

    if (allHalls.length > 0) {
        const allSelected = uiState.allSelected && state.selectedHalls.length === allHalls.length;
        html += `<div class="apple-card mb-4">
            <div class="flex items-center justify-between mb-3 cursor-pointer" onclick="toggleHallsCheckboxes()">
                <h3 class="text-[15px] font-semibold text-white tracking-tight">Выберите залы</h3>
                <div class="flex items-center gap-2">
                    <label class="hall-select-all" onclick="event.stopPropagation()">
                        <input type="checkbox" id="allHallsCheckbox" ${allSelected ? 'checked' : ''} onchange="toggleAllHalls()" class="accent-indigo-500 w-4 h-4">
                        <span class="text-xs text-zinc-300 select-none">Все</span>
                    </label>
                    <span class="toggle-arrow text-zinc-400 text-xs transition-transform duration-300 ${uiState.collapsed ? 'rotate-180' : ''}" id="hallsToggleArrow">▼</span>
                </div>
            </div>
            <div id="hallsCheckboxesWrapper" class="${uiState.collapsed ? 'hidden' : ''}">
                <div class="grid grid-cols-2 sm:grid-cols-3 gap-2" id="hallsCheckboxes">`;
        allHalls.forEach(hall => {
            const checked = state.selectedHalls.includes(hall);
            html += `<label class="hall-checkbox-card ${checked ? 'selected' : ''}"><input type="checkbox" value="${hall}" ${checked ? 'checked' : ''} onchange="this.parentElement.classList.toggle('selected', this.checked)"><span class="text-sm text-white">${hall}</span></label>`;
        });
        html += '</div><button onclick="saveSelectedHalls()" class="apple-save-btn">Сохранить</button></div></div>';
    } else {
        html += '<div class="text-center py-4 text-gray-500"><span class="text-2xl">📭</span><p class="text-xs">Залы не загружены</p></div>';
    }

    html += '<div class="apple-card" id="lineupsContainer"></div>';
    c.innerHTML = html;

    if (state.selectedHalls.length > 0) {
        await refreshLineupsOnly();
    } else {
        document.getElementById('lineupsContainer').innerHTML = '<div class="text-center py-8 text-gray-500"><span class="text-2xl">📭</span><p class="text-xs">Выберите залы</p></div>';
    }
}