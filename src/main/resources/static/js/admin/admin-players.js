// js/admin/admin-players.js

import { AdminAPI } from './admin-api.js';
import { capitalizeName } from '../core/utils.js';

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
    let subExpired = false;

    try {
        const sub = await AdminAPI.getPlayerSubscription(selectedPlayerId);
        playersCache[selectedPlayerId] = sub;
        if (sub && sub.activeNow) {
            subActive = true;
            const expiresDate = new Date(sub.expiresAt);
            const daysLeft = Math.ceil((expiresDate - new Date()) / (1000 * 60 * 60 * 24));
            const expiresStr = expiresDate.toLocaleString('ru-RU', {
                day: 'numeric', month: 'long', year: 'numeric',
                hour: '2-digit', minute: '2-digit'
            });
            badges += `<span class="badge badge-active">Подписка до ${expiresStr}</span>`;
            badges += `<span class="badge badge-admin">${daysLeft} дн.</span>`;
        } else {
            badges += '<span class="badge badge-inactive">Нет подписки</span>';
            if (sub && sub.expiresAt) {
                subExpired = new Date(sub.expiresAt) < new Date();
            }
        }
    } catch (e) {}

    if (section === 'sub') {
        document.getElementById('subSelBadges').innerHTML = badges;
        document.getElementById('removeSubBtn').classList.toggle('hidden', !subActive);
        return;
    }

    document.getElementById('playerSelBadges').innerHTML = badges;

    document.getElementById('playerGiveSub30').classList.toggle('hidden', false);
    document.getElementById('playerGiveSub60').classList.toggle('hidden', false);
    document.getElementById('playerRemoveSub').classList.toggle('hidden', !subActive);

    const showEnable = !subActive && !subExpired && playersCache[selectedPlayerId] && playersCache[selectedPlayerId].expiresAt;
    document.getElementById('playerEnableSub').classList.toggle('hidden', !showEnable);

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

    initPlayerCardSegments();
}

