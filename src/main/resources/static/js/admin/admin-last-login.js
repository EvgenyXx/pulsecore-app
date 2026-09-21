// js/admin/admin-last-login.js

import { AdminAPI } from './admin-api.js';

let currentPage = 0;
let totalPages = 0;
let isLoading = false;

const PAGE_SIZE = 20;

/**
 * Загрузить страницу последних входов.
 * @param {number} page — номер страницы (с 0)
 */
export async function loadLastLogin(page = 0) {
    if (isLoading) return;
    isLoading = true;

    const tbody = document.getElementById('lastLoginTableBody');
    const info = document.getElementById('lastLoginInfo');
    const pageLabel = document.getElementById('lastLoginPage');
    const prevBtn = document.getElementById('lastLoginPrev');
    const nextBtn = document.getElementById('lastLoginNext');

    if (!tbody) {
        isLoading = false;
        return;
    }

    tbody.innerHTML = '<tr><td colspan="2" class="text-center text-zinc-500 py-4">Загрузка...</td></tr>';
    if (info) info.textContent = 'Загрузка...';

    try {
        const data = await AdminAPI.getLastLogin(page, PAGE_SIZE);

        currentPage = data.number ?? 0;
        totalPages = data.totalPages ?? 0;

        if (!data.content || data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="2" class="text-center text-zinc-500 py-4">Нет данных</td></tr>';
            if (info) info.textContent = 'Всего: 0';
            if (pageLabel) pageLabel.textContent = '— / —';
            if (prevBtn) prevBtn.disabled = true;
            if (nextBtn) nextBtn.disabled = true;
            return;
        }

        tbody.innerHTML = data.content.map(item => `
            <tr class="border-b border-white/5">
                <td class="py-2 text-zinc-200">${escapeHtml(item.name || '—')}</td>
                <td class="py-2 text-right text-zinc-400">${formatLastLogin(item.lastLoginAt)}</td>
            </tr>
        `).join('');

        if (info) info.textContent = `Всего: ${data.totalElements ?? 0}`;

        if (pageLabel) {
            pageLabel.textContent = `${currentPage + 1} / ${Math.max(totalPages, 1)}`;
        }

        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1;

    } catch (e) {
        console.error('Ошибка загрузки последних входов:', e);
        tbody.innerHTML = '<tr><td colspan="2" class="text-center text-red-400 py-4">Ошибка загрузки</td></tr>';
        if (info) info.textContent = 'Ошибка';
        if (prevBtn) prevBtn.disabled = true;
        if (nextBtn) nextBtn.disabled = true;
    } finally {
        isLoading = false;
    }
}

export function loadLastLoginPrev() {
    if (currentPage > 0) {
        loadLastLogin(currentPage - 1);
    }
}

export function loadLastLoginNext() {
    if (currentPage < totalPages - 1) {
        loadLastLogin(currentPage + 1);
    }
}

/**
 * Форматирует дату последнего входа.
 */
function formatLastLogin(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return '—';

    const date = d.toLocaleDateString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
    const time = d.toLocaleTimeString('ru-RU', {
        hour: '2-digit',
        minute: '2-digit'
    });
    return `${date} ${time}`;
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}