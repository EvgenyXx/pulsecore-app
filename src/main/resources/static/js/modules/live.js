let selectedHall = null;
let allTournaments = [];
let allLineups = [];
let allHalls = [];

async function loadLive() {
    try {
        const today = new Date().toISOString().split('T')[0];

        const [tournamentsRes, lineupsRes] = await Promise.all([
            fetch('/api/tournament/live', { credentials: 'same-origin' }),
            fetch(`/api/lineups?date=${today}`, { credentials: 'same-origin' })
        ]);

        document.getElementById('loading').classList.add('hidden');

        if (tournamentsRes.ok) {
            allTournaments = await tournamentsRes.json();
        } else {
            allTournaments = [];
        }

        if (lineupsRes.ok) {
            allLineups = await lineupsRes.json();
        } else {
            allLineups = [];
        }

        const hallsSet = new Set();
        allTournaments.forEach(t => { if (t.hall) hallsSet.add(t.hall); });
        allLineups.forEach(l => { if (l.hall) hallsSet.add(l.hall); });
        allHalls = [...hallsSet].sort();

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
                    if (count > 0) {
                        el.textContent = count;
                        el.style.display = 'inline';
                    } else {
                        el.textContent = '0';
                        el.style.display = 'inline';
                    }
                }
            }
        } catch(e) {}
    }
}

function renderHallFilter() {
    const container = document.getElementById('hallFilter');
    container.classList.remove('hidden');
    container.innerHTML = `<span class="hall-filter-tag active" onclick="filterByHall(null)">Все залы</span>` +
        allHalls.map(h => `<span class="hall-filter-tag" onclick="filterByHall('${escapeHtml(h)}')">${escapeHtml(h)}</span>`).join('');
}

function filterByHall(hall) {
    selectedHall = hall;
    document.querySelectorAll('.hall-filter-tag').forEach(t => t.classList.remove('active'));
    event.target.classList.add('active');
    applyFilter();
}

function applyFilter() {
    const filteredTournaments = selectedHall
        ? allTournaments.filter(t => t.hall === selectedHall)
        : allTournaments;

    const filteredLineups = selectedHall
        ? allLineups.filter(l => l.hall === selectedHall)
        : allLineups;

    renderTournaments(filteredTournaments);
    renderLineups(filteredLineups);
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
    document.getElementById('subtitle').textContent = tournaments.length + ' активных трансляций';

    list.innerHTML = tournaments.map(t => `
        <div class="live-card" onclick="window.location.href='/live/${t.externalId}'">
            <div class="flex items-center justify-between mb-3">
                <div class="flex items-center gap-3">
                    <span class="live-badge"><span class="w-2 h-2 rounded-full bg-white"></span> LIVE</span>
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
            <button class="btn-live">▶ Смотреть трансляцию</button>
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

window.filterByHall = filterByHall;
loadLive();