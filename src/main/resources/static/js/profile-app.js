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
        if (!user || !user.id) {
            console.warn('[profile] user is empty');
            return;
        }

        state.playerId = user.id;

        const profileNameEl = document.getElementById('profileName');
        const emailInputEl = document.querySelector('#settings-account #settingsEmailInput');
        const createdAtEl = document.getElementById('createdAt');

        if (profileNameEl) profileNameEl.textContent = capitalizeName(user.name) || 'Игрок';
        if (emailInputEl) emailInputEl.value = user.email || '';

        if (user.createdAt && createdAtEl) {
            createdAtEl.textContent = new Date(user.createdAt).toLocaleDateString('ru-RU', {
                day: 'numeric', month: 'long', year: 'numeric'
            });
        }

        await Promise.all([loadNotificationState(), loadPushState()]);

        try {
            const sub = await ProfileAPI.getSubscription();
            const subText = document.getElementById('subInfoText');
            const subBtn = document.getElementById('subActionBtn');
            const premiumBadge = document.getElementById('profilePremiumBadge');

            if (sub && sub.activeNow) {
                if (premiumBadge) premiumBadge.classList.remove('hidden');

                const until = new Date(sub.expiresAt).toLocaleDateString('ru-RU', {
                    day: 'numeric', month: 'long', year: 'numeric'
                });
                if (subText) subText.textContent = 'Активна до ' + until;
                if (subBtn) {
                    subBtn.style.display = 'block';
                    subBtn.textContent = 'Продлить';
                }
            } else {
                if (premiumBadge) premiumBadge.classList.add('hidden');
                if (subText) subText.textContent = 'Не активна';
                if (subBtn) {
                    subBtn.style.display = 'block';
                    subBtn.textContent = 'Оформить подписку';
                }
            }
        } catch (e) {
            console.warn('[profile] subscription error:', e);
            const subText = document.getElementById('subInfoText');
            if (subText) subText.textContent = 'Не удалось загрузить';
        }
    } catch (e) {
        console.error('[profile] init error:', e);
    }
}

window.initProfileApp = init;

export { init };