// js/compare/data-loader.js

import { compareMetrics } from './metrics.js';
import { buildQueryString } from './period.js';

export async function loadDataForMetric(metric, start, end) {
    const query = buildQueryString({ start, end });
    const response = await fetch(metric.endpoint + query, { credentials: 'same-origin' });
    if (!response.ok) throw new Error('HTTP ' + response.status);
    return response.json();
}

export async function loadPlayers(start, end) {
    // Загружаем игроков (всегда с money endpoint)
    const moneyMetric = compareMetrics[0];
    return loadDataForMetric(moneyMetric, start, end);
}

export async function loadStatsPlayers(start, end) {
    // Загружаем статистику
    const statsMetric = compareMetrics[1];
    return loadDataForMetric(statsMetric, start, end);
}