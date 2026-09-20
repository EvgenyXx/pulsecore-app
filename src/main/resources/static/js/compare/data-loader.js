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

/**
 * Поиск игроков для сравнения.
 * Ищет по всем, кто играл матчи (включая незарегистрированных).
 * С пагинацией.
 *
 * @param {string} query — поисковый запрос (минимум 2 символа)
 * @param {number} page — номер страницы (с 0)
 * @param {number} size — размер страницы
 * @returns {Promise<{content: string[], totalElements: number, totalPages: number, page: number, size: number, last: boolean, first: boolean}>}
 */
export async function searchPlayers(query, page = 0, size = 20) {
    const params = new URLSearchParams({
        q: query,
        page: String(page),
        size: String(size)
    });

    const res = await fetch(
        `/api/tournament/compare/players/search?${params}`,
        { credentials: 'same-origin' }
    );

    if (!res.ok) throw new Error('HTTP ' + res.status);
    return res.json();
}