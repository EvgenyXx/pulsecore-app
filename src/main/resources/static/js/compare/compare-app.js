// js/compare/compare-app.js

import { compareMetrics } from './metrics.js';
import { renderPlayerCard } from './player-card.js';
import { WheelPicker } from './wheel.js';
import { getPeriodDates } from './period.js';
import { loadPlayers as fetchPlayers, loadStatsPlayers } from './data-loader.js';
import { initTooltips } from './tooltip.js';
import {
    initSettingsSheet,
    getSettings,
    getCurrentMetric,
    toggleSettingsSheet,
    setMode,
    setMetric,
    setPeriodFromSheet,
    applySettings
} from './settings-sheet.js';

let allPlayers = [];
let allStatsPlayers = [];
let selectedLeft = null;
let selectedRight = null;
let leftWheel = null;
let rightWheel = null;
let currentMode = 'versus';

function getRandomPlayers(players, count) {
    const shuffled = [...players].sort(() => Math.random() - 0.5);
    return shuffled.slice(0, Math.min(count, shuffled.length));
}

function updateCard(cardId, player) {
    const card = document.getElementById(cardId);
    const metric = getCurrentMetric();

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
        });
        rightWheel = new WheelPicker('pickerRight', allPlayers, (player) => {
            selectedRight = player;
            updateCard('rightCard', player);
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

window.toggleSettingsSheet = toggleSettingsSheet;
window.setMode = setMode;
window.setMetric = setMetric;
window.setPeriod = setPeriodFromSheet;
window.applySettings = applySettings;

window.addEventListener('settings-applied', (e) => {
    const { mode, metricId, start, end } = e.detail;

    currentMode = mode;

    if (mode === 'versus') {
        renderVersusMode();
    } else {
        renderSingleMode();
    }

    loadAllPlayers(start, end);
});

document.addEventListener('DOMContentLoaded', () => {
    initSettingsSheet();
    initTooltips();
    loadAllPlayers(null, null);
});