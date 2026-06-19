import { AdminAPI } from '../core/admin-api.js';

export async function sendBroadcast() {
    const message = document.getElementById('broadcastMessage').value.trim();
    const msg = document.getElementById('broadcastMsg');
    const btn = document.getElementById('broadcastBtn');

    if (!message) {
        msg.textContent = '❌ Введите текст сообщения';
        msg.className = 'text-xs text-center mt-3 text-red-400';
        msg.classList.remove('hidden');
        return;
    }

    if (!confirm(`Отправить push и email всем пользователям?\n\n"${message}"`)) return;

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-sm"></span> Отправляем...';
    msg.classList.add('hidden');

    try {
        const data = await AdminAPI.broadcast(message);
        msg.textContent = data.message || '✅ Отправлено';
        msg.className = 'text-xs text-center mt-3 text-emerald-400';
        document.getElementById('broadcastMessage').value = '';
    } catch (e) {
        msg.textContent = '❌ Ошибка соединения';
        msg.className = 'text-xs text-center mt-3 text-red-400';
    } finally {
        btn.disabled = false;
        btn.innerHTML = '🚀 Отправить всем';
        msg.classList.remove('hidden');
    }
}