// js/compare/settings-sheet.js

import { compareMetrics } from './metrics.js';
import { ComparePeriod } from './period.js';
import { initTooltips } from './tooltip.js';

let currentMode = 'versus';
let currentMetricId = 'salary'; // Дефолтная метрика — Зарплата
let currentPeriod = ComparePeriod.MONTH; // Дефолтный период — Месяц
let metricAccordionOpen = false;

export function initSettingsSheet() {
    renderMetricAccordion();
    initSegments();
}

function initSegments() {
    // Режим (modeRow) убран из HTML — слайдер только для периода
    const periodRow = document.getElementById('periodSegmentRow');

    if (periodRow && !periodRow.querySelector('.segment-slider')) {
        const slider = document.createElement('div');
        slider.className = 'segment-slider mode-4 pos-1'; // pos-1 это месяц
        periodRow.insertBefore(slider, periodRow.firstChild);
    }

    // Инициализируем визуальное состояние под текущий период (Месяц)
    setPeriod(currentPeriod);
}

function updatePeriodSlider() {
    const row = document.getElementById('periodSegmentRow');
    const slider = row?.querySelector('.segment-slider');
    if (slider) {
        const positions = {
            [ComparePeriod.WEEK]: 0,
            [ComparePeriod.MONTH]: 1,
            [ComparePeriod.YEAR]: 2,
            [ComparePeriod.ALL]: 3
        };
        slider.className = `segment-slider mode-4 pos-${positions[currentPeriod] ?? 1}`;
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
}

export function setMetric(metricId) {
    currentMetricId = metricId;
    renderMetricAccordion();
}

export function setPeriod(period) {
    currentPeriod = period;

    // Снимаем active со всех кнопок периода
    document.querySelectorAll('[onclick*="setPeriod"]').forEach(btn => {
        btn.classList.remove('active');
    });

    // Вешаем active на выбранную
    const activeBtn = document.querySelector(`[onclick="setPeriod('${period}')"]`);
    if (activeBtn) {
        activeBtn.classList.add('active');
    }

    updatePeriodSlider();
}

export function setPeriodFromSheet(period) {
    setPeriod(period);
}

export function applySettings() {
    const customStart = document.getElementById('customStart')?.value;
    const customEnd = document.getElementById('customEnd')?.value;

    toggleSettingsSheet();

    if (customStart && customEnd) {
        // Если введены кастомные даты — отправляем их, период игнорируем
        window.dispatchEvent(new CustomEvent('settings-applied', {
            detail: {
                mode: currentMode,
                metricId: currentMetricId,
                period: currentPeriod,
                start: customStart,
                end: customEnd
            }
        }));
        return;
    }

    // Иначе отправляем выбранный период (по дефолту MONTH)
    window.dispatchEvent(new CustomEvent('settings-applied', {
        detail: {
            mode: currentMode,
            metricId: currentMetricId,
            period: currentPeriod
        }
    }));
}

window.toggleSettingsSheet = toggleSettingsSheet;
window.toggleMetricAccordion = toggleMetricAccordion;
window.setMode = setMode;
window.setMetric = setMetric;
window.setPeriod = setPeriod;
window.setPeriodFromSheet = setPeriodFromSheet;
window.applySettings = applySettings;