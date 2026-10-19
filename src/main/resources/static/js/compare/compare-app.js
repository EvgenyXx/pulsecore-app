// js/compare/compare-app.js

import { renderH2HCard, renderMatchRow } from './h2h-card.js';
import { renderSalaryH2HCard } from './salary-card.js';
import { PlayerSearch } from './player-search.js';
import { getPeriodDates } from './period.js';
import { initTooltips } from './tooltip.js';
import { checkSubscription, subBlockHtml } from '../modules/subscription-block.js';
import {
    initSettingsSheet,
    getSettings,
    toggleSettingsSheet,
    setMode,
    setMetric,
    setPeriod,
    applySettings
} from './settings-sheet.js';

const MATCHES_PAGE_SIZE = 20;

let selectedLeft = null;
let selectedRight = null;
let leftSearch = null;
let rightSearch = null;
let currentMode = 'versus';
let currentPeriod = 'all';
let customStartDate = null;
let customEndDate = null;

// ===== Лента матчей =====
let matchesPage = 0;
let matchesHasMore = false;
let matchesLoading = false;
let matchesRequestId = 0;

function showSearchRow() {
    const searchRow = document.getElementById('playerSearchRow');
    if (searchRow) searchRow.style.display = '';
}

function showH2HButton() {
    const wrapper = document.getElementById('h2hCompareBtnWrapper');
    if (wrapper) wrapper.style.display = '';
    const result = document.getElementById('h2hResult');
    if (result) {
        result.classList.add('hidden');
        result.innerHTML = '';
    }
}

function hideH2HResult() {
    const resultContainer = document.getElementById('h2hResult');
    if (resultContainer) {
        resultContainer.classList.add('hidden');
        resultContainer.innerHTML = '';
    }
    matchesPage = 0;
    matchesHasMore = false;
    matchesLoading = false;
    matchesRequestId++;
}

// ===== Расчёт дат =====
function resolveDates() {
    let start = customStartDate;
    let end = customEndDate;

    if (!start || !end) {
        const dates = getPeriodDates(currentPeriod || 'all');
        start = dates.start;
        end = dates.end;
    }

    return { start, end };
}

// ===== Красивый рендер ошибки (в стиле крупных брендов) =====
function renderError(container, title, message) {
    container.innerHTML = `
        <div style="
            background: rgba(239, 68, 68, 0.05);
            border: 1px solid rgba(239, 68, 68, 0.2);
            border-radius: 16px;
            padding: 24px;
            text-align: center;
            margin-top: 16px;
            backdrop-filter: blur(10px);
        ">
            <div style="color: #fca5a5; font-weight: 700; font-size: 16px; margin-bottom: 6px;">${title}</div>
            <div style="color: #ef4444; font-size: 13px; line-height: 1.5;">${message}</div>
        </div>
    `;
    container.classList.remove('hidden');
}

// ===== Основной compare =====
async function compareH2H() {
    if (!selectedLeft || !selectedRight) return;

    const { start, end } = resolveDates();

    // Проверка на фронте: если дат нет, показываем ошибку и не шлем запрос
    if (!start || !end) {
        const resultContainer = document.getElementById('h2hResult');
        renderError(resultContainer, 'Не указан период', 'Выберите даты начала и окончания периода в настройках.');
        return;
    }

    const btn = document.getElementById('h2hCompareBtn');
    if (!btn) return;
    btn.disabled = true;
    btn.textContent = 'Загрузка...';

    try {
        const settings = getSettings();
        const metricId = settings.metricId || 'h2h';

        let endpoint = '/api/tournament/compare/h2h';
        if (metricId === 'salary') {
            endpoint = '/api/tournament/compare/salary/h2h';
        }

        const params = new URLSearchParams({
            player1Name: selectedLeft.playerName,
            player2Name: selectedRight.playerName,
            start: start,
            end: end
        });

        const res = await fetch(`${endpoint}?${params}`, {
            credentials: 'same-origin'
        });

        const resultContainer = document.getElementById('h2hResult');

        if (!res.ok) {
            let errorMessage = 'Проверьте соединение и попробуйте снова.';
            try {
                const errorData = await res.json();
                if (errorData.message) {
                    errorMessage = errorData.message;
                }
            } catch (e) {
                // Если не удалось распарсить JSON, оставляем дефолтный текст
            }
            renderError(resultContainer, 'Ошибка данных', errorMessage);
            return;
        }

        const data = await res.json();

        if (metricId === 'salary') {
            resultContainer.innerHTML = renderSalaryH2HCard(data);
            resultContainer.classList.remove('hidden');
            resultContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        } else {
            resultContainer.innerHTML = renderH2HCard(data);
            resultContainer.classList.remove('hidden');
            resultContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

            initMatchesScroll();
            matchesPage = 0;
            matchesHasMore = true;
            matchesLoading = false;
            matchesRequestId++;
            const reqId = matchesRequestId;
            await loadMatchesPage(reqId, true);
        }
    } catch (e) {
        console.error('Ошибка сравнения:', e);
        const resultContainer = document.getElementById('h2hResult');
        renderError(resultContainer, 'Нет соединения', 'Сервер временно недоступен. Попробуйте позже.');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Сравнить';
    }
}

