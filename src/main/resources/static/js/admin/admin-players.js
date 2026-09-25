// js/admin/admin-players.js

import { AdminAPI } from './admin-api.js';
import { formatMoney, capitalizeName } from '../core/utils.js';

let selectedPlayerId = null;
let playersCache = {};
let selectedPlayerData = null;

export async function searchPlayers(section) {
    const inputId = section === 'sub' ? 'subSearchInput' : 'playerSearchInput';
    const resultsId = section === 'sub' ? 'subSearchResults' : 'playerSearchResults';
    const q = document.getElementById(inputId).value.trim();
    const results = document.getElementById(resultsId);

    if (q.length < 2) {
        results.classList.add('hidden');
        return;
    }

    try {
        const players = await AdminAPI.searchPlayers(q);
        results.classList.remove('hidden');
        results.innerHTML = players.map(p => `
            <div class="player-card flex items-center justify-between" onclick="selectPlayer('${p.id}','${capitalizeName(p.name).replace(/'/g, "\\'")}','${p.email.replace(/'/g, "\\'")}','${section}')">
                <div>
                    <p class="text-white text-sm font-semibold">${capitalizeName(p.name)}</p>
                    <p class="text-xs text-zinc-500 mt-0.5">${p.email}</p>
                </div>
                <span class="text-indigo-400 text-sm flex-shrink-0 ml-2">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
                </span>
            </div>
        `).join('');
    } catch (e) {
        results.innerHTML = '<p class="text-red-400 text-sm text-center py-3">Ошибка загрузки</p>';
    }
}

export async function selectPlayer(id, name, email, section) {
    selectedPlayerId = id;
    selectedPlayerData = { id, name, email };

    if (section === 'sub') {
        document.getElementById('subSelName').textContent = name;
        document.getElementById('subSelEmail').textContent = email;
        document.getElementById('subSelectedPlayer').classList.remove('hidden');
        document.getElementById('subSearchResults').classList.add('hidden');
        document.getElementById('subMsg').classList.add('hidden');
        await refreshPlayerUI('sub');
    } else {
        document.getElementById('playerSelName').textContent = name;
        document.getElementById('playerSelEmail').textContent = email;
        document.getElementById('playerSelected').classList.remove('hidden');
        document.getElementById('playerSearchResults').classList.add('hidden');
        document.getElementById('playerMsg').classList.add('hidden');
        await refreshPlayerUI('players');
    }
}

async function refreshPlayerUI(section) {
    if (!selectedPlayerId) return;

    let badges = '';
    let subActive = false;

    try {
        const sub = await AdminAPI.getPlayerSubscription(selectedPlayerId);
        playersCache[selectedPlayerId] = sub;
        if (sub && sub.active) {
            subActive = true;
            const expiresDate = new Date(sub.expiresAt);
            const daysLeft = Math.ceil((expiresDate - new Date()) / (1000 * 60 * 60 * 24));
            badges += `<span class="badge badge-active">Подписка до ${expiresDate.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })}</span>`;
            badges += `<span class="badge badge-admin">${daysLeft} дн.</span>`;
        } else {
            badges += '<span class="badge badge-inactive">Нет подписки</span>';
        }
    } catch (e) {}

    if (section === 'sub') {
        document.getElementById('subSelBadges').innerHTML = badges;
        document.getElementById('removeSubBtn').classList.toggle('hidden', !subActive);
    } else {
        document.getElementById('playerSelBadges').innerHTML = badges;

        document.getElementById('playerGiveSub30').classList.toggle('hidden', false);
        document.getElementById('playerGiveSub60').classList.toggle('hidden', false);
        document.getElementById('playerRemoveSub').classList.toggle('hidden', !subActive);

        try {
            const roles = await AdminAPI.getPlayerRoles(selectedPlayerId);
            if (roles.includes('ROLE_ADMIN')) {
                badges += ' <span class="badge badge-admin">Админ</span>';
            }
            document.getElementById('playerSelBadges').innerHTML = badges;

            const toggleBtn = document.getElementById('toggleAdminBtn');
            if (toggleBtn) {
                toggleBtn.classList.remove('hidden');
                if (roles.includes('ROLE_ADMIN')) {
                    toggleBtn.textContent = 'Разжаловать';
                    toggleBtn.className = 'btn btn-danger';
                } else {
                    toggleBtn.textContent = 'Сделать админом';
                    toggleBtn.className = 'btn btn-amber';
                }
            }
        } catch (e) {}

        try {
            const players = await AdminAPI.searchPlayers(selectedPlayerData?.name || '');
            const playerData = players.find(p => p.id === selectedPlayerId);
            if (playerData) {
                selectedPlayerData = playerData;
                document.getElementById('playerName').value = playerData.name || '';
                document.getElementById('playerEmail').value = playerData.email || '';
                document.getElementById('playerLeague').value = playerData.primaryLeague || '';
                document.getElementById('playerHalls').value = playerData.selectedHalls || '';
                document.getElementById('playerLiveHalls').value = playerData.liveSelectedHalls || '';
                setStatusCheckbox('playerPush', playerData.pushEnabled);
                setStatusCheckbox('playerNotifications', playerData.notificationsEnabled);
                document.getElementById('playerLastLogin').textContent = formatLastLogin(playerData.lastLoginAt);
            }
        } catch (e) {}
    }
}

