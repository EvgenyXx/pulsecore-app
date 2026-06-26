let selectedTab = 'all';
let allTournaments = [];
let allLineups = [];
let allHalls = [];
let savedHalls = [];
let hallsCollapsed = false;

async function loadLive() {
    try {
        const sub = await fetch('/api/player/subscription', { credentials: 'same-origin' }).then(r => r.json()).catch(() => null);
        if (!sub || !sub.active) {
            document.getElementById('loading').classList.add('hidden');
            document.getElementById('noSubBlock').classList.remove('hidden');
            document.getElementById('tabFilter').classList.add('hidden');
            document.getElementById('hallFilter').classList.add('hidden');
            document.getElementById('subtitle').textContent = 'Требуется PRO';
            return;
        }

        const today = new Date().toISOString().split('T')[0];

        const [tournamentsRes, lineupsRes, hallsRes] = await Promise.all([
            fetch('/api/tournament/live', { credentials: 'same-origin' }),
            fetch(`/api/lineups?date=${today}`, { credentials: 'same-origin' }),
            fetch('/api/lineups/live-halls', { credentials: 'same-origin' })
        ]);

        document.getElementById('loading').classList.add('hidden');

        if (tournamentsRes.ok) allTournaments = await tournamentsRes.json(); else allTournaments = [];
        if (lineupsRes.ok) allLineups = await lineupsRes.json(); else allLineups = [];

        if (hallsRes.ok) {
            const text = await hallsRes.text();
            savedHalls = text ? text.split(',').map(h => h.trim()).filter(h => h) : [];
        } else {
            savedHalls = [];
        }

        const hallsSet = new Set();
        allTournaments.forEach(t => { if (t.hall) hallsSet.add(t.hall); });
        allLineups.forEach(l => { if (l.hall) hallsSet.add(l.hall); });
        allHalls = [...hallsSet].sort();

        hallsCollapsed = localStorage.getItem('liveHallsCollapsed') === 'true';

        document.getElementById('tabFilter').classList.remove('hidden');
        if (allHalls.length > 0) renderHallFilter();
        applyFilter();
    } catch (e) {
        document.getElementById('loading').classList.add('hidden');
        document.getElementById('empty').classList.remove('hidden');
        document.getElementById('subtitle').textContent = 'Ошибка загрузки';
    }
}

async function loadAllOnlineCounts() {
    const ids = allTournaments.map(t => t.externalId);
    for (const id of ids) {
        try {
            const res = await fetch(`/api/chat/${id}/online`, { credentials: 'same-origin' });
            if (res.ok) {
                const count = await res.json();
                const el = document.querySelector(`.online-count-${id}`);
                if (el) {
                    el.textContent = count > 0 ? count : '0';
                    el.style.display = 'inline';
                }
            }
        } catch(e) {}
    }
}

function renderHallFilter() {
    const container = document.getElementById('hallFilter');
    container.classList.remove('hidden');

    const allSelected = savedHalls.length === 0 || savedHalls.length === allHalls.length;

    container.innerHTML = `
        <div class="widget-card rounded-2xl p-4 mb-4 w-full">
            <div class="flex items-center justify-between mb-3 cursor-pointer" onclick="toggleHallsCheckboxes()">
                <div class="flex items-center gap-3">
                    <h3 class="text-sm font-semibold text-white">Выберите залы</h3>
                    <label class="hall-checkbox-card" style="padding: 4px 10px; gap: 6px;" onclick="event.stopPropagation()">
                        <input type="checkbox" id="allLiveHallsCheckbox" ${allSelected ? 'checked' : ''} onchange="toggleAllLiveHalls()" class="accent-indigo-500 w-4 h-4">
                        <span class="text-xs text-zinc-300 select-none">Все</span>
                    </label>
                </div>
                <span class="toggle-arrow text-zinc-400 text-xs ${hallsCollapsed ? 'rotate-180' : ''}" id="liveHallsToggleArrow">▼</span>
            </div>
            <div id="liveHallsCheckboxesWrapper" class="${hallsCollapsed ? 'hidden' : ''}">
                <div class="grid grid-cols-2 sm:grid-cols-3 gap-2" id="liveHallsCheckboxes">
                    ${allHalls.map(hall => {
        const checked = savedHalls.includes(hall);
        return `<label class="hall-checkbox-card ${checked ? 'selected' : ''}"><input type="checkbox" value="${escapeHtml(hall)}" ${checked ? 'checked' : ''} onchange="this.parentElement.classList.toggle('selected', this.checked)"><span class="text-sm text-white">${escapeHtml(hall)}</span></label>`;
    }).join('')}
                </div>
                <button onclick="saveSelectedLiveHalls()" class="btn-gold w-full mt-3 py-2 text-sm">Сохранить выбранные залы</button>
            </div>
        </div>
    `;
}

