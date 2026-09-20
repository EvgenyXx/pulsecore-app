// js/compare/compare-app.js

import { compareMetrics } from './metrics.js';
import { renderPlayerCard } from './player-card.js';
import { renderH2HCard } from './h2h-card.js';
import { WheelPicker } from './wheel.js';
import { PlayerSearch } from './player-search.js';
import { getPeriodDates } from './period.js';
import { loadPlayers as fetchPlayers, loadStatsPlayers } from './data-loader.js';
import { initTooltips } from './tooltip.js';
import { checkSubscription, subBlockHtml } from '../modules/subscription-block.js';
import {
    initSettingsSheet,
    getSettings,
    getCurrentMetric,
    toggleSettingsSheet,
    setMode,
    setMetric,
    setPeriod,
    applySettings
} from './settings-sheet.js';

let allPlayers = [];
let allStatsPlayers = [];
let selectedLeft = null;
let selectedRight = null;
let leftWheel = null;
let rightWheel = null;
let leftSearch = null;
let rightSearch = null;
let currentMode = 'versus';
let currentPeriod = 'all';
let customStartDate = null;
let customEndDate = null;

function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
}

function getRandomPlayers(players, count) {
    const shuffled = [...players].sort(() => Math.random() - 0.5);
    return shuffled.slice(0, Math.min(count, shuffled.length));
}

function updateCard(cardId, player) {
    const card = document.getElementById(cardId);
    const metric = getCurrentMetric();

    if (metric.id === 'h2h') {
        card.innerHTML = '';
        return;
    }

    if (!player) {
        card.innerHTML = '<p class="text-zinc-500 text-sm text-center">Выберите игрока</p>';
        return;
    }

    let data = player;

    if (metric.id === 'stats') {
        data = allStatsPlayers.find(p =>
            p.playerName.toLowerCase() === player.playerName.toLowerCase()
        );
    }

    card.innerHTML = renderPlayerCard(player, metric, data);
}

function updateBothCards() {
    updateCard('leftCard', selectedLeft);
    updateCard('rightCard', selectedRight);
}

function showSearchRow() {
    const searchRow = document.getElementById('playerSearchRow');
    if (searchRow) searchRow.style.display = '';
}

function hideSearchRow() {
    const searchRow = document.getElementById('playerSearchRow');
    if (searchRow) searchRow.style.display = 'none';
}

function renderVersusMode() {
    document.getElementById('pickerRight').closest('.wheel-container').style.display = '';
    document.querySelector('.vs-badge').style.display = '';
    document.getElementById('rightCard').style.display = '';
    document.querySelector('.card-spacer').style.display = '';
}

function renderSingleMode() {
    document.getElementById('pickerRight').closest('.wheel-container').style.display = 'none';
    document.querySelector('.vs-badge').style.display = 'none';
    document.getElementById('rightCard').style.display = 'none';
    document.querySelector('.card-spacer').style.display = 'none';
}

function toggleCardsVisibility(metricId) {
    const cardsRow = document.getElementById('cardsRow');
    if (!cardsRow) return;

    if (metricId === 'h2h') {
        cardsRow.style.display = 'none';
    } else {
        cardsRow.style.display = '';
    }
}

function showH2HButton() {
    const wrapper = document.getElementById('h2hCompareBtnWrapper');
    if (wrapper) wrapper.style.display = '';
    const result = document.getElementById('h2hResult');
    if (result) {
        result.classList.add('hidden');
        result.innerHTML = '';
    }
    toggleCardsVisibility('h2h');
}

function hideH2HButton() {
    const wrapper = document.getElementById('h2hCompareBtnWrapper');
    if (wrapper) wrapper.style.display = 'none';
    const result = document.getElementById('h2hResult');
    if (result) {
        result.classList.add('hidden');
        result.innerHTML = '';
    }
    toggleCardsVisibility('other');
}

function hideH2HResult() {
    const resultContainer = document.getElementById('h2hResult');
    if (resultContainer) {
        resultContainer.classList.add('hidden');
        resultContainer.innerHTML = '';
    }
}

async function compareH2H() {
    if (!selectedLeft || !selectedRight) return;

    const btn = document.getElementById('h2hCompareBtn');
    if (!btn) return;
    btn.disabled = true;
    btn.textContent = 'Загрузка...';

    try {
        let start = customStartDate;
        let end = customEndDate;

        if (!start || !end) {
            const dates = getPeriodDates(currentPeriod || 'all');
            start = dates.start;
            end = dates.end;
        }

        const params = new URLSearchParams({
            player1Name: selectedLeft.playerName,
            player2Name: selectedRight.playerName
        });

        if (start) params.append('start', start);
        if (end) params.append('end', end);

        const res = await fetch(`/api/tournament/compare/h2h?${params}`, {
            credentials: 'same-origin'
        });

        if (!res.ok) throw new Error('HTTP ' + res.status);

        const data = await res.json();

        let dateRange = '';
        if (start && end) {
            dateRange = `${formatDate(start)} — ${formatDate(end)}`;
        } else if (start) {
            dateRange = `С ${formatDate(start)}`;
        } else if (end) {
            dateRange = `До ${formatDate(end)}`;
        } else {
            dateRange = 'За всё время';
        }

        const resultContainer = document.getElementById('h2hResult');
        resultContainer.innerHTML = renderH2HCard(data, dateRange);
        resultContainer.classList.remove('hidden');
        resultContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

    } catch (e) {
        console.error('Ошибка H2H:', e);
        const resultContainer = document.getElementById('h2hResult');
        resultContainer.innerHTML = '<p class="text-red-400 text-center py-4">Ошибка загрузки H2H</p>';
        resultContainer.classList.remove('hidden');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Сравнить';
    }
}