function formatLastLogin(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' }) + ' ' +
        d.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' });
}

function setStatusCheckbox(prefix, value) {
    const checkbox = document.getElementById(prefix + 'Checkbox');
    const label = document.getElementById(prefix + 'Label');
    if (checkbox) {
        checkbox.classList.toggle('checked', value);
        checkbox.textContent = value ? '✓' : '';
    }
    if (label) {
        label.textContent = value ? 'Вкл' : 'Выкл';
        label.classList.toggle('active', value);
    }
}

export function togglePlayerStatus(prefix) {
    const checkbox = document.getElementById(prefix + 'Checkbox');
    const label = document.getElementById(prefix + 'Label');
    if (!checkbox) return;

    const isChecked = checkbox.classList.contains('checked');
    if (isChecked) {
        checkbox.classList.remove('checked');
        checkbox.textContent = '';
        label.textContent = 'Выкл';
        label.classList.remove('active');
    } else {
        checkbox.classList.add('checked');
        checkbox.textContent = '✓';
        label.textContent = 'Вкл';
        label.classList.add('active');
    }
}

function isStatusChecked(prefix) {
    const checkbox = document.getElementById(prefix + 'Checkbox');
    return checkbox ? checkbox.classList.contains('checked') : false;
}

// ===== ПОДПИСКА В КАРТОЧКЕ ИГРОКА =====

export async function giveSub(days) {
    if (!selectedPlayerId) return;
    const msg = document.getElementById('playerMsg');
    msg.textContent = 'Выдача...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.giveSubscription(selectedPlayerId, days);
        msg.textContent = `Подписка выдана на ${days} дней`;
        msg.className = 'text-xs text-center text-emerald-400';
        await refreshPlayerUI('players');
    } catch (e) {
        msg.textContent = 'Ошибка при выдаче';
        msg.className = 'text-xs text-center text-red-400';
    }
}

export async function giveSubCustom() {
    if (!selectedPlayerId) return;
    const days = parseInt(document.getElementById('playerCustomDays').value);
    const msg = document.getElementById('playerMsg');

    if (!days || days < 1 || days > 3650) {
        msg.textContent = 'Введите число от 1 до 3650';
        msg.className = 'text-xs text-center text-red-400';
        msg.classList.remove('hidden');
        return;
    }

    msg.textContent = 'Выдача...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.giveSubscription(selectedPlayerId, days);
        msg.textContent = `Подписка выдана на ${days} дней`;
        msg.className = 'text-xs text-center text-emerald-400';
        await refreshPlayerUI('players');
    } catch (e) {
        msg.textContent = 'Ошибка при выдаче';
        msg.className = 'text-xs text-center text-red-400';
    }
}

