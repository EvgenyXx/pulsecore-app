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
window.toggleProfilePush = togglePush;
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
            const premiumBadge = document.getElementById('profilePremiumBadge');

            if (sub && sub.active) {
                if (premiumBadge) premiumBadge.classList.remove('hidden');

                const until = new Date(sub.expiresAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
                subText.textContent = 'Активна до ' + until;
                subBtn.style.display = 'block';
                subBtn.textContent = 'Продлить';
            } else {
                if (premiumBadge) premiumBadge.classList.add('hidden');

                subText.textContent = 'Не активна';
                subBtn.style.display = 'block';
                subBtn.textContent = 'Оформить подписку';
            }
        } catch(e) {
            document.getElementById('subInfoText').textContent = 'Не удалось загрузить';
        }
    } catch(e) {
        window.location.href = '/';
    }
}

window.initProfileApp = init;

document.addEventListener('DOMContentLoaded', init);