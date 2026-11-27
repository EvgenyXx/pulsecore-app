// js/admin/admin-players.js

import { AdminAPI } from './admin-api.js';
import { capitalizeName } from '../core/utils.js';

let selectedPlayerId = null;
let playersCache = {};
let selectedPlayerData = null;

// Активная вкладка карточки игрока (сохраняется между обновлениями данных)
let activePcTab = 'main';
// Таймер автоскрытия сообщений
let playerMsgTimer = null;
// Защита от двойных нажатий
let actionBusy = false;
// Защита от устаревших ответов поиска при быстром вводе
const searchRequestIds = { sub: 0, players: 0 };

/* =====================================================
   ВСПОМОГАТЕЛЬНЫЕ
   ===================================================== */

function escapeHtml(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function getInitials(name) {
    const parts = String(name || '').trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) return '?';
    const first = parts[0][0] || '';
    const second = parts.length > 1 ? (parts[1][0] || '') : '';
    return (first + second).toUpperCase();
}

function updateAvatar(name) {
    const avatar = document.querySelector('#playerSelected .avatar-circle');
    if (avatar) avatar.textContent = getInitials(name);
}

function setValue(id, value) {
    const el = document.getElementById(id);
    if (el) el.value = value ?? '';
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value ?? '';
}

async function runAction(fn) {
    if (actionBusy) return;
    actionBusy = true;
    const card = document.getElementById('playerSelected');
    if (card) card.classList.add('is-busy');
    try {
        await fn();
    } finally {
        actionBusy = false;
        if (card) card.classList.remove('is-busy');
    }
}

/* =====================================================
   ПОИСК
   ===================================================== */

export async function searchPlayers(section) {
    const inputId = section === 'sub' ? 'subSearchInput' : 'playerSearchInput';
    const resultsId = section === 'sub' ? 'subSearchResults' : 'playerSearchResults';
    const q = document.getElementById(inputId).value.trim();
    const results = document.getElementById(resultsId);
    const key = section === 'sub' ? 'sub' : 'players';

    if (q.length < 2) {
        results.classList.add('hidden');
        return;
    }

    const requestId = ++searchRequestIds[key];

    try {
        const players = await AdminAPI.searchPlayers(q);
        if (requestId !== searchRequestIds[key]) return;

        results.classList.remove('hidden');

        if (!players || players.length === 0) {
            results.innerHTML = '<p class="text-zinc-500 text-sm text-center py-3">Никого не найдено</p>';
            return;
        }

        results.innerHTML = players.map(p => {
            const name = capitalizeName(p.name);
            const email = p.email || '';
            return `
            <div class="player-card flex items-center justify-between" data-id="${escapeHtml(p.id)}" data-name="${escapeHtml(name)}" data-email="${escapeHtml(email)}">
                <div class="min-w-0">
                    <p class="text-white text-sm font-semibold truncate">${escapeHtml(name)}</p>
                    <p class="text-xs text-zinc-500 mt-0.5 truncate">${escapeHtml(email)}</p>
                </div>
                <span class="text-indigo-400 text-sm flex-shrink-0 ml-2">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
                </span>
            </div>
        `;
        }).join('');

        results.onclick = (e) => {
            const card = e.target.closest('.player-card[data-id]');
            if (!card || !results.contains(card)) return;
            selectPlayer(card.dataset.id, card.dataset.name, card.dataset.email, section);
        };
    } catch (e) {
        if (requestId !== searchRequestIds[key]) return;
        results.classList.remove('hidden');
        results.innerHTML = '<p class="text-red-400 text-sm text-center py-3">Ошибка загрузки</p>';
    }
}

// ===== СОЗДАНИЕ ИГРОКА =====
export async function createPlayer() {
    const nameEl = document.getElementById('createPlayerName');
    const msg = document.getElementById('createPlayerMsg');
    const btn = document.getElementById('createPlayerBtn');

    function showMsg(text, type = 'ok') {
        if (!msg) return;
        msg.textContent = text;
        msg.className = 'text-xs text-center mt-2 ' + (
            type === 'ok' ? 'text-emerald-400' :
            type === 'warn' ? 'text-zinc-400' : 'text-red-400'
        );
        msg.classList.remove('hidden');
    }

    const name = nameEl?.value.trim() || '';

    if (name.length < 2) {
        showMsg('Имя минимум 2 символа', 'error');
        return;
    }

    if (btn) btn.disabled = true;
    showMsg('Создание...', 'warn');

    try {
        const data = await AdminAPI.createPlayer({ name });
        showMsg(data?.message || 'Игрок успешно создан');

        if (nameEl) nameEl.value = '';

        const searchInput = document.getElementById('playerSearchInput');
        if (searchInput) {
            searchInput.value = name;
            searchPlayers('players');
        }
    } catch (e) {
        showMsg(e.message || 'Ошибка создания', 'error');
    } finally {
        if (btn) btn.disabled = false;
    }
}

