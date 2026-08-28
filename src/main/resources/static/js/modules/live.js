let selectedTab = 'live';
let allTournaments = [];
let allHalls = [];
let savedHalls = [];
let hallsCollapsed = false;

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"]/g, c => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;' })[c]);
}

function initTabSlider() {
    const tabFilter = document.getElementById('tabFilter');
    if (!tabFilter || tabFilter.querySelector('.tab-slider')) return;

    const slider = document.createElement('div');
    slider.className = 'tab-slider pos-0';
    tabFilter.insertBefore(slider, tabFilter.firstChild);
}

function updateTabSlider(tab) {
    const slider = document.querySelector('.tab-slider');
    if (slider) {
        const positions = { 'live': 0, 'upcoming': 1, 'finished': 2 };
        slider.className = `tab-slider pos-${positions[tab] ?? 0}`;
    }
}

function startOnlinePolling() {
    setInterval(async () => {
        try {
            const res = await fetch('/api/tournament/online/all');
            const data = await res.json();
            for (const [id, count] of Object.entries(data)) {
                const el = document.querySelector('.online-count-' + id);
                if (el) el.textContent = count;
            }
        } catch(e) {}
    }, 5000);
}

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

        const [tournamentsRes, hallsRes] = await Promise.all([
            fetch('/api/tournament/live', { credentials: 'same-origin' }),
            fetch('/api/player/live/halls', { credentials: 'same-origin' })
        ]);

        document.getElementById('loading').classList.add('hidden');

        if (tournamentsRes.ok) allTournaments = await tournamentsRes.json(); else allTournaments = [];

        if (hallsRes.ok) {
            const text = await hallsRes.text();
            savedHalls = text ? text.split(',').map(h => h.trim()).filter(h => h) : [];
        } else {
            savedHalls = [];
        }

        const hallsSet = new Set();
        allTournaments.forEach(t => { if (t.hall) hallsSet.add(t.hall); });
        allHalls = [...hallsSet].sort();

        hallsCollapsed = localStorage.getItem('liveHallsCollapsed') === 'true';

        document.getElementById('tabFilter').classList.remove('hidden');

        initTabSlider();
        updateTabSlider('live');

        if (allHalls.length > 0) renderHallFilter();
        applyFilter();
        startOnlinePolling();
    } catch (e) {
        document.getElementById('loading').classList.add('hidden');
        document.getElementById('empty').classList.remove('hidden');
        document.getElementById('subtitle').textContent = 'Ошибка загрузки';
    }
}

function renderHallFilter() {
    const container = document.getElementById('hallFilter');
    container.classList.remove('hidden');

    const allSelected = savedHalls.length === 0 || savedHalls.length === allHalls.length;

    container.innerHTML = `
        <div class="apple-card mb-4 w-full">
            <div class="flex items-center justify-between mb-3 cursor-pointer" onclick="toggleHallsCheckboxes()">
                <h3 class="text-[15px] font-semibold text-white tracking-tight">Выберите залы</h3>
                <div class="flex items-center gap-2">
                    <label class="hall-select-all" onclick="event.stopPropagation()">
                        <input type="checkbox" id="allLiveHallsCheckbox" ${allSelected ? 'checked' : ''} onchange="toggleAllLiveHalls()" class="accent-indigo-500 w-4 h-4">
                        <span class="text-xs text-zinc-300 select-none">Все</span>
                    </label>
                    <span class="toggle-arrow text-zinc-400 text-xs transition-transform duration-300 ${hallsCollapsed ? 'rotate-180' : ''}" id="liveHallsToggleArrow">▼</span>
                </div>
            </div>
            <div id="liveHallsCheckboxesWrapper" class="${hallsCollapsed ? 'hidden' : ''}">
                <div class="grid grid-cols-3 gap-1.5" id="liveHallsCheckboxes">
                    ${allHalls.map(hall => {
        const checked = savedHalls.includes(hall);
        return `<label class="hall-checkbox-card ${checked ? 'selected' : ''}"><input type="checkbox" value="${hall}" ${checked ? 'checked' : ''} onchange="this.parentElement.classList.toggle('selected', this.checked)"><span class="text-xs text-white">${hall}</span></label>`;
    }).join('')}
                </div>
                <button onclick="saveSelectedLiveHalls()" class="apple-save-btn">Сохранить</button>
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
    await fetch('/api/player/live-halls', {
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

    renderTournaments(filtered);
}

function getStatusBadge(status) {
    switch(status) {
        case 'LIVE': return '<span class="live-badge"><span class="w-2 h-2 rounded-full bg-white"></span> LIVE</span>';
        case 'UPCOMING': return '<span class="status-badge-upcoming">Скоро</span>';
        case 'FINISHED': return '<span class="status-badge-finished">Завершён</span>';
        default: return '';
    }
}

function getButton(status, externalId) {
    switch(status) {
        case 'LIVE': return `<button class="apple-btn-live" onclick="event.stopPropagation(); openTournament('${externalId}')">Смотреть трансляцию</button>`;
        case 'UPCOMING': return `<button class="apple-btn-upcoming" disabled>Ожидание</button>`;
        case 'FINISHED': return `<button class="apple-btn-finished" onclick="event.stopPropagation(); openTournament('${externalId}')">Чат</button>`;
        default: return `<button class="apple-btn-live" onclick="event.stopPropagation(); openTournament('${externalId}')">Смотреть</button>`;
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

    list.innerHTML = tournaments.map((t, i) => `
        <div class="apple-card live-tournament-card" onclick="openTournament('${t.externalId}')" style="animation-delay: ${i * 50}ms; ${t.status === 'FINISHED' ? 'opacity:0.7;' : ''}${t.status === 'UPCOMING' ? 'border-color:rgba(245,158,11,0.25);' : ''}">
            <div class="flex items-center justify-between mb-3">
                <div class="flex items-center gap-3">
                    ${getStatusBadge(t.status)}
                    <h3 class="text-[17px] font-bold text-white tracking-tight">${escapeHtml(t.league || 'Турнир')}</h3>
                </div>
                <div class="flex items-center gap-3">
                    <span class="text-xs text-zinc-400">👁 <b class="online-count-${t.externalId}" style="color:#818cf8;">0</b></span>
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
}

window.toggleHallsCheckboxes = toggleHallsCheckboxes;
window.toggleAllLiveHalls = toggleAllLiveHalls;
window.saveSelectedLiveHalls = saveSelectedLiveHalls;

document.getElementById('tabFilter').querySelectorAll('.tab-filter-tag').forEach(tag => {
    tag.addEventListener('click', function() {
        document.querySelectorAll('.tab-filter-tag').forEach(t => t.classList.remove('active'));
        this.classList.add('active');
        selectedTab = this.dataset.tab;
        updateTabSlider(selectedTab);
        applyFilter();
    });
});

loadLive();