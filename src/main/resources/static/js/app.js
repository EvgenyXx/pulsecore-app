import { API } from './core/api.js';
import { state } from './core/state.js';
import { capitalizeName } from './core/utils.js';
import { loadDashboardWidgets, goHome, highlightNav } from './modules/dashboard.js';
import { loadTopWeekPreview, toggleTopWeek, switchLeague, switchPeriod, loadTopWeek } from './modules/top.js';
import { loadSelectedHalls, loadHallsContent, switchHallsDate, toggleAllHalls, toggleHallsCheckboxes, saveSelectedHalls } from './modules/lineup.js';
import { executeSum, openEditTournamentModal, closeEditTournamentModal, saveTournamentEdit, changePage } from './modules/sum.js';

// ===== Глобальные функции для onclick =====
window.goHome = goHome;
window.showAction = showAction;
window.toggleTopWeek = toggleTopWeek;
window.switchLeague = switchLeague;
window.switchPeriod = switchPeriod;
window.executeSum = executeSum;
window.openEditTournamentModal = openEditTournamentModal;
window.closeEditTournamentModal = closeEditTournamentModal;
window.saveTournamentEdit = saveTournamentEdit;
window.changePage = changePage;
window.switchHallsDate = switchHallsDate;
window.toggleAllHalls = toggleAllHalls;
window.toggleHallsCheckboxes = toggleHallsCheckboxes;
window.saveSelectedHalls = saveSelectedHalls;
window.logout = logout;

// ===== Навигация =====
function showAction(action) {
    document.getElementById('homePage').style.display = 'none';
    document.getElementById('actionPage').classList.remove('hidden');
    highlightNav('nav-' + action);

    const title = document.getElementById('actionTitle');
    const subtitle = document.getElementById('actionSubtitle');
    const content = document.getElementById('actionContent');

    const burgerBtn = `<button onclick="toggleMobileMenu()" class="md:hidden w-9 h-9 flex items-center justify-center rounded-lg bg-white/5 hover:bg-white/10 active:scale-90 text-white ml-auto">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
    </button>`;

    if (action === 'halls') {
        title.innerHTML = 'Расписание турниров' + burgerBtn;
        subtitle.textContent = 'Составы по выбранным залам';
        if (document.getElementById('proBadge').classList.contains('hidden')) {
            content.innerHTML = `<div class="widget-card rounded-2xl p-8 text-center"><div class="w-14 h-14 rounded-full bg-indigo-500/10 flex items-center justify-center mx-auto mb-4"><svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg></div><h3 class="text-lg font-bold text-white mb-2">Требуется подписка</h3><p class="text-zinc-400 text-sm mb-4">Оформите подписку чтобы видеть составы</p><a href="/subscribe" class="inline-block bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-xl px-6 py-3 text-sm transition-all">Оформить подписку</a></div>`;
            return;
        }
        loadHallsContent();
    } else if (action === 'sum') {
        title.innerHTML = '💰 Сумма за период' + burgerBtn;
        subtitle.textContent = 'Подсчёт заработка и список турниров';
        if (document.getElementById('proBadge').classList.contains('hidden')) {
            content.innerHTML = `<div class="widget-card rounded-2xl p-8 text-center"><div class="w-14 h-14 rounded-full bg-indigo-500/10 flex items-center justify-center mx-auto mb-4"><svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg></div><h3 class="text-lg font-bold text-white mb-2">Требуется подписка</h3><p class="text-zinc-400 text-sm mb-4">Оформите подписку чтобы посчитать заработок</p><a href="/subscribe" class="inline-block bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-xl px-6 py-3 text-sm transition-all">Оформить подписку</a></div>`;
            return;
        }
        state.currentSumPage = 0;
        content.innerHTML = `<div class="widget-card rounded-2xl p-6 text-center"><div class="spinner mx-auto mb-4"></div><p class="text-zinc-400 text-sm">Проверка доступа...</p></div>`;
        fetch('/api/player/sum?start=2026-01-01&end=' + new Date().toISOString().split('T')[0], { credentials: 'same-origin' })
            .then(r => {
                if (r.status === 402) {
                    content.innerHTML = `<div class="text-center py-8"><div class="text-5xl mb-4">🔒</div><h3 class="text-xl font-bold text-indigo-400 mb-2">Требуется подписка</h3></div>`;
                    return;
                }
                content.innerHTML = `
                    <div class="widget-card rounded-2xl p-4 mb-4">
                        <p class="text-sm font-semibold text-indigo-300 mb-2">📖 Как пользоваться:</p>
                        <p class="text-xs text-zinc-400 mb-1">1. Выберите даты начала и окончания (или одну)</p>
                        <p class="text-xs text-zinc-400 mb-1">2. Нажмите «Рассчитать»</p>
                        <p class="text-xs text-zinc-400">3. Система покажет общую сумму и список турниров с корректировкой ✏️</p>
                    </div>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
                        <div><label class="text-xs text-zinc-400 mb-1 block">📅 Дата с</label><input id="dateStart" type="text" class="flatpickr-input" placeholder="Выберите дату"></div>
                        <div><label class="text-xs text-zinc-400 mb-1 block">📅 Дата по (необязательно)</label><input id="dateEnd" type="text" class="flatpickr-input" placeholder="Выберите дату"></div>
                    </div>
                    <button onclick="executeSum()" class="btn-gold w-full">🧮 Рассчитать</button>
                    <p id="sumError" class="text-red-400 text-xs mt-3 hidden"></p>
                    <div id="actionResult" class="mt-4"></div>
                `;
                flatpickr('#dateStart', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });
                flatpickr('#dateEnd', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });
            })
            .catch(() => {
                content.innerHTML = '<div class="text-center py-8 text-red-400">❌ Ошибка</div>';
            });
    }
}

