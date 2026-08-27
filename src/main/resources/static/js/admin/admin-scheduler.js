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

function updateSchedulerUI(isPaused) {
    const dot = document.getElementById('schedulerStatusDot');
    const text = document.getElementById('schedulerStatusText');
    const btn = document.getElementById('schedulerToggleBtn');

    if (dot) dot.className = `scheduler-dot ${isPaused ? 'paused' : 'running'}`;
    if (text) text.textContent = isPaused ? 'На паузе' : 'Работает';
    if (btn) {
        btn.textContent = isPaused ? 'Возобновить' : 'Приостановить';

        // Сначала убираем все цветные классы
        btn.classList.remove('btn-amber', 'btn-emerald', 'btn-danger', 'btn-indigo');

        // Потом добавляем нужный
        btn.classList.add(isPaused ? 'btn-emerald' : 'btn-amber');
    }
}

window.loadSchedulerStatus = loadSchedulerStatus;
window.toggleScheduler = toggleScheduler;