// ===== Лента матчей (только для метрики h2h) =====
async function loadMatchesPage(reqId, reset = false) {
    if (matchesLoading) return;
    if (!matchesHasMore && !reset) return;
    if (!selectedLeft || !selectedRight) return;

    matchesLoading = true;

    const list = document.getElementById('h2hMatchesList');
    const empty = document.getElementById('h2hMatchesEmpty');

    if (reset && list) {
        list.innerHTML = `
            <div class="h2h-matches-loader">
                <div class="spinner-sm"></div>
                <span>Загрузка матчей…</span>
            </div>
        `;
    }

    try {
        const { start, end } = resolveDates();
        const params = new URLSearchParams({
            player1Name: selectedLeft.playerName,
            player2Name: selectedRight.playerName,
            page: matchesPage,
            size: MATCHES_PAGE_SIZE
        });
        if (start) params.append('start', start);
        if (end) params.append('end', end);

        const res = await fetch(`/api/tournament/compare/h2h/matches?${params}`, {
            credentials: 'same-origin'
        });
        if (!res.ok) throw new Error('HTTP ' + res.status);

        const page = await res.json();

        if (reqId !== matchesRequestId) return;

        const items = page.content || [];

        if (!list) return;

        if (reset) list.innerHTML = '';

        const loader = list.querySelector('.h2h-matches-loader');
        if (loader) loader.remove();

        if (page.number === 0 && items.length === 0) {
            empty?.classList.remove('hidden');
            matchesHasMore = false;
            return;
        }

        empty?.classList.add('hidden');

        const html = items.map(m =>
            renderMatchRow(m, selectedLeft.playerName, selectedRight.playerName)
        ).join('');

        list.insertAdjacentHTML('beforeend', html);

        matchesPage = (page.number ?? 0) + 1;
        matchesHasMore = !page.last;

        if (matchesHasMore) {
            list.insertAdjacentHTML('beforeend', `
                <div class="h2h-matches-loader" id="h2hMatchesLoader">
                    <div class="spinner-sm"></div>
                </div>
            `);
        }

    } catch (e) {
        console.error('Ошибка загрузки матчей:', e);
    } finally {
        matchesLoading = false;
    }
}

// Наблюдатель за скроллом СПИСКА МАТЧЕЙ — бесконечная подгрузка
function initMatchesScroll() {
    const list = document.querySelector('.h2h-matches-scroll');
    if (!list || list.dataset.h2hBound === '1') return;
    list.dataset.h2hBound = '1';

    list.addEventListener('scroll', () => {
        if (matchesLoading || !matchesHasMore) return;

        const loader = document.getElementById('h2hMatchesLoader');
        if (!loader) return;

        const rect = loader.getBoundingClientRect();
        const viewport = list.getBoundingClientRect();

        if (rect.top <= viewport.bottom + 80) {
            loadMatchesPage(matchesRequestId, false);
        }
    });
}

// ===== Поиск =====
function handleSearchSelect(side, playerName) {
    const player = { playerName: playerName };

    if (side === 'left') {
        selectedLeft = player;
    } else {
        selectedRight = player;
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

async function loadCompare() {
    try {
        const hasSub = await checkSubscription();

        if (!hasSub) {
            document.querySelector('.compare-header').style.display = 'none';
            document.getElementById('playerSearchRow')?.style.setProperty('display', 'none');
            document.getElementById('h2hCompareBtnWrapper')?.remove();

            const container = document.querySelector('#comparePage .max-w-5xl');
            if (!document.getElementById('compareNoSub')) {
                container.insertAdjacentHTML('beforeend', `<div id="compareNoSub">${subBlockHtml()}</div>`);
            }
            return;
        }

        document.querySelector('.compare-header').style.display = '';
        document.getElementById('compareNoSub')?.remove();

        initSearch();

        showSearchRow();
        showH2HButton();
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
    const detail = e.detail || {};

    currentMode = detail.mode || currentMode;
    currentPeriod = detail.period || currentPeriod;

    if (detail.metricId) {
        const { setMetric } = window;
        if (setMetric) setMetric(detail.metricId);
    }

    const customStartInput = document.getElementById('customStart');
    const customEndInput = document.getElementById('customEnd');
    customStartDate = customStartInput?.value || null;
    customEndDate = customEndInput?.value || null;

    showSearchRow();
    showH2HButton();
});

export function init() {
    initSettingsSheet();
    initTooltips();
}