// ===== Logout =====
async function logout() {
    await API.logout();
    window.location.replace('/');
}

// ===== Push =====
async function checkPushStatus() {
    try {
        const reg = await navigator.serviceWorker.ready;
        const sub = await reg.pushManager.getSubscription();
        const toggle = document.getElementById('pushToggle');
        if (toggle && sub) toggle.checked = true;
    } catch(e) {}
}

window.checkPushStatus = checkPushStatus;

async function togglePush() {
    const toggle = document.getElementById('pushToggle');
    if (toggle.checked) {
        const ok = await enablePushNotifications();
        if (!ok) toggle.checked = false;
    } else {
        await disablePushNotifications();
    }
}

window.togglePush = togglePush;

async function enablePushNotifications() {
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
        alert('Ваш браузер не поддерживает push-уведомления');
        return false;
    }
    try {
        const reg = await navigator.serviceWorker.register('/sw.js');
        const vapidKey = await API.getVapidKey();
        const sub = await reg.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: urlB64ToUint8Array(vapidKey)
        });
        await API.subscribePush({
            endpoint: sub.endpoint,
            p256dh: btoa(String.fromCharCode(...new Uint8Array(sub.getKey('p256dh')))),
            auth: btoa(String.fromCharCode(...new Uint8Array(sub.getKey('auth'))))
        });
        return true;
    } catch(e) {
        console.error('Ошибка подписки:', e);
        return false;
    }
}

async function disablePushNotifications() {
    try {
        const reg = await navigator.serviceWorker.ready;
        const sub = await reg.pushManager.getSubscription();
        if (sub) {
            await sub.unsubscribe();
            await API.unsubscribePush({ endpoint: sub.endpoint });
        }
    } catch(e) {
        console.error('Ошибка отписки:', e);
    }
}

function urlB64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
}

// ===== Инициализация =====
async function init() {
    try {
        const data = await API.getMe();
        state.playerId = data.id;
        state.isAdmin = data.admin || false;

        if (data.admin) {
            document.getElementById('nav-admin')?.classList.remove('hidden');
            document.getElementById('mobile-nav-admin')?.classList.remove('hidden');
        }

        document.getElementById('playerName').textContent = capitalizeName(data.name);
        document.getElementById('sidebarPlayerName').textContent = capitalizeName(data.name);
        const mobileName = document.getElementById('mobilePlayerName');
        if (mobileName) mobileName.textContent = capitalizeName(data.name);

        await loadDashboardWidgets();
        loadTopWeekPreview();
        loadSelectedHalls();

        if (data.subscription?.active) {
            checkPushStatus();
        }
    } catch (e) {
        window.location.href = '/';
    }
}

// ===== Pull-to-Refresh =====
const ptr = document.getElementById('ptrIndicator');
let ptrStart = 0, ptrTriggered = false;

document.addEventListener('touchstart', e => {
    if (window.scrollY <= 5) {
        ptrStart = e.touches[0].clientX;
        ptrTriggered = false;
    }
}, { passive: true });

document.addEventListener('touchmove', e => {
    if (ptrTriggered || ptrStart === 0 || window.scrollY > 5) return;
    if (e.touches[0].clientX - ptrStart > 60) {
        ptrTriggered = true;
        ptr.innerHTML = '<span class="spinner-sm"></span> Обновление...';
        ptr.classList.add('active');
    }
}, { passive: true });

document.addEventListener('touchend', () => {
    if (ptrTriggered) {
        loadDashboardWidgets();
        loadTopWeekPreview();
        setTimeout(() => {
            ptr.innerHTML = '✓ Обновлено';
            ptr.classList.add('done');
            setTimeout(() => {
                ptr.classList.remove('active', 'done');
            }, 1200);
        }, 500);
    }
    ptrStart = 0;
});

// ===== Mobile Menu =====
window.toggleMobileMenu = function() {
    const menu = document.getElementById('mobileMenu');
    const overlay = document.getElementById('mobileMenuOverlay');
    if (menu.classList.contains('translate-x-0')) {
        menu.classList.remove('translate-x-0');
        menu.classList.add('translate-x-full');
        overlay.classList.add('hidden');
    } else {
        menu.classList.remove('translate-x-full');
        menu.classList.add('translate-x-0');
        overlay.classList.remove('hidden');
    }
};

window.mobileNav = function(action, el) {
    toggleMobileMenu();
    document.querySelectorAll('.mobile-nav-item').forEach(i => {
        i.classList.remove('bg-indigo-500/10', 'border-indigo-500/20');
    });
    el.classList.add('bg-indigo-500/10', 'border-indigo-500/20');
    if (action === 'home') goHome();
    else showAction(action);
};

// ===== Запуск =====
document.addEventListener('DOMContentLoaded', init);