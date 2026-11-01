// js/admin/admin-last-login.js

import { AdminAPI } from './admin-api.js';

let currentPage = 0;
let totalPages = 0;
let isLoading = false;

const PAGE_SIZE = 20;

export async function loadLastLogin(page = 0) {
    if (isLoading) return;
    isLoading = true;

    const list = document.getElementById('lastLoginList');
    const info = document.getElementById('lastLoginInfo');
    const pageLabel = document.getElementById('lastLoginPage');
    const prevBtn = document.getElementById('lastLoginPrev');
    const nextBtn = document.getElementById('lastLoginNext');

    if (!list) {
        isLoading = false;
        return;
    }

    list.innerHTML = '<div class="last-login-row last-login-empty">Загрузка...</div>';
    if (info) info.textContent = 'Загрузка...';

    try {
        const data = await AdminAPI.getLastLogin(page, PAGE_SIZE);

        currentPage = data.number ?? 0;
        totalPages = data.totalPages ?? 0;
        const items = data.content || [];
        const total = data.totalElements ?? 0;

        if (items.length === 0) {
            list.innerHTML = '<div class="last-login-row last-login-empty">Нет данных</div>';
            if (info) info.textContent = 'Всего: 0';
            if (pageLabel) pageLabel.textContent = '— / —';
            if (prevBtn) prevBtn.disabled = true;
            if (nextBtn) nextBtn.disabled = true;
            return;
        }

        const startIdx = currentPage * PAGE_SIZE;

        list.innerHTML = items.map((item, i) => {
            const name = escapeHtml(item.name || '—');
            const email = item.email ? escapeHtml(item.email) : '';
            const last = formatLastLogin(item.lastLoginAt);
            const num = startIdx + i + 1;
            return `
                <div class="last-login-row">
                    <span class="last-login-idx">${num}</span>
                    <div class="last-login-main">
                        <span class="last-login-name">${name}</span>
                        ${email ? `<span class="last-login-email">${email}</span>` : ''}
                    </div>
                    <span class="last-login-date">${last}</span>
                </div>
            `;
        }).join('');

        if (info) info.textContent = `Всего: ${total}`;
        if (pageLabel) pageLabel.textContent = `${currentPage + 1} / ${Math.max(totalPages, 1)}`;
        if (prevBtn) prevBtn.disabled = currentPage <= 0;
        if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1;

    } catch (e) {
        console.error('Ошибка загрузки последних входов:', e);
        list.innerHTML = '<div class="last-login-row last-login-empty last-login-error">Ошибка загрузки</div>';
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