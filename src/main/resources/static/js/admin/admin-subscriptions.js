import { AdminAPI } from './admin-api.js';

let selectedPlayerId = null;

export function setSelectedPlayer(id) {
    selectedPlayerId = id;
}

export async function giveSub(days) {
    if (!selectedPlayerId) return;
    const msg = document.getElementById('subMsg');
    msg.textContent = 'Выдача...';
    msg.className = 'text-xs text-center mt-3 text-emerald-300/60';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.giveSubscription(selectedPlayerId, days);
        msg.textContent = `Подписка выдана на ${days} дней`;
        msg.className = 'text-xs text-center mt-3 text-emerald-400';
        await refreshSubUI();
    } catch (e) {
        msg.textContent = 'Ошибка при выдаче';
        msg.className = 'text-xs text-center mt-3 text-red-400';
    }
}

export async function giveSubCustom() {
    if (!selectedPlayerId) return;
    const days = parseInt(document.getElementById('customDaysInput').value);
    const msg = document.getElementById('subMsg');

    if (!days || days < 1 || days > 3650) {
        msg.textContent = 'Введите число от 1 до 3650';
        msg.className = 'text-xs text-center mt-3 text-red-400';
        msg.classList.remove('hidden');
        return;
    }

    msg.textContent = 'Выдача...';
    msg.className = 'text-xs text-center mt-3 text-emerald-300/60';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.giveSubscription(selectedPlayerId, days);
        msg.textContent = `Подписка выдана на ${days} дней`;
        msg.className = 'text-xs text-center mt-3 text-emerald-400';
        await refreshSubUI();
    } catch (e) {
        msg.textContent = 'Ошибка при выдаче';
        msg.className = 'text-xs text-center mt-3 text-red-400';
    }
}

export async function removeSub() {
    if (!selectedPlayerId) return;
    const msg = document.getElementById('subMsg');
    msg.textContent = 'Отключение...';
    msg.className = 'text-xs text-center mt-3 text-emerald-300/60';
    msg.classList.remove('hidden');

    try {
        await AdminAPI.removeSubscription(selectedPlayerId);
        msg.textContent = 'Подписка отключена';
        msg.className = 'text-xs text-center mt-3 text-emerald-400';
        await refreshSubUI();
    } catch (e) {
        msg.textContent = 'Ошибка при отключении';
        msg.className = 'text-xs text-center mt-3 text-red-400';
    }
}

async function refreshSubUI() {
    try {
        const sub = await AdminAPI.getPlayerSubscription(selectedPlayerId);
        let badges = '';
        if (sub && sub.active) {
            badges += `<span class="badge badge-active">Подписка до ${new Date(sub.expiresAt).toLocaleDateString('ru-RU')}</span>`;
        } else {
            badges += '<span class="badge badge-inactive">Нет подписки</span>';
        }
        document.getElementById('subSelBadges').innerHTML = badges;
        document.getElementById('removeSubBtn').classList.toggle('hidden', !(sub && sub.active));
    } catch (e) {}
}