/* =====================================================
   ВЫБОР ИГРОКА
   ===================================================== */

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
        activePcTab = 'main';
        document.getElementById('playerSelName').textContent = name;
        document.getElementById('playerSelEmail').textContent = email;
        updateAvatar(name);
        document.getElementById('playerSelBadges').innerHTML = '<span class="badge badge-neutral">Загрузка...</span>';
        document.getElementById('playerSelected').classList.remove('hidden');
        document.getElementById('playerSearchResults').classList.add('hidden');
        document.getElementById('playerMsg').classList.add('hidden');
        initPlayerCardSegments();
        await refreshPlayerUI('players');
    }
}

function getSubInfo(sub) {
    const info = { badges: '', active: false, expired: false, start: null, end: null };

    if (sub && sub.activeNow) {
        info.active = true;
        const expiresDate = new Date(sub.expiresAt);
        const daysLeft = Math.max(0, Math.ceil((expiresDate - new Date()) / (1000 * 60 * 60 * 24)));
        const expiresStr = expiresDate.toLocaleString('ru-RU', {
            day: 'numeric', month: 'long', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
        info.badges += `<span class="badge badge-active">Подписка до ${expiresStr}</span>`;
        info.badges += `<span class="badge ${daysLeft <= 7 ? 'badge-admin' : 'badge-neutral'}">${daysLeft} дн.</span>`;
    } else {
        info.badges += '<span class="badge badge-inactive">Нет подписки</span>';
        if (sub && sub.expiresAt) {
            info.expired = new Date(sub.expiresAt) < new Date();
        }
    }

    if (sub?.startedAt) info.start = sub.startedAt.slice(0, 10);
    if (sub?.expiresAt) info.end = sub.expiresAt.slice(0, 10);

    return info;
}

async function refreshPlayerUI(section) {
    if (!selectedPlayerId) return;
    const playerId = selectedPlayerId;

    // ===== Раздел «Подписки» =====
    if (section === 'sub') {
        let sub = null;
        let failed = false;
        try {
            sub = await AdminAPI.getPlayerSubscription(playerId);
            playersCache[playerId] = sub;
        } catch (e) {
            failed = true;
        }
        if (playerId !== selectedPlayerId) return;

        const info = getSubInfo(sub);
        document.getElementById('subSelBadges').innerHTML = failed
            ? '<span class="badge badge-inactive">Ошибка загрузки подписки</span>'
            : info.badges;
        document.getElementById('removeSubBtn').classList.toggle('hidden', !info.active);
        return;
    }

    // ===== Карточка игрока: всё грузим параллельно =====
    const [subRes, rolesRes, playersRes] = await Promise.allSettled([
        AdminAPI.getPlayerSubscription(playerId),
        AdminAPI.getPlayerRoles(playerId),
        AdminAPI.searchPlayers(selectedPlayerData?.name || '')
    ]);

    // Пока грузили, могли выбрать другого игрока
    if (playerId !== selectedPlayerId) return;

    // --- Подписка ---
    const sub = subRes.status === 'fulfilled' ? subRes.value : null;
    if (subRes.status === 'fulfilled') playersCache[playerId] = sub;

    const info = getSubInfo(sub);
    let badges = subRes.status === 'fulfilled'
        ? info.badges
        : '<span class="badge badge-inactive">Ошибка загрузки подписки</span>';

    document.getElementById('playerGiveSub30').classList.toggle('hidden', false);
    document.getElementById('playerGiveSub60').classList.toggle('hidden', false);
    document.getElementById('playerRemoveSub').classList.toggle('hidden', !info.active);

    const showEnable = !info.active && !info.expired && !!sub?.expiresAt;
    document.getElementById('playerEnableSub').classList.toggle('hidden', !showEnable);

    setValue('subPeriodStart', info.start || '');
    setValue('subPeriodEnd', info.end || '');

    // --- Роли ---
    if (rolesRes.status === 'fulfilled') {
        const roles = Array.isArray(rolesRes.value) ? rolesRes.value : [];
        const isAdmin = roles.includes('ROLE_ADMIN');

        if (isAdmin) {
            badges += ' <span class="badge badge-admin">Админ</span>';
        }

        const toggleBtn = document.getElementById('toggleAdminBtn');
        if (toggleBtn) {
            toggleBtn.classList.remove('hidden');
            if (isAdmin) {
                toggleBtn.textContent = 'Разжаловать';
                toggleBtn.className = 'btn btn-danger';
            } else {
                toggleBtn.textContent = 'Сделать админом';
                toggleBtn.className = 'btn btn-amber';
            }
        }
    }

    document.getElementById('playerSelBadges').innerHTML = badges;

    // --- Данные игрока ---
    if (playersRes.status === 'fulfilled' && Array.isArray(playersRes.value)) {
        const playerData = playersRes.value.find(p => String(p.id) === String(playerId));
        if (playerData) {
            selectedPlayerData = playerData;
            setValue('playerName', playerData.name || '');
            setValue('playerEmail', playerData.email || '');
            setValue('playerLeague', playerData.primaryLeague || '');

            // ЗАПОЛНЕНИЕ ГОРОДА
            setValue('playerCity', playerData.city || '');

            setValue('playerHalls', playerData.selectedHalls || '');
            setValue('playerLiveHalls', playerData.liveSelectedHalls || '');
            setStatusCheckbox('playerPush', !!playerData.pushEnabled);
            setStatusCheckbox('playerNotifications', !!playerData.notificationsEnabled);
            setText('playerLastLogin', formatLastLogin(playerData.lastLoginAt));

            const displayName = capitalizeName(playerData.name || '');
            setText('playerSelName', displayName);
            setText('playerSelEmail', playerData.email || '');
            updateAvatar(displayName);
        }
    }

    // Восстанавливаем активную вкладку, а не сбрасываем на «Основное»
    initPlayerCardSegments();
}

/* =====================================================
   ВКЛАДКИ КАРТОЧКИ
   ===================================================== */

function getPlayerCardPanels() {
    return {
        main: document.getElementById('pc-main'),
        sub: document.getElementById('pc-sub'),
        status: document.getElementById('pc-status'),
        service: document.getElementById('pc-service')
    };
}

function showPlayerCardTab(key, scrollTop) {
    const seg = document.getElementById('seg-player-card');
    const panels = getPlayerCardPanels();
    if (!panels[key]) key = 'main';

    Object.entries(panels).forEach(([k, el]) => {
        if (el) el.style.display = (k === key) ? 'flex' : 'none';
    });

    if (seg) {
        seg.querySelectorAll('.segmented-btn').forEach(b => {
            b.classList.toggle('active', b.dataset.pc === key);
        });
    }

    if (scrollTop) {
        const scroll = document.querySelector('#playerSelected .pc-scroll');
        if (scroll) scroll.scrollTop = 0;
    }
}

function initPlayerCardSegments() {
    const seg = document.getElementById('seg-player-card');
    if (!seg) return;

    seg.querySelectorAll('.segmented-btn').forEach(btn => {
        btn.onclick = () => {
            activePcTab = btn.dataset.pc;
            showPlayerCardTab(activePcTab, true);
        };
    });

    showPlayerCardTab(activePcTab, false);
}

/* =====================================================
   ФОРМАТИРОВАНИЕ И ЧЕКБОКСЫ
   ===================================================== */

function formatLastLogin(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    if (isNaN(d)) return '—';

    const abs = d.toLocaleString('ru-RU', {
        day: 'numeric', month: 'long', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const day = new Date(d);
    day.setHours(0, 0, 0, 0);
    const diff = Math.round((today - day) / 86400000);

    let rel = '';
    if (diff === 0) rel = 'сегодня';
    else if (diff === 1) rel = 'вчера';
    else if (diff > 1) rel = `${diff} дн. назад`;

    return rel ? `${abs} · ${rel}` : abs;
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
    if (!checkbox) return;

    const isChecked = checkbox.classList.contains('checked');
    setStatusCheckbox(prefix, !isChecked);
}

function isStatusChecked(prefix) {
    const checkbox = document.getElementById(prefix + 'Checkbox');
    return checkbox ? checkbox.classList.contains('checked') : false;
}

function showPlayerMsg(text, type = 'ok') {
    const msg = document.getElementById('playerMsg');
    if (!msg) return;

    if (playerMsgTimer) {
        clearTimeout(playerMsgTimer);
        playerMsgTimer = null;
    }

    msg.textContent = text;
    msg.className = 'text-xs text-center mt-2 ' + (type === 'ok'
        ? 'text-emerald-400'
        : type === 'warn'
            ? 'text-zinc-400'
            : 'text-red-400');
    msg.classList.remove('hidden');

    // Успешные сообщения прячем автоматически, ошибки оставляем
    if (type === 'ok') {
        playerMsgTimer = setTimeout(() => {
            msg.classList.add('hidden');
            playerMsgTimer = null;
        }, 3500);
    }
}

/* =====================================================
   ДЕЙСТВИЯ С ПОДПИСКОЙ
   ===================================================== */

export async function giveSub(days) {
    if (!selectedPlayerId) return;

    await runAction(async () => {
        showPlayerMsg('Выдача...', 'warn');
        try {
            await AdminAPI.giveSubscription(selectedPlayerId, days);
            showPlayerMsg(`Подписка выдана на ${days} дней`);
            await refreshPlayerUI('players');
        } catch (e) {
            showPlayerMsg('Ошибка при выдаче', 'error');
        }
    });
}

export async function giveSubCustom() {
    if (!selectedPlayerId) return;
    const days = parseInt(document.getElementById('playerCustomDays').value, 10);

    if (!days || days < 1 || days > 3650) {
        showPlayerMsg('Введите число от 1 до 3650', 'error');
        return;
    }

    await runAction(async () => {
        showPlayerMsg('Выдача...', 'warn');
        try {
            await AdminAPI.giveSubscription(selectedPlayerId, days);
            showPlayerMsg(`Подписка выдана на ${days} дней`);
            await refreshPlayerUI('players');
        } catch (e) {
            showPlayerMsg('Ошибка при выдаче', 'error');
        }
    });
}

export async function giveSubPeriod() {
    if (!selectedPlayerId) return;

    const start = document.getElementById('subPeriodStart')?.value;
    const end = document.getElementById('subPeriodEnd')?.value;

    if (!start || !end) {
        showPlayerMsg('Укажите обе даты', 'error');
        return;
    }

    if (start >= end) {
        showPlayerMsg('Дата начала должна быть раньше даты конца', 'error');
        return;
    }

    await runAction(async () => {
        showPlayerMsg('Выдача...', 'warn');
        try {
            const data = await AdminAPI.activateSubscriptionForPeriod(
                selectedPlayerId,
                `${start}T00:00:00`,
                `${end}T23:59:59`
            );
            showPlayerMsg(data?.message || 'Подписка выдана на период');
            await refreshPlayerUI('players');
        } catch (e) {
            showPlayerMsg(e.message || 'Ошибка при выдаче', 'error');
        }
    });
}

export async function removeSub() {
    if (!selectedPlayerId) return;

    await runAction(async () => {
        showPlayerMsg('Отключение...', 'warn');
        try {
            await AdminAPI.removeSubscription(selectedPlayerId);
            showPlayerMsg('Подписка отключена');
            await refreshPlayerUI('players');
        } catch (e) {
            showPlayerMsg('Ошибка при отключении', 'error');
        }
    });
}

export async function enableSub() {
    if (!selectedPlayerId) return;

    await runAction(async () => {
        showPlayerMsg('Включение...', 'warn');
        try {
            await AdminAPI.enableSubscription(selectedPlayerId);
            showPlayerMsg('Подписка включена');
            await refreshPlayerUI('players');
        } catch (e) {
            showPlayerMsg('Ошибка при включении', 'error');
        }
    });
}

/* =====================================================
   СОХРАНЕНИЕ, РОЛИ, СЕРВИС
   ===================================================== */

export async function updatePlayer() {
    if (!selectedPlayerId) return;

    await runAction(async () => {
        showPlayerMsg('Сохранение...', 'warn');
        try {
            const name = document.getElementById('playerName').value || null;
            const email = document.getElementById('playerEmail').value || null;

            await AdminAPI.updatePlayer(selectedPlayerId, {
                name,
                email,
                primaryLeague: document.getElementById('playerLeague').value || null,

                // СОХРАНЕНИЕ ГОРОДА
                city: document.getElementById('playerCity')?.value || null,

                selectedHalls: document.getElementById('playerHalls').value || null,
                liveSelectedHalls: document.getElementById('playerLiveHalls').value || null,
                pushEnabled: isStatusChecked('playerPush'),
                notificationsEnabled: isStatusChecked('playerNotifications')
            });
            showPlayerMsg('Сохранено');

            const displayName = capitalizeName(name || '');
            document.getElementById('playerSelName').textContent = displayName;
            document.getElementById('playerSelEmail').textContent = email || '';
            updateAvatar(displayName);

            if (selectedPlayerData) {
                selectedPlayerData.name = name || selectedPlayerData.name;
                selectedPlayerData.email = email;
            }
        } catch (e) {
            showPlayerMsg('Ошибка сохранения', 'error');
        }
    });
}

export async function togglePlayerRole(roleName) {
    if (!selectedPlayerId) return;
    const toggleBtn = document.getElementById('toggleAdminBtn');
    if (!toggleBtn) return;
    const isGrant = toggleBtn.classList.contains('btn-amber');

    await runAction(async () => {
        showPlayerMsg(isGrant ? 'Выдача роли...' : 'Отзыв роли...', 'warn');
        try {
            await AdminAPI.togglePlayerRole(selectedPlayerId, roleName, isGrant);
            showPlayerMsg(isGrant ? 'Роль выдана' : 'Роль отозвана');
            await refreshPlayerUI('players');
        } catch (e) {
            showPlayerMsg('Ошибка', 'error');
        }
    });
}

export async function deletePlayerTournaments() {
    if (!selectedPlayerId) return;

    await runAction(async () => {
        showPlayerMsg('Удаление...', 'warn');
        try {
            const data = await AdminAPI.deletePlayerTournaments(selectedPlayerId);
            showPlayerMsg(data?.message || 'Турниры удалены');
        } catch (e) {
            showPlayerMsg('Ошибка', 'error');
        }
    });
}

export async function resyncPlayerTournaments() {
    if (!selectedPlayerId) return;

    const from = document.getElementById('resyncFrom')?.value || null;
    const to = document.getElementById('resyncTo')?.value || null;
    const label = from && to ? `${from} – ${to}` : 'весь период';

    await runAction(async () => {
        showPlayerMsg(`Синхронизация ${label}...`, 'warn');
        try {
            const data = await AdminAPI.resyncPlayerTournaments(selectedPlayerId, from, to);
            showPlayerMsg(data?.message || `Синхронизация запущена: ${label}`);
        } catch (e) {
            showPlayerMsg(e.message || 'Ошибка синхронизации', 'error');
        }
    });
}

export async function deletePlayerAccount() {
    if (!selectedPlayerId) return;
    if (!confirm('Удалить аккаунт навсегда?')) return;

    await runAction(async () => {
        showPlayerMsg('Удаление...', 'warn');
        try {
            await AdminAPI.deletePlayerAccount(selectedPlayerId);
            showPlayerMsg('Аккаунт удалён');
            document.getElementById('playerSelected').classList.add('hidden');
            delete playersCache[selectedPlayerId];
            selectedPlayerId = null;
            selectedPlayerData = null;
        } catch (e) {
            showPlayerMsg('Ошибка удаления', 'error');
        }
    });
}

/* =====================================================
   ОБЗОР ПОДПИСОК — ПАГИНАЦИЯ КАК «ВХОДЫ»
   ===================================================== */

const SUBS_PAGE_SIZE = 20;
const subsState = { loaded: false, loading: false, items: [], page: 0 };

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
                <span class="subs-name">${escapeHtml(name)}</span>
                ${email ? `<span class="subs-email">${escapeHtml(email)}</span>` : ''}
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
    list.innerHTML = `<div class="subs-row subs-empty ${isError ? 'subs-warn' : ''}">${escapeHtml(text)}</div>`;
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