export async function removeSub() {
    if (!selectedPlayerId) return;
    const msg = document.getElementById('playerMsg');
    msg.textContent = 'Отключение...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.removeSubscription(selectedPlayerId);
        msg.textContent = 'Подписка отключена';
        msg.className = 'text-xs text-center text-emerald-400';
        await refreshPlayerUI('players');
    } catch (e) {
        msg.textContent = 'Ошибка при отключении';
        msg.className = 'text-xs text-center text-red-400';
    }
}

export async function updatePlayer() {
    if (!selectedPlayerId) return;

    const msg = document.getElementById('playerMsg');
    msg.textContent = 'Сохранение...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.updatePlayer(selectedPlayerId, {
            name: document.getElementById('playerName').value || null,
            email: document.getElementById('playerEmail').value || null,
            primaryLeague: document.getElementById('playerLeague').value || null,
            selectedHalls: document.getElementById('playerHalls').value || null,
            liveSelectedHalls: document.getElementById('playerLiveHalls').value || null,
            pushEnabled: isStatusChecked('playerPush'),
            notificationsEnabled: isStatusChecked('playerNotifications')
        });
        msg.textContent = 'Сохранено';
        msg.className = 'text-xs text-center text-emerald-400';

        document.getElementById('playerSelName').textContent = capitalizeName(document.getElementById('playerName').value);
        document.getElementById('playerSelEmail').textContent = document.getElementById('playerEmail').value;
    } catch (e) {
        msg.textContent = 'Ошибка сохранения';
        msg.className = 'text-xs text-center text-red-400';
    }
}

export async function togglePlayerRole(roleName) {
    if (!selectedPlayerId) return;
    const msg = document.getElementById('playerMsg');
    const toggleBtn = document.getElementById('toggleAdminBtn');
    const isGrant = toggleBtn.classList.contains('btn-amber');

    msg.textContent = isGrant ? 'Выдача роли...' : 'Отзыв роли...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.togglePlayerRole(selectedPlayerId, roleName, isGrant);
        msg.textContent = isGrant ? 'Роль выдана' : 'Роль отозвана';
        msg.className = 'text-xs text-center text-emerald-400';
        await refreshPlayerUI('players');
    } catch (e) {
        msg.textContent = 'Ошибка';
        msg.className = 'text-xs text-center text-red-400';
    }
}

export async function deletePlayerTournaments() {
    if (!selectedPlayerId) return;
    const msg = document.getElementById('playerMsg');
    msg.textContent = 'Удаление...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        const data = await AdminAPI.deletePlayerTournaments(selectedPlayerId);
        msg.textContent = data.message;
        msg.className = 'text-xs text-center text-emerald-400';
    } catch (e) {
        msg.textContent = 'Ошибка';
        msg.className = 'text-xs text-center text-red-400';
    }
}

export async function resyncPlayerTournaments() {
    if (!selectedPlayerId) return;

    const today = new Date();
    const yearAgo = new Date();
    yearAgo.setFullYear(today.getFullYear() - 1);

    const defaultFrom = yearAgo.toISOString().split('T')[0];
    const defaultTo = today.toISOString().split('T')[0];

    showResyncModal(defaultFrom, defaultTo);
}

