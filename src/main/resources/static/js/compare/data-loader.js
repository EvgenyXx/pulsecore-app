// js/compare/data-loader.js

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