function handleSearchSelect(side, playerName) {
    const found = allPlayers.find(p =>
        p.playerName.toLowerCase() === playerName.toLowerCase()
    );

    const player = found || { playerName: playerName, playerId: null };

    if (side === 'left') {
        selectedLeft = player;
        if (found && leftWheel) leftWheel.scrollToPlayer(found.playerId);
        updateCard('leftCard', player);
    } else {
        selectedRight = player;
        if (found && rightWheel) rightWheel.scrollToPlayer(found.playerId);
        updateCard('rightCard', player);
    }

    hideH2HResult();
}

function initSearch() {
    if (leftSearch) return;

    leftSearch = new PlayerSearch('searchLeft', 'searchLeftDropdown', (name) => {
        handleSearchSelect('left', name);
    });

    rightSearch = new PlayerSearch('searchRight', 'searchRightDropdown', (name) => {
        handleSearchSelect('right', name);
    });
}

async function loadAllPlayers(start, end) {
    try {
        allPlayers = await fetchPlayers(start, end);

        try {
            allStatsPlayers = await loadStatsPlayers(start, end);
        } catch (e) {
            allStatsPlayers = [];
        }

        const random = getRandomPlayers(allPlayers, 2);
        selectedLeft = random[0] || null;
        selectedRight = random[1] || null;

        if (leftWheel) leftWheel.destroy();
        if (rightWheel) rightWheel.destroy();

        leftWheel = new WheelPicker('pickerLeft', allPlayers, (player) => {
            selectedLeft = player;
            updateCard('leftCard', player);
            if (leftSearch) leftSearch.setValue(player.playerName);
            hideH2HResult();
        });
        rightWheel = new WheelPicker('pickerRight', allPlayers, (player) => {
            selectedRight = player;
            updateCard('rightCard', player);
            if (rightSearch) rightSearch.setValue(player.playerName);
            hideH2HResult();
        });

        leftWheel.render(selectedLeft?.playerId);
        rightWheel.render(selectedRight?.playerId);

        updateBothCards();

        setTimeout(() => {
            if (selectedLeft) leftWheel.scrollToPlayer(selectedLeft.playerId);
            if (selectedRight) rightWheel.scrollToPlayer(selectedRight.playerId);
        }, 50);

    } catch (e) {
        console.error('Ошибка загрузки игроков:', e);
    }
}

async function loadCompare() {
    try {
        const hasSub = await checkSubscription();

        if (!hasSub) {
            document.querySelector('.wheels-row').style.display = 'none';
            document.querySelector('.cards-row').style.display = 'none';
            document.querySelector('.compare-header').style.display = 'none';
            document.getElementById('playerSearchRow')?.style.setProperty('display', 'none');
            document.getElementById('h2hCompareBtnWrapper')?.remove();

            const container = document.querySelector('#comparePage .max-w-5xl');
            if (!document.getElementById('compareNoSub')) {
                container.insertAdjacentHTML('beforeend', `<div id="compareNoSub">${subBlockHtml()}</div>`);
            }
            return;
        }

        document.querySelector('.wheels-row').style.display = '';
        document.querySelector('.cards-row').style.display = '';
        document.querySelector('.compare-header').style.display = '';
        document.getElementById('compareNoSub')?.remove();

        initSearch();

        if (currentMode === 'versus') {
            renderVersusMode();
        } else {
            renderSingleMode();
        }

        const metric = getCurrentMetric();
        if (metric.id === 'h2h') {
            showSearchRow();
            showH2HButton();
        } else {
            hideSearchRow();
            hideH2HButton();
        }

        await loadAllPlayers(null, null);
    } catch (e) {
        console.error('Ошибка загрузки H2H:', e);
    }
}

window.toggleSettingsSheet = toggleSettingsSheet;
window.toggleMetricAccordion = toggleMetricAccordion;
window.setMode = setMode;
window.setMetric = setMetric;
window.setPeriod = setPeriod;
window.setPeriodFromSheet = setPeriod;
window.applySettings = applySettings;
window.loadCompare = loadCompare;
window.initCompareApp = loadCompare;
window.compareH2H = compareH2H;

window.addEventListener('settings-applied', (e) => {
    const { mode, metricId, start, end } = e.detail;

    currentMode = mode;
    currentPeriod = getSettings().period || currentPeriod;

    const customStartInput = document.getElementById('customStart');
    const customEndInput = document.getElementById('customEnd');
    customStartDate = customStartInput?.value || null;
    customEndDate = customEndInput?.value || null;

    if (mode === 'versus') {
        renderVersusMode();
    } else {
        renderSingleMode();
    }

    if (metricId === 'h2h') {
        showSearchRow();
        showH2HButton();
    } else {
        hideSearchRow();
        hideH2HButton();
    }

    loadAllPlayers(start, end);
});

document.addEventListener('DOMContentLoaded', () => {
    initSettingsSheet();
    initTooltips();
});