function showResyncModal(defaultFrom, defaultTo) {
    document.getElementById('resyncModal')?.remove();

    const modal = document.createElement('div');
    modal.id = 'resyncModal';
    modal.className = 'modal-overlay';
    modal.style.cssText = 'position:fixed;inset:0;background:rgba(0,0,0,0.75);z-index:10000;display:flex;align-items:center;justify-content:center;backdrop-filter:blur(4px);';

    modal.innerHTML = `
        <div style="background:#12121f;border-radius:14px;padding:20px;max-width:400px;width:90%;border:1px solid rgba(255,255,255,0.08);">
            <h3 class="text-white font-semibold mb-1">Пересинхронизация турниров</h3>
            <p class="text-xs text-zinc-400 mb-4">Укажите период загрузки</p>

            <div class="grid grid-cols-2 gap-3 mb-4">
                <div>
                    <label class="text-xs text-zinc-400 mb-1 block">С даты</label>
                    <input id="resyncFrom" type="date" value="${defaultFrom}" class="w-full bg-white/5 border border-white/10 rounded-lg p-3 text-white text-sm focus:border-indigo-500 focus:outline-none">
                </div>
                <div>
                    <label class="text-xs text-zinc-400 mb-1 block">По дату</label>
                    <input id="resyncTo" type="date" value="${defaultTo}" class="w-full bg-white/5 border border-white/10 rounded-lg p-3 text-white text-sm focus:border-indigo-500 focus:outline-none">
                </div>
            </div>

            <div class="flex gap-2">
                <button id="resyncConfirm" class="btn-gold flex-1 py-2 rounded-lg text-sm">Запустить</button>
                <button id="resyncCancel" class="flex-1 bg-zinc-700 hover:bg-zinc-600 text-white py-2 rounded-lg text-sm">Отмена</button>
            </div>
            <p id="resyncError" class="text-red-400 text-xs mt-2 hidden"></p>
        </div>
    `;

    document.body.appendChild(modal);

    document.getElementById('resyncCancel').onclick = () => modal.remove();
    modal.onclick = (e) => { if (e.target === modal) modal.remove(); };

    document.getElementById('resyncConfirm').onclick = async () => {
        const from = document.getElementById('resyncFrom').value;
        const to = document.getElementById('resyncTo').value;
        const err = document.getElementById('resyncError');

        err.classList.add('hidden');

        if (!from || !to) {
            err.textContent = 'Заполните обе даты';
            err.classList.remove('hidden');
            return;
        }

        if (from > to) {
            err.textContent = 'Дата "с" позже даты "по"';
            err.classList.remove('hidden');
            return;
        }

        modal.remove();
        await executeResync(from, to);
    };
}

async function executeResync(from, to) {
    const msg = document.getElementById('playerMsg');
    msg.textContent = `Синхронизация ${from} – ${to}...`;
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        const data = await AdminAPI.resyncPlayerTournaments(selectedPlayerId, from, to);
        msg.textContent = data.message || `Синхронизация запущена: ${from} – ${to}`;
        msg.className = 'text-xs text-center text-emerald-400';
    } catch (e) {
        msg.textContent = e.message || 'Ошибка синхронизации';
        msg.className = 'text-xs text-center text-red-400';
    }
}

export async function deletePlayerAccount() {
    if (!selectedPlayerId) return;
    if (!confirm('Удалить аккаунт навсегда?')) return;

    const msg = document.getElementById('playerMsg');
    msg.textContent = 'Удаление...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.deletePlayerAccount(selectedPlayerId);
        msg.textContent = 'Аккаунт удалён';
        msg.className = 'text-xs text-center text-emerald-400';
        document.getElementById('playerSelected').classList.add('hidden');
    } catch (e) {
        msg.textContent = 'Ошибка удаления';
        msg.className = 'text-xs text-center text-red-400';
    }
}

// ===== ОБЗОР ПОДПИСОК ВСЕХ ИГРОКОВ =====

const subState = { loaded: false, loading: false, items: [] };

