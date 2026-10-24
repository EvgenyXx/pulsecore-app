// js/compare/settings-sheet.js

import { compareMetrics } from './metrics.js';
import { ComparePeriod } from './period.js';
import { initTooltips } from './tooltip.js';

let currentMode = 'versus';
let currentMetricId = 'salary'; // Дефолтная метрика — Зарплата
let currentPeriod = 'month'; // Дефолтный период — Месяц (храним строкой)
let metricAccordionOpen = false;

export function initSettingsSheet() {
    renderMetricAccordion();
    initSegments();
}

function initSegments() {
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
        // Мапа соответствия строки к позиции бегунка (0 - неделя, 1 - месяц, 2 - год, 3 - всё время)
        const positions = {
            'week': 0,
            'month': 1,
            'year': 2,
            'all': 3
        };
        const pos = positions[currentPeriod] ?? 1;
        slider.className = `segment-slider mode-4 pos-${pos}`;
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

// Принимаем строку ('week', 'month', 'year', 'all')
export function setPeriod(period) {
    // Принудительно делаем нижний регистр, чтобы 'YEAR' не сломал поиск
    currentPeriod = String(period).toLowerCase();

    // Снимаем active со всех кнопок периода
    document.querySelectorAll('[onclick*="setPeriod"]').forEach(btn => {
        btn.classList.remove('active');
    });

    // Вешаем active на выбранную
    const activeBtn = document.querySelector(`[onclick="setPeriod('${currentPeriod}')"]`);
    if (activeBtn) {
        activeBtn.classList.add('active');
    }

    // Двигаем ползунок
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
                period: currentPeriod, // 'week', 'month' и т.д.
                start: customStart,
                end: customEnd
            }
        }));
        return;
    }

    // Иначе отправляем выбранный период
    window.dispatchEvent(new CustomEvent('settings-applied', {
        detail: {
            mode: currentMode,
            metricId: currentMetricId,
            period: currentPeriod // Отправляется 'week', 'month' и т.д.
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