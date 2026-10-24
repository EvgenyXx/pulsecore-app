// js/compare/period.js

/**
 * Периоды сравнения.
 * Значения в UPPER CASE, чтобы Spring мог смаппить их в enum ComparePeriod на бэке.
 */
export const ComparePeriod = Object.freeze({
    WEEK: 'WEEK',
    MONTH: 'MONTH',
    YEAR: 'YEAR',
    ALL: 'ALL',
});

/**
 * Возвращает { start, end, label } для отображения на фронте.
 * ВАЖНО: для 'ALL' возвращает start=null, end=null (это только для UI).
 * Для запросов к бэку используй buildPeriodParams().
 */
export function getPeriodDates(period) {
    const today = new Date();
    let start = null;
    let end = today.toISOString().split('T')[0];
    let label = 'Всё время';

    switch (period) {
        case ComparePeriod.WEEK: {
            const day = today.getDay() || 7;
            const monday = new Date(today);
            monday.setDate(today.getDate() - day + 1);
            start = monday.toISOString().split('T')[0];
            label = 'Неделя';
            break;
        }
        case ComparePeriod.MONTH: {
            start = new Date(today.getFullYear(), today.getMonth(), 1)
                .toISOString()
                .split('T')[0];
            label = 'Месяц';
            break;
        }
        case ComparePeriod.YEAR: {
            start = new Date(today.getFullYear(), 0, 1)
                .toISOString()
                .split('T')[0];
            label = 'Год';
            break;
        }
        case ComparePeriod.ALL: {
            start = null;
            end = null;
            label = 'Всё время';
            break;
        }
        default: {
            // дефолт — месяц
            start = new Date(today.getFullYear(), today.getMonth(), 1)
                .toISOString()
                .split('T')[0];
            label = 'Месяц';
            break;
        }
    }

    return { start, end, label };
}

/**
 * Собирает объект параметров для запроса к бэку.
 *
 * Приоритет:
 * 1. Если переданы customStart + customEnd — шлём их как start/end, period не шлём.
 * 2. Иначе — шлём period (в UPPER CASE, дефолт MONTH). start/end не шлём.
 *
 * @param {string|null} customStart
 * @param {string|null} customEnd
 * @param {string} period
 * @returns {{ start?: string, end?: string, period?: string }}
 */
export function buildPeriodParams(customStart, customEnd, period) {
    if (customStart && customEnd) {
        return { start: customStart, end: customEnd };
    }

    const actualPeriod = period || ComparePeriod.MONTH;
    return { period: actualPeriod };
}

/**
 * Собирает query-string из объекта (пропускает null/undefined/пустые строки).
 */
export function buildQueryString(params) {
    const searchParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
            searchParams.append(key, value);
        }
    });
    const query = searchParams.toString();
    return query ? '?' + query : '';
}