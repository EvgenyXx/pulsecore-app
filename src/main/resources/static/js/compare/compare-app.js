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
    setPeriod,
    applySettings
} from './settings-sheet.js';

let allPlayers = [];
let allStatsPlayers = [];
let selectedLeft = null;
let selectedRight = null;
let leftWheel = null;
let rightWheel = null;
let currentMode = 'versus';

const subBlockHtml = () => `
    <div class="flex items-center justify-between mb-4">
        <div></div>
        <button onclick="toggleMobileMenu()" class="md:hidden w-9 h-9 flex items-center justify-center rounded-lg bg-white/5 hover:bg-white/10 active:scale-90 text-white">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
        </button>
    </div>
    <div class="apple-card p-8 text-center" style="animation: fadeIn 0.2s ease">
        <div class="w-14 h-14 rounded-full bg-indigo-500/10 flex items-center justify-center mx-auto mb-4">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
        </div>
        <h3 class="text-lg font-bold text-white mb-2">Требуется подписка</h3>
        <p class="text-zinc-400 text-sm mb-4">Оформите подписку чтобы открыть все функции</p>
        <a href="/subscribe" class="inline-block bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-full px-6 py-3 text-sm transition-all">Оформить подписку</a>
    </div>
`;

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

async function loadCompare() {
    try {
        const res = await fetch('/api/player/halls', { credentials: 'same-origin' });
        if (res.status === 402) {
            document.querySelector('.wheels-row').style.display = 'none';
            document.querySelector('.cards-row').style.display = 'none';
            document.querySelector('.compare-header').style.display = 'none';

            const container = document.querySelector('#comparePage .max-w-5xl');
            container.insertAdjacentHTML('beforeend', `<div id="compareNoSub">${subBlockHtml()}</div>`);
            return;
        }

        document.querySelector('.wheels-row').style.display = '';
        document.querySelector('.cards-row').style.display = '';
        document.querySelector('.compare-header').style.display = '';
        document.getElementById('compareNoSub')?.remove();

        await loadAllPlayers(null, null);
    } catch (e) {
        console.error('Ошибка загрузки H2H:', e);
    }
}

// Экспортируем функции в window
window.toggleSettingsSheet = toggleSettingsSheet;
window.toggleMetricAccordion = toggleMetricAccordion;
window.setMode = setMode;
window.setMetric = setMetric;
window.setPeriod = setPeriod;
window.setPeriodFromSheet = setPeriod;
window.applySettings = applySettings;
window.loadCompare = loadCompare;
window.initCompareApp = loadCompare;

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
});