import { AdminAPI } from '../core/admin-api.js';
import { formatMoney, capitalizeName } from '../core/utils.js';

let selectedPlayerId = null;
let playersCache = {};

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

    try {
        const sub = await AdminAPI.getPlayerSubscription(selectedPlayerId);
        playersCache[selectedPlayerId] = sub;
        if (sub && sub.active) {
            badges += `<span class="badge-active">Подписка до ${new Date(sub.expiresAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })}</span>`;
        } else {
            badges += '<span class="badge-inactive">Нет подписки</span>';
        }
    } catch (e) {}

    if (section === 'sub') {
        document.getElementById('subSelBadges').innerHTML = badges;
        const cached = playersCache[selectedPlayerId];
        document.getElementById('removeSubBtn').classList.toggle('hidden', !(cached && cached.active));
    } else {
        try {
            const roles = await AdminAPI.getPlayerRoles(selectedPlayerId);
            if (roles.includes('ROLE_ADMIN')) {
                badges += ' <span class="badge-admin">Админ</span>';
            }
            document.getElementById('playerSelBadges').innerHTML = badges;
            document.getElementById('grantAdminBtn').classList.toggle('hidden', roles.includes('ROLE_ADMIN'));
            document.getElementById('revokeAdminBtn').classList.toggle('hidden', !roles.includes('ROLE_ADMIN'));
        } catch (e) {}
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
        msg.textContent = isGrant ? `Роль ${roleName} выдана` : `Роль ${roleName} отозвана`;
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
    msg.textContent = 'Запуск фоновой загрузки...';
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
    msg.textContent = 'Удаление аккаунта...';
    msg.className = 'text-xs text-center text-zinc-400';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.deletePlayerAccount(selectedPlayerId);
        msg.textContent = 'Аккаунт удалён';
        msg.className = 'text-xs text-center text-emerald-400';
        document.getElementById('playerSelected').classList.add('hidden');
    } catch (e) {
        msg.textContent = 'Ошибка при удалении';
        msg.className = 'text-xs text-center text-red-400';
    }
}