function toggleHallsCheckboxes() {
    const wrapper = document.getElementById('liveHallsCheckboxesWrapper');
    const arrow = document.getElementById('liveHallsToggleArrow');
    if (!wrapper || !arrow) return;
    wrapper.classList.toggle('hidden');
    arrow.classList.toggle('rotate-180');
    hallsCollapsed = !hallsCollapsed;
    localStorage.setItem('liveHallsCollapsed', hallsCollapsed);
}

function toggleAllLiveHalls() {
    const allCheckbox = document.getElementById('allLiveHallsCheckbox');
    const checkboxes = document.querySelectorAll('#liveHallsCheckboxes input[type="checkbox"]');
    const selectAll = allCheckbox.checked;
    checkboxes.forEach(cb => { cb.checked = selectAll; cb.parentElement.classList.toggle('selected', selectAll); });
}

async function saveSelectedLiveHalls() {
    const checkboxes = document.querySelectorAll('#liveHallsCheckboxes input[type="checkbox"]:checked');
    savedHalls = Array.from(checkboxes).map(cb => cb.value);
    const hallsStr = savedHalls.join(',');
    await fetch('/api/lineups/live-halls', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        credentials: 'same-origin',
        body: hallsStr
    });
    applyFilter();
    renderHallFilter();
}

function applyFilter() {
    let filtered = allTournaments;

    if (selectedTab === 'live') {
        filtered = filtered.filter(t => t.status === 'LIVE');
    } else if (selectedTab === 'upcoming') {
        filtered = filtered.filter(t => t.status === 'UPCOMING');
    } else if (selectedTab === 'finished') {
        filtered = filtered.filter(t => t.status === 'FINISHED');
    }

    if (savedHalls.length > 0) {
        filtered = filtered.filter(t => savedHalls.includes(t.hall));
    }

    let filteredLineups = allLineups;
    if (savedHalls.length > 0) {
        filteredLineups = filteredLineups.filter(l => savedHalls.includes(l.hall));
    }

    renderTournaments(filtered);
    renderLineups(filteredLineups);
}

function getStatusBadge(status) {
    switch(status) {
        case 'LIVE': return '<span class="live-badge"><span class="w-2 h-2 rounded-full bg-white"></span> LIVE</span>';
        case 'UPCOMING': return '<span style="background:#f59e0b;color:#000;font-size:0.65rem;font-weight:700;padding:3px 10px;border-radius:20px;">Скоро</span>';
        case 'FINISHED': return '<span style="background:#52525b;color:#a1a1aa;font-size:0.65rem;font-weight:700;padding:3px 10px;border-radius:20px;">Завершён</span>';
        default: return '';
    }
}

function getButton(status, externalId) {
    switch(status) {
        case 'LIVE': return `<button class="btn-live">Смотреть трансляцию</button>`;
        case 'UPCOMING': return `<button class="btn-live" style="background:linear-gradient(135deg,#f59e0b,#d97706);" disabled>Ожидание</button>`;
        case 'FINISHED': return `<button class="btn-live" style="background:linear-gradient(135deg,#52525b,#404040);">Чат</button>`;
        default: return `<button class="btn-live">Смотреть</button>`;
    }
}

