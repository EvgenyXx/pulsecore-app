import { API } from '../core/api.js';
import { state } from '../core/state.js';
import { formatDateShort, capitalizeName } from '../core/utils.js';

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
    } catch(e) {}
}

export async function saveSelectedHalls() {
    const checkboxes = document.querySelectorAll('#hallsCheckboxes input[type="checkbox"]:checked');
    state.selectedHalls = Array.from(checkboxes).map(cb => cb.value);
    const hallsStr = state.selectedHalls.join(', ');
    try {
        await API.saveHalls(hallsStr);
        await refreshLineupsOnly();
    } catch(e) {}
}

export function switchHallsDate(date) {
    state.hallsDate = date;
    document.querySelectorAll('.date-tab').forEach(btn => {
        btn.classList.remove('active');
        if (btn.getAttribute('data-date') === date) btn.classList.add('active');
    });
    refreshLineupsOnly();
}

function renderLineupRows(lineupsByHall) {
    let allLineups = [];
    for (const [hall, lineups] of Object.entries(lineupsByHall)) {
        lineups.forEach(l => allLineups.push({ ...l, hall: hall }));
    }
    allLineups.sort((a, b) => (a.time || '99:99').localeCompare(b.time || '99:99'));

    let html = '';
    allLineups.forEach(l => {
        const playersArray = (l.players || '—').split(',').map(p => capitalizeName(p.trim())).filter(p => p);
        const playersList = playersArray.length > 0
            ? playersArray.map(p => `<span class="player-name">${p}</span>`).join('')
            : '<span class="player-name">—</span>';

        const isPlayerInLineup = l.player || l.inLineup || l.isPlayer || false;
        const rowClass = isPlayerInLineup ? 'lineup-row player-row' : 'lineup-row';

        html += `<div class="${rowClass}">
            <div class="lineup-info">
                <span class="time-badge">${l.time || '??:??'}</span>
                <span class="league-badge">${l.league || '?'}</span>
                <span class="hall-badge">${l.hall}</span>
            </div>
            <div class="players-text">${playersList}</div>
        </div>`;
    });
    return html;
}

export async function refreshLineupsOnly() {
    let lineupsByHall = {};
    if (state.selectedHalls.length > 0) {
        try {
            lineupsByHall = await API.getMyLineups(state.hallsDate);
        } catch(e) {}
    }

    const container = document.getElementById('lineupsContainer');
    if (!container) return;

    if (Object.keys(lineupsByHall).length > 0) {
        container.innerHTML = renderLineupRows(lineupsByHall);
    } else {
        container.innerHTML = '<div class="text-center py-8 text-gray-500"><span class="text-2xl">📭</span><p class="text-xs">Нет составов по выбранным залам</p></div>';
    }
}

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
    c.innerHTML = '<div class="text-center py-8"><div class="spinner mx-auto"></div></div>';

    const uiState = loadHallsUIState();

    let allHalls = [];
    try {
        const data = await API.getAllLineups(state.hallsDate);
        allHalls = Object.keys(data).sort();
    } catch(e) {}

    let lineupsByHall = {};
    if (state.selectedHalls.length > 0) {
        try {
            lineupsByHall = await API.getMyLineups(state.hallsDate);
        } catch(e) {}
    }

    let html = '';
    const today = new Date();
    const dates = [];
    for (let i = 0; i < 3; i++) { const d = new Date(today); d.setDate(d.getDate() + i); dates.push(d.toISOString().split('T')[0]); }

    html += '<div class="flex gap-2 mb-4 flex-wrap">';
    dates.forEach(d => {
        const label = d === dates[0] ? 'Сегодня' : d === dates[1] ? 'Завтра' : 'Послезавтра';
        html += `<button onclick="switchHallsDate('${d}')" data-date="${d}" class="date-tab ${d === state.hallsDate ? 'active' : ''}">${label}<br><span class="text-xs opacity-60">${formatDateShort(d)}</span></button>`;
    });
    html += '</div>';

    if (allHalls.length > 0) {
        const allSelected = uiState.allSelected && state.selectedHalls.length === allHalls.length;
        html += `<div class="widget-card rounded-2xl p-4 mb-4">
            <div class="flex items-center justify-between mb-3 cursor-pointer" onclick="toggleHallsCheckboxes()">
                <div class="flex items-center gap-3">
                    <h3 class="text-sm font-semibold text-white">🏛 Выберите залы</h3>
                    <label class="hall-checkbox-card" style="padding: 4px 10px; gap: 6px;" onclick="event.stopPropagation()">
                        <input type="checkbox" id="allHallsCheckbox" ${allSelected ? 'checked' : ''} onchange="toggleAllHalls()" class="accent-indigo-500 w-4 h-4">
                        <span class="text-xs text-zinc-300 select-none">Все</span>
                    </label>
                </div>
                <span class="toggle-arrow text-zinc-400 text-xs ${uiState.collapsed ? 'rotate-180' : ''}" id="hallsToggleArrow">▼</span>
            </div>
            <div id="hallsCheckboxesWrapper" class="${uiState.collapsed ? 'hidden' : ''}">
                <div class="grid grid-cols-2 sm:grid-cols-3 gap-2" id="hallsCheckboxes">`;
        allHalls.forEach(hall => {
            const checked = state.selectedHalls.includes(hall);
            html += `<label class="hall-checkbox-card ${checked ? 'selected' : ''}"><input type="checkbox" value="${hall}" ${checked ? 'checked' : ''} onchange="this.parentElement.classList.toggle('selected', this.checked)"><span class="text-sm text-white">${hall}</span></label>`;
        });
        html += '</div><button onclick="saveSelectedHalls()" class="btn-gold w-full mt-3 py-2 text-sm">💾 Сохранить выбранные залы</button></div></div>';
    } else {
        html += '<div class="text-center py-4 text-gray-500"><span class="text-2xl">📭</span><p class="text-xs">Залы не загружены</p></div>';
    }

    html += '<div class="widget-card rounded-2xl p-4" id="lineupsContainer">';
    if (Object.keys(lineupsByHall).length > 0) {
        html += renderLineupRows(lineupsByHall);
    } else if (state.selectedHalls.length > 0) {
        html += '<div class="text-center py-8 text-gray-500"><span class="text-2xl">📭</span><p class="text-xs">Нет составов по выбранным залам</p></div>';
    }
    html += '</div>';

    c.innerHTML = html;
}