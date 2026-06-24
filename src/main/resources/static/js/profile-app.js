import { ProfileAPI } from './core/profile-api.js';
import { state } from './core/state.js';
import { capitalizeName } from './core/utils.js';
import {
    loadNotificationState,
    loadPushState,
    toggleNotifications,
    togglePush,
    togglePasswordVisibility,
    showPasswordForm,
    checkOldPassword,
    changePassword,
    saveEmail,
    logout
} from './modules/profile.js';

window.toggleNotifications = toggleNotifications;
window.togglePush = togglePush;
window.togglePassword = togglePasswordVisibility;
window.showPasswordForm = showPasswordForm;
window.checkOldPassword = checkOldPassword;
window.changePassword = changePassword;
window.saveEmail = saveEmail;
window.logout = logout;

async function init() {
    try {
        const user = await ProfileAPI.getMe();
        if (!user || !user.id) { window.location.href = '/'; return; }

        state.playerId = user.id;

        document.getElementById('profileName').textContent = capitalizeName(user.name) || 'Игрок';
        document.getElementById('emailInput').value = user.email || '';

        if (user.createdAt) {
            document.getElementById('createdAt').textContent = new Date(user.createdAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
        }

        await Promise.all([loadNotificationState(), loadPushState()]);

        // Загрузка статуса подписки
        try {
            const sub = await ProfileAPI.getSubscription();
            const subText = document.getElementById('subInfoText');
            const subBtn = document.getElementById('subActionBtn');
            if (sub && sub.active) {
                const until = new Date(sub.expiresAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
                subText.innerHTML = '✅ <span class="text-emerald-400">Активна</span> до ' + until;
                if (subBtn) subBtn.style.display = 'none';
            } else if (sub && sub.expiresAt) {
                const until = new Date(sub.expiresAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
                subText.innerHTML = '❌ <span class="text-red-400">Истекла</span> ' + until;
                if (subBtn) { subBtn.style.display = 'block'; subBtn.textContent = 'Оформить подписку'; }
            } else {
                subText.innerHTML = '❌ <span class="text-red-400">Не оформлена</span>';
                if (subBtn) { subBtn.style.display = 'block'; subBtn.textContent = 'Оформить подписку'; }
            }
        } catch(e) {
            document.getElementById('subInfoText').textContent = 'Не удалось загрузить';
        }
    } catch(e) {
        window.location.href = '/';
    }
}

document.addEventListener('DOMContentLoaded', init);