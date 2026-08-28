// js/compare/settings-sheet.js

import { compareMetrics } from './metrics.js';
import { getPeriodDates } from './period.js';
import { initTooltips } from './tooltip.js';

let currentMode = 'versus';
let currentMetricId = 'money';
let currentPeriod = 'all';
let metricAccordionOpen = false;

export function initSettingsSheet() {
    renderMetricAccordion();
    initSegments();
}

function initSegments() {
    const modeRow = document.querySelector('.sheet-segment-row:has(#modeVersus)');
    const periodRow = document.querySelector('.sheet-segment-row:has(#periodAll)');

    if (modeRow && !modeRow.querySelector('.segment-slider')) {
        const slider = document.createElement('div');
        slider.className = 'segment-slider mode-2 pos-0';
        modeRow.insertBefore(slider, modeRow.firstChild);
    }

    if (periodRow && !periodRow.querySelector('.segment-slider')) {
        const slider = document.createElement('div');
        slider.className = 'segment-slider mode-4 pos-3';
        periodRow.insertBefore(slider, periodRow.firstChild);
    }

    updateModeSlider();
    updatePeriodSlider();
}

function updateModeSlider() {
    const row = document.querySelector('.sheet-segment-row:has(#modeVersus)');
    const slider = row?.querySelector('.segment-slider');
    if (slider) {
        slider.className = `segment-slider mode-2 ${currentMode === 'versus' ? 'pos-0' : 'pos-1'}`;
    }
}

function updatePeriodSlider() {
    const row = document.querySelector('.sheet-segment-row:has(#periodAll)');
    const slider = row?.querySelector('.segment-slider');
    if (slider) {
        const positions = { 'week': 0, 'month': 1, 'year': 2, 'all': 3 };
        slider.className = `segment-slider mode-4 pos-${positions[currentPeriod] ?? 3}`;
    }
}

function renderMetricAccordion() {
    const body = document.getElementById('metricAccordionBody');
    if (!body) return;

    body.innerHTML = compareMetrics.map(metric => `
        <button class="period-sheet-btn ${metric.id === currentMetricId ? 'active' : ''}" 
                onclick="setMetric('${metric.id}')">
            <span>${metric.label}</span>
            <span style="display: flex; align-items: center; gap: 8px;">
                ${metric.id === currentMetricId ? '<span class="sheet-check">✓</span>' : ''}
                <span class="tooltip-icon" 
                      data-tooltip="${metric.description}" 
                      onclick="event.stopPropagation(); event.preventDefault();">
                    ?
                </span>
            </span>
        </button>
    `).join('');

    initTooltips();
}

export function toggleMetricAccordion() {
    metricAccordionOpen = !metricAccordionOpen;
    const body = document.getElementById('metricAccordionBody');
    const arrow = document.getElementById('metricArrow');
    if (metricAccordionOpen) {
        body.style.display = 'block';
        arrow.style.transform = 'rotate(180deg)';
    } else {
        body.style.display = 'none';
        arrow.style.transform = 'rotate(0deg)';
    }
}

export function getSettings() {
    return { mode: currentMode, metricId: currentMetricId, period: currentPeriod };
}

export function getCurrentMetric() {
    return compareMetrics.find(m => m.id === currentMetricId) || compareMetrics[0];
}

export function toggleSettingsSheet() {
    const overlay = document.getElementById('settingsSheetOverlay');
    if (overlay) overlay.classList.toggle('open');
}

export function setMode(mode) {
    currentMode = mode;

    document.getElementById('modeVersus')?.classList.remove('active');
    document.getElementById('modeSingle')?.classList.remove('active');

    if (mode === 'versus') {
        document.getElementById('modeVersus')?.classList.add('active');
    } else {
        document.getElementById('modeSingle')?.classList.add('active');
    }

    updateModeSlider();
}

export function setMetric(metricId) {
    currentMetricId = metricId;
    renderMetricAccordion();
}

// Единая функция для установки периода
export function setPeriod(period) {
    currentPeriod = period;

    // Снимаем active со всех кнопок периодов
    document.querySelectorAll('[onclick*="setPeriod"]').forEach(btn => {
        btn.classList.remove('active');
    });

    // Активируем нужную кнопку
    const activeBtn = document.querySelector(`[onclick="setPeriod('${period}')"]`);
    if (activeBtn) {
        activeBtn.classList.add('active');
    }

    updatePeriodSlider();
}

// Для обратной совместимости
export function setPeriodFromSheet(period) {
    setPeriod(period);
}

export function applySettings() {
    const { start, end } = getPeriodDates(currentPeriod);
    const customStart = document.getElementById('customStart')?.value;
    const customEnd = document.getElementById('customEnd')?.value;

    if (customStart && customEnd) {
        toggleSettingsSheet();
        window.dispatchEvent(new CustomEvent('settings-applied', {
            detail: { mode: currentMode, metricId: currentMetricId, start: customStart, end: customEnd }
        }));
        return;
    }

    toggleSettingsSheet();
    window.dispatchEvent(new CustomEvent('settings-applied', {
        detail: { mode: currentMode, metricId: currentMetricId, start, end }
    }));
}

// Экспортируем для window
window.toggleSettingsSheet = toggleSettingsSheet;
window.toggleMetricAccordion = toggleMetricAccordion;
window.setMode = setMode;
window.setMetric = setMetric;
window.setPeriod = setPeriod;
window.setPeriodFromSheet = setPeriodFromSheet;
window.applySettings = applySettings;