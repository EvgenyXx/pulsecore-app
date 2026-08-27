// js/admin/admin-tournaments.js

import { AdminAPI } from './admin-api.js';

let currentDate = getTodayString();
let expandedTournamentId = null;
let tournamentsCache = [];
let autoRefreshInterval = null;

function getTodayString() {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

export async function loadTournaments(date) {
    if (date) currentDate = date;

    const container = document.getElementById('tournamentList');
    if (!container) return;

    const dateLabel = document.getElementById('tournamentDateLabel');
    if (dateLabel) dateLabel.textContent = formatDate(currentDate);

    container.innerHTML = '<p class="text-center text-zinc-500 py-8">Загрузка...</p>';

    try {
        tournamentsCache = await AdminAPI.getTournamentsByDate(currentDate);
        renderTournamentList();
        startAutoRefresh();
    } catch (e) {
        console.error('Ошибка загрузки турниров:', e);
        container.innerHTML = '<p class="text-red-400 text-center py-8">Ошибка загрузки турниров</p>';
    }
}

function startAutoRefresh() {
    // Очищаем предыдущий интервал
    if (autoRefreshInterval) {
        clearInterval(autoRefreshInterval);
    }

    // Запускаем обновление каждые 30 секунд
    autoRefreshInterval = setInterval(async () => {
        // ПРОВЕРКА: если вкладка неактивна - пропускаем
        if (document.hidden) return;

        // ПРОВЕРКА: если раздел турниров не открыт - пропускаем
        const section = document.getElementById('section-tournaments');
        if (!section || section.classList.contains('hidden')) return;

        try {
            const freshData = await AdminAPI.getTournamentsByDate(currentDate);

            // Проверяем, есть ли реальные изменения
            if (JSON.stringify(freshData) !== JSON.stringify(tournamentsCache)) {
                tournamentsCache = freshData;

                // Если карточка развернута, обновляем только её содержимое
                if (expandedTournamentId) {
                    updateExpandedCard();
                } else {
                    renderTournamentList();
                }
            }
        } catch (e) {
            console.error('Ошибка автообновления:', e);
        }
    }, 30000); // 30 секунд
}

// ДОПОЛНИТЕЛЬНО: обновление при возвращении на вкладку
document.addEventListener('visibilitychange', () => {
    // Если вкладка стала активной
    if (!document.hidden) {
        const section = document.getElementById('section-tournaments');
        // Проверяем, открыт ли раздел турниров
        if (section && !section.classList.contains('hidden') && currentDate) {
            loadTournaments(currentDate);
        }
    }
});

// ДОПОЛНИТЕЛЬНО: обновление при фокусе на окне
window.addEventListener('focus', () => {
    const section = document.getElementById('section-tournaments');
    if (section && !section.classList.contains('hidden') && currentDate) {
        loadTournaments(currentDate);
    }
});

function updateExpandedCard() {
    if (!expandedTournamentId) return;

    const t = tournamentsCache.find(t => Number(t.id) === expandedTournamentId);
    if (!t) return;

    const card = document.getElementById(`tournament-card-${expandedTournamentId}`);
    if (!card) return;

    // Обновляем только содержимое развернутой карточки
    const players = parsePlayers(t.players);

    // Обновляем статусы
    const statuses = ['started', 'finished', 'cancelled', 'processed'];
    statuses.forEach(key => {
        const statusEl = document.getElementById(`status-${key}-${expandedTournamentId}`);
        if (statusEl) {
            if (t[key]) {
                statusEl.classList.add('checked');
                statusEl.textContent = '✓';
            } else {
                statusEl.classList.remove('checked');
                statusEl.textContent = '';
            }
        }

        // Обновляем метки статусов
        const labelEl = document.querySelector(`#tournament-card-${expandedTournamentId} .status-label`);
        if (labelEl) {
            labelEl.classList.toggle('active', t[key]);
        }
    });

    // Обновляем количество игроков
    const playersCount = document.querySelector(`#tournament-card-${expandedTournamentId} .tournament-card-meta`);
    if (playersCount) {
        playersCount.textContent = `Игроков: ${players.length} • ${t.link || ''}`;
    }

    // Обновляем список игроков
    const playersList = document.querySelector(`#tournament-card-${expandedTournamentId} .tournament-players-list`);
    if (playersList) {
        playersList.innerHTML = players.length
            ? players.map(p => `<span class="player-badge">${p.replace(/"/g, '')}</span>`).join('')
            : '<span class="text-xs text-zinc-500">Нет данных</span>';
    }

    // Обновляем бейдж статуса
    const badge = document.querySelector(`#tournament-card-${expandedTournamentId} .badge`);
    if (badge) {
        badge.className = `badge ${getStatusBadge(t)}`;
        badge.textContent = getStatusLabel(t);
    }
}

function renderTournamentList() {
    const container = document.getElementById('tournamentList');
    if (!container) return;

    if (!tournamentsCache || tournamentsCache.length === 0) {
        container.innerHTML = '<p class="text-center text-zinc-500 py-8">Нет турниров на эту дату</p>';
        return;
    }

    container.innerHTML = tournamentsCache.map(t => buildTournamentCard(t)).join('');
}

function buildTournamentCard(t) {
    const numericId = Number(t.id);
    const isExpanded = expandedTournamentId === numericId;
    const players = parsePlayers(t.players);

    return `
        <div class="tournament-card ${isExpanded ? 'expanded' : ''}" id="tournament-card-${numericId}">
            <div class="tournament-card-header" onclick="toggleTournamentExpand('${numericId}')">
                <div class="tournament-card-time">${t.time || '—'}</div>
                <div class="tournament-card-date">${formatDate(t.date)}</div>
                <span class="badge ${getStatusBadge(t)}">${getStatusLabel(t)}</span>
                <svg class="tournament-card-arrow" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
            </div>
            <div class="tournament-card-meta">
                Игроков: ${players.length} • ${t.link || ''}
            </div>

            ${isExpanded ? buildExpandedView(t, players, numericId) : ''}
        </div>
    `;
}

function buildExpandedView(t, players, numericId) {
    return `
        <div class="tournament-card-expanded">
            <div class="tournament-card-link">${t.link || '—'}</div>

            <div class="tournament-card-section-title">Дата и время</div>
            <div class="flex items-center gap-2 mb-3">
                <input id="tournament-date-${numericId}" type="date" value="${t.date || ''}" class="input w-auto">
                <input id="tournament-time-${numericId}" type="text" value="${t.time || ''}" class="input w-24" placeholder="Время">
            </div>

            <div class="tournament-card-section-title">Статусы</div>
            <div class="tournament-status-grid">
                ${buildStatusBox('started', 'Начат', t.started, numericId)}
                ${buildStatusBox('finished', 'Завершён', t.finished, numericId)}
                ${buildStatusBox('cancelled', 'Отменён', t.cancelled, numericId)}
                ${buildStatusBox('processed', 'Обработан', t.processed, numericId)}
            </div>

            <div class="tournament-card-section-title">Участники (${players.length})</div>
            <div class="tournament-players-list">
                ${players.length ? players.map(p => `<span class="player-badge">${p.replace(/"/g, '')}</span>`).join('') : '<span class="text-xs text-zinc-500">Нет данных</span>'}
            </div>

            <button class="btn btn-indigo save-btn" onclick="saveTournament('${numericId}')">Сохранить</button>
        </div>
    `;
}

function buildStatusBox(key, label, value, id) {
    return `
        <div class="status-item" onclick="toggleStatus('${key}', '${id}')">
            <span class="status-checkbox ${value ? 'checked' : ''}" id="status-${key}-${id}">
                ${value ? '✓' : ''}
            </span>
            <span class="status-label ${value ? 'active' : ''}">${label}</span>
        </div>
    `;
}

function parsePlayers(playersRaw) {
    if (!playersRaw) return [];
    if (Array.isArray(playersRaw)) return playersRaw;
    return [];
}

export function toggleStatus(key, id) {
    const el = document.getElementById(`status-${key}-${id}`);
    if (!el) return;
    el.classList.toggle('checked');
    el.textContent = el.classList.contains('checked') ? '✓' : '';

    const numericId = Number(id);
    const t = tournamentsCache.find(t => Number(t.id) === numericId);
    if (t) t[key] = el.classList.contains('checked');
}

export function toggleTournamentExpand(id) {
    const numericId = Number(id);
    expandedTournamentId = expandedTournamentId === numericId ? null : numericId;
    renderTournamentList();
}

export async function saveTournament(id) {
    const numericId = Number(id);

    const date = document.getElementById(`tournament-date-${numericId}`)?.value;
    const time = document.getElementById(`tournament-time-${numericId}`)?.value;

    const t = tournamentsCache.find(t => Number(t.id) === numericId);
    if (!t) return;

    const payload = {
        date: date || t.date,
        time: time || t.time,
        started: t.started,
        finished: t.finished,
        cancelled: t.cancelled,
        processed: t.processed
    };

    try {
        await AdminAPI.updateTournament(numericId, payload);

        // Обновляем данные в кэше БЕЗ перерисовки
        if (date) t.date = date;
        if (time) t.time = time;

        // Просто обновляем текст в шапке карточки
        const timeEl = document.querySelector(`#tournament-card-${numericId} .tournament-card-time`);
        const dateEl = document.querySelector(`#tournament-card-${numericId} .tournament-card-date`);
        if (timeEl) timeEl.textContent = time || t.time;
        if (dateEl) dateEl.textContent = formatDate(date || t.date);

    } catch (e) {
        alert('Ошибка сохранения');
    }
}

function getStatusLabel(t) {
    if (t.cancelled) return 'Отменён';
    if (t.finished) return 'Завершён';
    if (t.started) return 'Идёт';
    return 'Ожидание';
}

function getStatusBadge(t) {
    if (t.cancelled) return 'badge-inactive';
    if (t.finished) return 'badge-active';
    if (t.started) return 'badge-admin';
    return '';
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
}

window.loadTournaments = loadTournaments;
window.saveTournament = saveTournament;
window.toggleTournamentExpand = toggleTournamentExpand;
window.toggleStatus = toggleStatus;