function fmtDate(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    if (isNaN(d)) return '—';
    return d.toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

function subIsActive(item) {
    return !!(item.active ?? item.isActive ?? item.hasActiveSubscription);
}

function subName(item) {
    return capitalizeName(item.name || item.playerName || item.player_name || '—');
}

function subEmail(item) {
    return item.email || item.playerEmail || '';
}

function subExpires(item) {
    return item.expiresAt || item.expires_at || item.subscriptionEnd || null;
}

function subDaysLeft(item) {
    if (typeof item.daysLeft === 'number') return item.daysLeft;
    const expires = subExpires(item);
    if (!expires) return null;
    return Math.max(0, Math.ceil((new Date(expires) - new Date()) / 86400000));
}

function subStatusBadge(item) {
    return subIsActive(item)
        ? '<span class="badge badge-active">Активна</span>'
        : '<span class="badge badge-inactive">Нет</span>';
}

function subRowHtml(item, idx) {
    const name = subName(item);
    const email = subEmail(item);
    const expires = subExpires(item);
    const days = subDaysLeft(item);
    const daysClass = days === null ? 'subs-muted' : (days <= 7 ? 'subs-warn' : '');

    return `
        <tr>
            <td class="subs-idx">${idx + 1}</td>
            <td>
                <div class="subs-name">${name}</div>
                ${email ? `<div class="subs-email">${email}</div>` : ''}
            </td>
            <td>${subStatusBadge(item)}</td>
            <td class="subs-right">${fmtDate(expires)}</td>
            <td class="subs-right ${daysClass}">${days === null ? '—' : days + ' дн.'}</td>
        </tr>
    `;
}

function subCardHtml(item) {
    const name = subName(item);
    const email = subEmail(item);
    const expires = subExpires(item);
    const days = subDaysLeft(item);
    const daysClass = days === null ? 'subs-muted' : (days <= 7 ? 'subs-warn' : '');
    const badge = subStatusBadge(item);

    return `
        <div class="subs-card">
            <div class="subs-card-top">
                <div style="min-width:0;flex:1;">
                    <div class="subs-card-name">${name}</div>
                    ${email ? `<div class="subs-card-email">${email}</div>` : ''}
                </div>
                <div style="flex-shrink:0;">${badge}</div>
            </div>
            <div class="subs-card-bottom">
                <span class="subs-card-date">До ${fmtDate(expires)}</span>
                <span class="subs-card-days ${daysClass}">${days === null ? '—' : days + ' дн.'}</span>
            </div>
        </div>
    `;
}

function renderSubsEmpty(text, isError = false) {
    const cls = isError ? 'subs-empty subs-warn' : 'subs-empty';
    const cardCls = isError ? 'subs-card subs-card-empty subs-warn' : 'subs-card subs-card-empty';

    const tableEl = document.getElementById('subsOverviewBody');
    if (tableEl) tableEl.innerHTML = `<tr><td colspan="5" class="${cls}">${text}</td></tr>`;

    const cardsEl = document.getElementById('subsOverviewCards');
    if (cardsEl) cardsEl.innerHTML = `<div class="${cardCls}">${text}</div>`;
}

export async function loadSubscriptionsOverview(force = false) {
    const tableEl = document.getElementById('subsOverviewBody');
    const cardsEl = document.getElementById('subsOverviewCards');
    const info = document.getElementById('subsOverviewInfo');
    if (!tableEl && !cardsEl) return;

    if (subState.loaded && !force) return;
    if (subState.loading) return;

    subState.loading = true;
    renderSubsEmpty('Загрузка...');
    if (info) info.textContent = 'Загрузка...';

    try {
        const data = await AdminAPI.getAllSubscriptions();
        const list = Array.isArray(data) ? data : (data?.content || []);
        subState.items = list;
        subState.loaded = true;

        if (list.length === 0) {
            renderSubsEmpty('Подписок нет');
        } else {
            if (tableEl) tableEl.innerHTML = list.map(subRowHtml).join('');
            if (cardsEl) cardsEl.innerHTML = list.map(subCardHtml).join('');
        }

        if (info) {
            const activeCount = list.filter(subIsActive).length;
            info.textContent = `Всего: ${list.length} · Активных: ${activeCount}`;
        }
    } catch (e) {
        renderSubsEmpty(`Ошибка: ${e.message || 'не удалось загрузить'}`, true);
        if (info) info.textContent = 'Ошибка загрузки';
    } finally {
        subState.loading = false;
    }
}

export function refreshSubscriptionsOverview() {
    return loadSubscriptionsOverview(true);
}