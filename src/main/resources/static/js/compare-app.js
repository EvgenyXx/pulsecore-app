import { API } from './core/api.js';
import { state } from './core/state.js';

let allPlayers = [];
let selectedLeft = null;
let selectedRight = null;

function formatMoney(value) {
    return new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 0 }).format(value || 0);
}

function renderPicker(containerId, players, selectedId) {
    const container = document.getElementById(containerId);
    container.innerHTML = players.map(p => `
        <div class="player-item ${p.playerId === selectedId ? 'selected' : ''}" data-player-id="${p.playerId}">
            ${p.playerName}
        </div>
    `).join('');
}

function updateCard(cardId, player) {
    const card = document.getElementById(cardId);
    if (!player) {
        card.innerHTML = '<p class="text-zinc-500 text-sm">Выберите игрока</p>';
        return;
    }
    card.innerHTML = `
        <p class="text-white font-semibold text-sm">${player.playerName}</p>
        <p class="text-zinc-500 text-xs mt-1">Лига: ${player.primaryLeague || '—'}</p>
        <div class="stat-row mt-2"><span class="stat-label">Турниров:</span><span class="stat-value">${player.tournaments || 0}</span></div>
        <div class="stat-row"><span class="stat-label">Заработано:</span><span class="stat-value">${formatMoney(player.totalAmount)}</span></div>
        <div class="stat-row"><span class="stat-label">Средний:</span><span class="stat-value">${formatMoney(player.averageAmount)}</span></div>
    `;
}

function scrollToPlayer(pickerId, playerId) {
    const picker = document.getElementById(pickerId);
    const item = picker.querySelector(`[data-player-id="${playerId}"]`);
    if (item) {
        item.scrollIntoView({ behavior: 'instant', block: 'center' });
    }
}

function attachPickerEvents(pickerId, cardId, side) {
    const picker = document.getElementById(pickerId);

    picker.addEventListener('scroll', () => {
        const items = picker.querySelectorAll('.player-item');
        const pickerCenter = picker.scrollTop + picker.clientHeight / 2;

        let closestItem = null;
        let closestDistance = Infinity;

        items.forEach(item => {
            const itemCenter = item.offsetTop + item.clientHeight / 2;
            const distance = Math.abs(itemCenter - pickerCenter);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestItem = item;
            }
        });

        if (closestItem) {
            items.forEach(i => i.classList.remove('selected'));
            closestItem.classList.add('selected');
            const playerId = closestItem.dataset.playerId;
            const player = allPlayers.find(p => p.playerId === playerId);

            if (side === 'left') {
                selectedLeft = player;
                updateCard(cardId, player);
            } else {
                selectedRight = player;
                updateCard(cardId, player);
            }
        }
    });

    picker.addEventListener('click', (e) => {
        const item = e.target.closest('.player-item');
        if (item) {
            item.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    });
}

function getRandomPlayers(players, count) {
    const shuffled = [...players].sort(() => Math.random() - 0.5);
    return shuffled.slice(0, Math.min(count, shuffled.length));
}

async function loadPlayers(start, end) {
    try {
        let url = '/api/tournament/compare/players';
        const params = new URLSearchParams();
        if (start) params.append('start', start);
        if (end) params.append('end', end);
        if (params.toString()) url += '?' + params.toString();

        const response = await fetch(url, { credentials: 'same-origin' });
        if (!response.ok) throw new Error('HTTP ' + response.status);

        allPlayers = await response.json();

        // Выбираем двух случайных игроков
        const random = getRandomPlayers(allPlayers, 2);
        selectedLeft = random[0] || null;
        selectedRight = random[1] || null;

        // Рендерим пикеры
        renderPicker('pickerLeft', allPlayers, selectedLeft?.playerId);
        renderPicker('pickerRight', allPlayers, selectedRight?.playerId);

        // Обновляем карточки
        updateCard('leftCard', selectedLeft);
        updateCard('rightCard', selectedRight);

        // Скроллим к выбранным
        setTimeout(() => {
            if (selectedLeft) scrollToPlayer('pickerLeft', selectedLeft.playerId);
            if (selectedRight) scrollToPlayer('pickerRight', selectedRight.playerId);
        }, 50);

    } catch (e) {
        console.error('Ошибка загрузки игроков:', e);
    }
}

window.togglePeriodSheet = function() {
    const overlay = document.getElementById('periodSheetOverlay');
    overlay.classList.toggle('open');
};

window.setPeriod = function(period) {
    const label = document.getElementById('periodLabel');
    const today = new Date();
    let start = null;
    let end = today.toISOString().split('T')[0];

    switch (period) {
        case 'week':
            const day = today.getDay() || 7;
            const monday = new Date(today);
            monday.setDate(today.getDate() - day + 1);
            start = monday.toISOString().split('T')[0];
            label.textContent = 'Неделя';
            break;
        case 'month':
            start = new Date(today.getFullYear(), today.getMonth(), 1).toISOString().split('T')[0];
            label.textContent = 'Месяц';
            break;
        case 'year':
            start = new Date(today.getFullYear(), 0, 1).toISOString().split('T')[0];
            label.textContent = 'Год';
            break;
        case 'all':
            start = null;
            end = null;
            label.textContent = 'Всё время';
            break;
    }

    // Обновляем активную кнопку в шторке
    document.querySelectorAll('.period-sheet-btn').forEach(b => b.classList.remove('active'));
    if (event && event.target) {
        event.target.classList.add('active');
    }

    // Закрываем шторку
    togglePeriodSheet();

    // Загружаем
    loadPlayers(start, end);
};

window.loadCustom = function() {
    const start = document.getElementById('customStart').value;
    const end = document.getElementById('customEnd').value;

    if (start && end) {
        document.getElementById('periodLabel').textContent = `${start} — ${end}`;
        togglePeriodSheet();
        loadPlayers(start, end);
    }
};

// Инициализация
document.addEventListener('DOMContentLoaded', () => {
    attachPickerEvents('pickerLeft', 'leftCard', 'left');
    attachPickerEvents('pickerRight', 'rightCard', 'right');
    loadPlayers(null, null);
});