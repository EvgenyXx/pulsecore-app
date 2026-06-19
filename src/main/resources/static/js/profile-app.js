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
    } catch(e) {
        window.location.href = '/';
    }
}

document.addEventListener('DOMContentLoaded', init);