function initPlayerCardSegments() {
    const seg = document.getElementById('seg-player-card');
    if (!seg) return;

    const panels = {
        main: document.getElementById('pc-main'),
        sub: document.getElementById('pc-sub'),
        status: document.getElementById('pc-status'),
        service: document.getElementById('pc-service')
    };

    function show(key) {
        Object.entries(panels).forEach(([k, el]) => {
            if (el) el.style.display = (k === key) ? 'block' : 'none';
        });
    }

    seg.querySelectorAll('.segmented-btn').forEach(btn => {
        btn.onclick = () => {
            seg.querySelectorAll('.segmented-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            show(btn.dataset.pc);
        };
    });

    seg.querySelectorAll('.segmented-btn').forEach(b => b.classList.remove('active'));
    const first = seg.querySelector('[data-pc="main"]');
    if (first) first.classList.add('active');
    show('main');
}

function formatLastLogin(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleString('ru-RU', {
        day: 'numeric', month: 'long', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
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

export async function enableSub() {
    if (!selectedPlayerId) return;
    const msg = document.getElementById('playerMsg');
    msg.textContent = 'Включение...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.enableSubscription(selectedPlayerId);
        msg.textContent = 'Подписка включена';
        msg.className = 'text-xs text-center text-emerald-400';
        await refreshPlayerUI('players');
    } catch (e) {
        msg.textContent = 'Ошибка при включении';
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

    const fromEl = document.getElementById('resyncFrom');
    const toEl = document.getElementById('resyncTo');
    const from = fromEl?.value || null;
    const to = toEl?.value || null;
    const msg = document.getElementById('playerMsg');

    const label = from && to ? `${from} – ${to}` : 'весь период';
    msg.textContent = `Синхронизация ${label}...`;
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        const data = await AdminAPI.resyncPlayerTournaments(selectedPlayerId, from, to);
        msg.textContent = data.message || `Синхронизация запущена: ${label}`;
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

/* =====================================================
   ОБЗОР ПОДПИСОК — ПАГИНАЦИЯ КАК «ВХОДЫ»
   ===================================================== */

const SUBS_PAGE_SIZE = 20;
const subsState = {
    loaded: false,
    loading: false,
    items: [],
    page: 0
};

function fmtDate(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    if (isNaN(d)) return '—';
    return d.toLocaleString('ru-RU', {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

function subIsActive(item) {
    return !!(item.active ?? item.isActive ?? item.activeSubscription);
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

function subRowHtml(item, idx) {
    const name = subName(item);
    const email = subEmail(item);
    const expires = subExpires(item);
    const days = subDaysLeft(item);
    const daysClass = days === null ? '' : (days <= 7 ? 'subs-warn' : '');
    const active = subIsActive(item);

    return `
        <div class="subs-row ${active ? '' : 'subs-row-inactive'}">
            <span class="subs-idx">${idx + 1}</span>
            <div class="subs-main">
                <span class="subs-name">${name}</span>
                ${email ? `<span class="subs-email">${email}</span>` : ''}
            </div>
            <div class="subs-right-col">
                ${active
                    ? `<span class="subs-date">${fmtDate(expires)}</span>`
                    : `<span class="subs-date subs-muted">—</span>`}
                ${days !== null
                    ? `<span class="subs-days ${daysClass}">${days} дн.</span>`
                    : ''}
            </div>
        </div>
    `;
}

function renderSubsEmpty(text, isError = false) {
    const list = document.getElementById('subsList');
    if (!list) return;
    list.innerHTML = `<div class="subs-row subs-empty ${isError ? 'subs-warn' : ''}">${text}</div>`;
}

function renderSubsPage() {
    const list = document.getElementById('subsList');
    const info = document.getElementById('subsOverviewInfo');
    const pageLabel = document.getElementById('subsPage');
    const prevBtn = document.getElementById('subsPrev');
    const nextBtn = document.getElementById('subsNext');

    const items = subsState.items;
    const totalPages = Math.max(1, Math.ceil(items.length / SUBS_PAGE_SIZE));
    const page = Math.min(subsState.page, totalPages - 1);
    subsState.page = page;

    const start = page * SUBS_PAGE_SIZE;
    const pageItems = items.slice(start, start + SUBS_PAGE_SIZE);

    if (pageItems.length === 0) {
        renderSubsEmpty('Подписок нет');
    } else {
        list.innerHTML = pageItems.map((it, i) => subRowHtml(it, start + i)).join('');
    }

    if (info) {
        const activeCount = items.filter(subIsActive).length;
        info.textContent = `Всего: ${items.length} · Активных: ${activeCount}`;
    }
    if (pageLabel) pageLabel.textContent = `${page + 1} / ${totalPages}`;
    if (prevBtn) prevBtn.disabled = page <= 0;
    if (nextBtn) nextBtn.disabled = page >= totalPages - 1;
}

export async function loadSubscriptionsOverview(force = false) {
    const list = document.getElementById('subsList');
    if (!list) return;

    if (subsState.loaded && !force) {
        renderSubsPage();
        return;
    }
    if (subsState.loading) return;

    subsState.loading = true;
    renderSubsEmpty('Загрузка...');

    try {
        const data = await AdminAPI.getAllSubscriptions();
        const raw = Array.isArray(data) ? data : (data?.content || []);

        const items = raw.slice().sort((a, b) => {
            const aAct = subIsActive(a) ? 1 : 0;
            const bAct = subIsActive(b) ? 1 : 0;
            if (aAct !== bAct) return bAct - aAct;
            const aExp = subExpires(a) ? new Date(subExpires(a)).getTime() : 0;
            const bExp = subExpires(b) ? new Date(subExpires(b)).getTime() : 0;
            return bExp - aExp;
        });

        subsState.items = items;
        subsState.loaded = true;
        subsState.page = 0;

        renderSubsPage();
    } catch (e) {
        renderSubsEmpty(`Ошибка: ${e.message || 'не удалось загрузить'}`, true);
        const info = document.getElementById('subsOverviewInfo');
        if (info) info.textContent = 'Ошибка загрузки';
    } finally {
        subsState.loading = false;
    }
}

export function subsPrevPage() {
    if (subsState.page > 0) {
        subsState.page -= 1;
        renderSubsPage();
    }
}

export function subsNextPage() {
    const totalPages = Math.max(1, Math.ceil(subsState.items.length / SUBS_PAGE_SIZE));
    if (subsState.page < totalPages - 1) {
        subsState.page += 1;
        renderSubsPage();
    }
}

export function refreshSubscriptionsOverview() {
    return loadSubscriptionsOverview(true);
}