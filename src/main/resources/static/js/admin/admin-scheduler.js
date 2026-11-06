// js/admin/admin-scheduler.js

import { AdminAPI } from './admin-api.js';

export async function loadSchedulerStatus() {
    try {
        const data = await AdminAPI.getSchedulerStatus();
        updateSchedulerUI(data.paused);
    } catch (e) {
        console.error('Ошибка загрузки статуса планировщика:', e);
    }
}

export async function toggleScheduler() {
    try {
        const data = await AdminAPI.getSchedulerStatus();
        if (data.paused) {
            await AdminAPI.resumeScheduler();
        } else {
            await AdminAPI.pauseScheduler();
        }
        await loadSchedulerStatus();
    } catch (e) {
        console.error('Ошибка переключения планировщика:', e);
    }
}

export async function runSchedulerTask(apiMethod) {
    try {
        await AdminAPI[apiMethod]();
        showToast('Запущено успешно!', 'success');
    } catch (e) {
        console.error('Ошибка:', e);
        showToast('Ошибка при запуске: ' + e.message, 'error');
    }
}

function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    const bgColor = type === 'success' ? 'bg-emerald-600' : 'bg-red-500';
    toast.className = `fixed bottom-5 right-5 px-6 py-3 rounded-xl shadow-2xl text-white font-medium z-[9999] transition-all duration-300 transform translate-y-10 opacity-0 ${bgColor}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    requestAnimationFrame(() => toast.classList.remove('translate-y-10', 'opacity-0'));
    setTimeout(() => {
        toast.classList.add('translate-y-10', 'opacity-0');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function updateSchedulerUI(isPaused) {
    const dot = document.getElementById('schedulerStatusDot');
    const text = document.getElementById('schedulerStatusText');
    const btn = document.getElementById('schedulerToggleBtn');

    if (dot) dot.className = `scheduler-dot ${isPaused ? 'paused' : 'running'}`;
    if (text) text.textContent = isPaused ? 'На паузе' : 'Работает';
    if (btn) {
        btn.textContent = isPaused ? 'Возобновить' : 'Приостановить';
        btn.classList.remove('btn-amber', 'btn-emerald', 'btn-danger', 'btn-indigo');
        btn.classList.add(isPaused ? 'btn-emerald' : 'btn-amber');
    }
}

window.loadSchedulerStatus = loadSchedulerStatus;
window.toggleScheduler = toggleScheduler;
window.runSchedulerTask = runSchedulerTask;