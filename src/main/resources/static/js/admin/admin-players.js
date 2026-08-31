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
        // Обновляем бейджи
        document.getElementById('playerSelBadges').innerHTML = badges;

        // Обновляем кнопки подписки в карточке игрока
        document.getElementById('playerGiveSub30').classList.toggle('hidden', false);
        document.getElementById('playerGiveSub60').classList.toggle('hidden', false);
        document.getElementById('playerRemoveSub').classList.toggle('hidden', !subActive);

        try {
            const roles = await AdminAPI.getPlayerRoles(selectedPlayerId);
            if (roles.includes('ROLE_ADMIN')) {
                badges += ' <span class="badge badge-admin">Админ</span>';
            }
            document.getElementById('playerSelBadges').innerHTML = badges;
            document.getElementById('grantAdminBtn').classList.toggle('hidden', roles.includes('ROLE_ADMIN'));
            document.getElementById('revokeAdminBtn').classList.toggle('hidden', !roles.includes('ROLE_ADMIN'));
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
    const isGrant = !document.getElementById('grantAdminBtn').classList.contains('hidden');

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
    const msg = document.getElementById('playerMsg');
    msg.textContent = 'Синхронизация...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        const data = await AdminAPI.resyncPlayerTournaments(selectedPlayerId);
        msg.textContent = data.message;
        msg.className = 'text-xs text-center text-emerald-400';
    } catch (e) {
        msg.textContent = 'Ошибка';
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