function renderTournaments(tournaments) {
    const list = document.getElementById('liveList');
    const empty = document.getElementById('empty');

    if (!tournaments || tournaments.length === 0) {
        list.classList.add('hidden');
        empty.classList.remove('hidden');
        document.getElementById('subtitle').textContent = '0 трансляций';
        return;
    }

    empty.classList.add('hidden');
    list.classList.remove('hidden');

    const liveCount = tournaments.filter(t => t.status === 'LIVE').length;
    document.getElementById('subtitle').textContent = tournaments.length + ' турниров • ' + liveCount + ' в эфире';

    list.innerHTML = tournaments.map(t => `
        <div class="live-card" onclick="window.location.href='/live/${t.externalId}'" style="${t.status === 'FINISHED' ? 'opacity:0.7;' : ''}${t.status === 'UPCOMING' ? 'border-color:rgba(245,158,11,0.25);' : ''}">
            <div class="flex items-center justify-between mb-3">
                <div class="flex items-center gap-3">
                    ${getStatusBadge(t.status)}
                    <h3 class="text-lg font-bold text-white">${escapeHtml(t.league || 'Турнир')}</h3>
                </div>
                <div class="flex items-center gap-2">
                    <span style="font-size:0.75rem; color:#a1a1aa;">👁 <b class="online-count-${t.externalId}" style="color:#818cf8;">...</b></span>
                    <span class="text-zinc-400 text-sm">${escapeHtml(t.time || '')}</span>
                </div>
            </div>
            <div class="flex items-center gap-2 text-zinc-500 text-sm mb-3">
                <span>${escapeHtml(t.hall || '')}</span>
                <span>•</span>
                <span>${escapeHtml(t.date || '')}</span>
            </div>
            ${t.players && t.players.length > 0 ? `
            <div class="flex flex-wrap gap-1.5 mb-4">
                ${t.players.map(p => `<span class="text-xs bg-white/5 border border-white/10 rounded-lg px-2.5 py-1 text-zinc-300">${escapeHtml(p)}</span>`).join('')}
            </div>` : ''}
            ${getButton(t.status, t.externalId)}
        </div>
    `).join('');

    loadAllOnlineCounts();
}

function renderLineups(lineups) {
    const section = document.getElementById('lineupsSection');
    if (!lineups || lineups.length === 0) {
        section.classList.add('hidden');
        return;
    }

    section.classList.remove('hidden');
    document.getElementById('lineupsContainer').innerHTML = lineups.map(l => `
        <div class="widget-card" onclick="window.location.href='/live/${l.id}'">
            <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                    <span class="text-sm font-semibold text-white">${escapeHtml(l.league || '?')}</span>
                    <span class="text-xs text-zinc-500">${escapeHtml(l.hall || '')}</span>
                </div>
                <div class="flex items-center gap-3">
                    <span class="text-xs text-indigo-400 font-semibold">${escapeHtml(l.time || '??:??')}</span>
                    <span class="text-xs text-zinc-500">▶</span>
                </div>
            </div>
            ${l.players ? `<p class="text-xs text-zinc-500 mt-1.5">${escapeHtml(l.players)}</p>` : ''}
        </div>
    `).join('');
}

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"]/g, c => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;' })[c]);
}

window.toggleHallsCheckboxes = toggleHallsCheckboxes;
window.toggleAllLiveHalls = toggleAllLiveHalls;
window.saveSelectedLiveHalls = saveSelectedLiveHalls;

document.getElementById('tabFilter').querySelectorAll('.tab-filter-tag').forEach(tag => {
    tag.addEventListener('click', function() {
        document.querySelectorAll('.tab-filter-tag').forEach(t => t.classList.remove('active'));
        this.classList.add('active');
        selectedTab = this.dataset.tab;
        applyFilter();
    });
});

loadLive();