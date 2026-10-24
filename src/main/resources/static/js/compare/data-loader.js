// js/compare/data-loader.js

/**
 * Поиск игроков для сравнения (старый эндпоинт).
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

/**
 * Поиск только зарегистрированных в системе игроков.
 * Возвращает тот же формат, что и searchPlayers — content: string[],
 * чтобы UI-код (player-search.js) не пришлось менять.
 *
 * Внутри ответ /api/player/search — Page<PlayerData>, берём из каждого
 * элемента поле name.
 *
 * @param {string} query — поисковый запрос (минимум 2 символа)
 * @param {number} page — номер страницы (с 0)
 * @param {number} size — размер страницы
 */
export async function searchRegisteredPlayers(query, page = 0, size = 20) {
    const params = new URLSearchParams({
        q: query,
        page: String(page),
        size: String(size)
    });

    const res = await fetch(
        `/api/player/search?${params}`,
        { credentials: 'same-origin' }
    );

    if (!res.ok) throw new Error('HTTP ' + res.status);

    const pageData = await res.json();

    // Page<PlayerData> → приводим content к string[] (именами),
    // остальные поля page сохраняем как есть.
    return {
        ...pageData,
        content: (pageData.content || []).map(p => p.name)
    };
}