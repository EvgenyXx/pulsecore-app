import { ProfileAPI } from '../core/profile-api.js';

let notificationsEnabled = true;
let pushEnabled = true;

// ===== Загрузка состояний =====
export async function loadNotificationState() {
    try {
        const data = await ProfileAPI.getNotificationStatus();
        notificationsEnabled = data.enabled;
    } catch(e) {}
    updateNotificationToggleUI();
}

export async function loadPushState() {
    try {
        const data = await ProfileAPI.getPushStatus();
        pushEnabled = data.pushEnabled;
    } catch(e) {}
    updatePushToggleUI();
}

// ===== UI переключателей =====
function updateNotificationToggleUI() {
    const track = document.getElementById('toggleTrack');
    const thumb = document.getElementById('toggleThumb');
    const label = document.getElementById('notifLabel');
    if (!track || !thumb || !label) return;

    if (notificationsEnabled) {
        track.className = 'toggle-track on';
        thumb.className = 'toggle-thumb on';
        label.textContent = 'Включены';
        label.className = 'text-xs text-emerald-400';
    } else {
        track.className = 'toggle-track off';
        thumb.className = 'toggle-thumb';
        label.textContent = 'Отключены';
        label.className = 'text-xs text-gray-500';
    }
}

function updatePushToggleUI() {
    const track = document.getElementById('pushToggleTrack');
    const thumb = document.getElementById('pushToggleThumb');
    const label = document.getElementById('pushLabel');
    if (!track || !thumb || !label) return;

    if (pushEnabled) {
        track.className = 'toggle-track on';
        thumb.className = 'toggle-thumb on';
        label.textContent = 'Включены';
        label.className = 'text-xs text-emerald-400';
    } else {
        track.className = 'toggle-track off';
        thumb.className = 'toggle-thumb';
        label.textContent = 'Отключены';
        label.className = 'text-xs text-gray-500';
    }
}

// ===== Действия =====
export async function toggleNotifications() {
    notificationsEnabled = !notificationsEnabled;
    updateNotificationToggleUI();
    try {
        await ProfileAPI.setNotification(notificationsEnabled);
    } catch(e) {
        notificationsEnabled = !notificationsEnabled;
        updateNotificationToggleUI();
    }
}

export async function togglePush() {
    try {
        const data = await ProfileAPI.togglePush();
        pushEnabled = data.pushEnabled;
    } catch(e) {}
    updatePushToggleUI();
}

export function togglePasswordVisibility(inputId, btn) {
    const input = document.getElementById(inputId);
    if (!input) return;
    input.type = input.type === 'password' ? 'text' : 'password';
    btn.textContent = input.type === 'password' ? '👁️' : '🙈';
}

export function showPasswordForm() {
    document.getElementById('passwordForm').classList.remove('hidden');
    document.getElementById('changePasswordBtn').classList.add('hidden');
    document.getElementById('stepOld').classList.remove('hidden');
    document.getElementById('stepNew').classList.add('hidden');
    document.getElementById('oldPasswordInput').value = '';
    document.getElementById('oldPasswordInput').disabled = false;
    document.getElementById('newPasswordInput').value = '';
    document.getElementById('oldPasswordError').classList.add('hidden');
    const checkBtn = document.getElementById('checkOldBtn');
    checkBtn.disabled = false;
    checkBtn.innerHTML = 'Проверить →';
}

function resetPasswordForm() {
    document.getElementById('passwordForm').classList.add('hidden');
    document.getElementById('changePasswordBtn').classList.remove('hidden');
    document.getElementById('stepNew').classList.add('hidden');
    document.getElementById('oldPasswordInput').value = '';
    document.getElementById('oldPasswordInput').disabled = false;
    document.getElementById('newPasswordInput').value = '';
}

export async function checkOldPassword() {
    const oldPassword = document.getElementById('oldPasswordInput').value;
    const errorEl = document.getElementById('oldPasswordError');
    errorEl.classList.add('hidden');

    if (!oldPassword) {
        errorEl.textContent = 'Введите старый пароль';
        errorEl.classList.remove('hidden');
        return;
    }

    const btn = document.getElementById('checkOldBtn');
    btn.disabled = true;
    btn.innerHTML = '⏳ Проверка...';

    try {
        await ProfileAPI.verifyPassword(oldPassword);
        document.getElementById('stepNew').classList.remove('hidden');
        document.getElementById('checkOldBtn').classList.add('hidden');
        document.getElementById('oldPasswordInput').disabled = true;
        document.getElementById('newPasswordInput').focus();
    } catch(e) {
        errorEl.textContent = e.message || 'Неверный пароль';
        errorEl.classList.remove('hidden');
        btn.disabled = false;
        btn.innerHTML = 'Проверить →';
    }
}

export async function changePassword() {
    const oldPassword = document.getElementById('oldPasswordInput').value;
    const newPassword = document.getElementById('newPasswordInput').value;
    const errorEl = document.getElementById('newPasswordError');
    errorEl.classList.add('hidden');

    if (!newPassword || newPassword.length < 6) {
        errorEl.textContent = 'Минимум 6 символов';
        errorEl.classList.remove('hidden');
        return;
    }

    const btn = document.getElementById('changeBtn');
    btn.disabled = true;
    btn.innerHTML = '⏳';

    try {
        await ProfileAPI.changePassword(oldPassword, newPassword);
        showMessage('✅ Пароль изменён!', 'text-emerald-400');
        resetPasswordForm();
    } catch(e) {
        errorEl.textContent = e.message || 'Ошибка';
        errorEl.classList.remove('hidden');
        btn.disabled = false;
        btn.innerHTML = 'Сменить пароль ✅';
    }
}

export async function saveEmail() {
    const btn = document.getElementById('saveEmailBtn');
    const email = document.getElementById('emailInput').value.trim();

    btn.disabled = true;
    btn.innerHTML = '⏳';

    try {
        await ProfileAPI.saveEmail(email);
        showMessage('✅ Email обновлён!', 'text-emerald-400');
    } catch(e) {
        showMessage('❌ ' + (e.message || 'Ошибка'), 'text-red-400');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '💾 Сохранить email';
    }
}

export async function logout() {
    await ProfileAPI.logout();
    window.location.replace('/');
}

// ===== Вспомогательные =====
function showMessage(text, className) {
    const msgBox = document.getElementById('messageBox');
    if (!msgBox) return;
    msgBox.textContent = text;
    msgBox.className = 'text-xs md:text-sm text-center mb-4 ' + className;
    msgBox.classList.remove('hidden');
    setTimeout(() => msgBox.classList.add('hidden'), 3000);
}