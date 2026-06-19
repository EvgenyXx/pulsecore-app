import { AdminAPI } from '../core/admin-api.js';

export async function loadPageStats() {
    const days = document.getElementById('statsDays').value;
    const tbody = document.getElementById('statsTableBody');
    tbody.innerHTML = '<tr><td colspan="3" class="text-center text-zinc-500 py-4"><span class="spinner-sm"></span> Загрузка...</td></tr>';

    try {
        const data = await AdminAPI.getPageStats(days);
        const total = data.reduce((sum, item) => sum + item.count, 0);

        if (data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center text-zinc-500 py-4">Нет данных</td></tr>';
        } else {
            tbody.innerHTML = data.map(item => {
                const percent = total > 0 ? ((item.count / total) * 100).toFixed(1) : 0;
                return `<tr>
                    <td class="text-white text-sm py-1.5">${item.path}</td>
                    <td class="text-white font-semibold text-right py-1.5">${item.count}</td>
                    <td class="text-zinc-400 text-right py-1.5">${percent}%</td>
                </tr>`;
            }).join('');
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="3" class="text-center text-red-400 py-4">Ошибка загрузки</td></tr>';
    }

    loadPlayerStats(days);
}

async function loadPlayerStats(days) {
    const container = document.getElementById('playerStatsContainer');
    if (!container) return;
    container.innerHTML = '<p class="text-center text-zinc-500 py-4"><span class="spinner-sm"></span> Загрузка...</p>';

    try {
        const data = await AdminAPI.getPlayerStats(days);
        if (!data || data.length === 0) {
            container.innerHTML = '<p class="text-center text-zinc-500 py-4">Нет данных</p>';
            return;
        }

        const total = data.reduce((sum, item) => sum + item.totalCount, 0);

        let html = '<div class="overflow-x-auto"><table class="w-full text-xs"><thead><tr class="text-zinc-500"><th class="text-left py-2">Игрок</th><th class="text-left py-2">Куда заходил</th><th class="text-right py-2">Всего</th><th class="text-right py-2">%</th></tr></thead><tbody>';

        data.forEach(item => {
            const percent = total > 0 ? ((item.totalCount / total) * 100).toFixed(1) : 0;
            const pathsHtml = item.paths.map(p =>
                `<div class="flex justify-between gap-4 py-0.5">
                    <span class="text-indigo-400/80">${p.path}</span>
                    <span class="text-zinc-500 font-mono">×${p.count}</span>
                </div>`
            ).join('');

            html += `<tr>
                <td class="text-white text-sm font-semibold align-top pt-2">${item.name}</td>
                <td class="align-top pt-2">${pathsHtml}</td>
                <td class="text-white font-semibold text-right align-top pt-2">${item.totalCount}</td>
                <td class="text-zinc-400 text-right align-top pt-2">${percent}%</td>
            </tr>`;
        });

        html += '</tbody></table></div>';
        container.innerHTML = html;
    } catch (e) {
        container.innerHTML = '<p class="text-center text-red-400 py-4">Ошибка загрузки</p>';
    }
}