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
    return {
        mode: currentMode,
        metricId: currentMetricId,
        period: currentPeriod
    };
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
}

export function setMetric(metricId) {
    currentMetricId = metricId;
    renderMetricAccordion();
}

export function setPeriodFromSheet(period) {
    currentPeriod = period;

    document.querySelector('[onclick="setPeriod(\'week\')"]')?.classList.remove('active');
    document.querySelector('[onclick="setPeriod(\'month\')"]')?.classList.remove('active');
    document.querySelector('[onclick="setPeriod(\'year\')"]')?.classList.remove('active');
    document.getElementById('periodAll')?.classList.remove('active');

    const periodButtons = {
        'week': document.querySelector('[onclick="setPeriod(\'week\')"]'),
        'month': document.querySelector('[onclick="setPeriod(\'month\')"]'),
        'year': document.querySelector('[onclick="setPeriod(\'year\')"]'),
        'all': document.getElementById('periodAll')
    };

    const btn = periodButtons[period];
    if (btn) btn.classList.add('active');
}

export function applySettings() {
    const { start, end } = getPeriodDates(currentPeriod);
    const customStart = document.getElementById('customStart')?.value;
    const customEnd = document.getElementById('customEnd')?.value;

    if (customStart && customEnd) {
        toggleSettingsSheet();
        window.dispatchEvent(new CustomEvent('settings-applied', {
            detail: {
                mode: currentMode,
                metricId: currentMetricId,
                start: customStart,
                end: customEnd
            }
        }));
        return;
    }

    toggleSettingsSheet();
    window.dispatchEvent(new CustomEvent('settings-applied', {
        detail: {
            mode: currentMode,
            metricId: currentMetricId,
            start,
            end
        }
    }));
}

// Экспортируем для window
window.toggleSettingsSheet = toggleSettingsSheet;
window.toggleMetricAccordion = toggleMetricAccordion;
window.setMode = setMode;
window.setMetric = setMetric;
window.setPeriodFromSheet = setPeriodFromSheet;
window.applySettings = applySettings;