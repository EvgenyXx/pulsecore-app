import { API } from './core/api.js';
import { state } from './core/state.js';
import { capitalizeName } from './core/utils.js';
import { loadDashboardWidgets, goHome, highlightNav } from './dashboard/dashboard.js';
import { loadTopWeek, switchLeague, switchPeriod } from './modules/top.js';
import { loadSelectedHalls, loadHallsContent, switchHallsDate, toggleAllHalls, toggleHallsCheckboxes, saveSelectedHalls } from './modules/lineup.js';
import { executeSum, openEditTournamentModal, closeEditTournamentModal, saveTournamentEdit, changePage } from './modules/sum.js';
import { checkSubscription, subBlockHtml } from './modules/subscription-block.js';

window.goHome = goHome;
window.showAction = showAction;
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
window.toggleTheme = toggleTheme;

// Красивый лоадер
document.body.insertAdjacentHTML('afterbegin', `
    <div id="appLoader" style="
        position:fixed;inset:0;z-index:9999;
        background: radial-gradient(ellipse at center, #0c0c18 0%, #060610 100%);
        display:flex;flex-direction:column;align-items:center;justify-content:center;
        transition: opacity 0.4s ease;
        opacity: 1;
    ">
        <div style="
            position:relative;
            width:64px;height:64px;
            margin-bottom:22px;
        ">
            <div style="
                position:absolute;inset:0;
                border:3px solid rgba(99,102,241,0.12);
                border-radius:50%;
            "></div>
            <div style="
                position:absolute;inset:0;
                border:3px solid transparent;
                border-top-color:#818cf8;
                border-radius:50%;
                animation: appSpin 0.9s linear infinite;
                filter: drop-shadow(0 0 8px rgba(129,140,248,0.5));
            "></div>
        </div>
        <div style="
            font-family:'Inter',sans-serif;
            font-size:15px;
            font-weight:600;
            color:#818cf8;
            letter-spacing:1.5px;
            opacity:0.85;
        ">PULSECORE</div>
        <style>@keyframes appSpin{to{transform:rotate(360deg)}}</style>
    </div>
`);

async function showAction(action) {
    const homePage = document.getElementById('homePage');
    const actionPage = document.getElementById('actionPage');
    const content = document.getElementById('actionContent');
    const title = document.getElementById('actionTitle');

    highlightNav('nav-' + action);

    homePage.style.display = 'none';
    actionPage.classList.remove('hidden');
    actionPage.style.opacity = '1';

    if (action === 'halls') {
        title.innerHTML = `
    <h2 class="action-title-3d" data-text="Расписание турниров">Расписание турниров</h2>
`;

        loadHallsContent();
    } else if (action === 'sum') {
        title.innerHTML = `
    <h2 class="action-title-3d" data-text="Сумма за период">Сумма за период</h2>
`;

        const hasSub = await checkSubscription();
        if (!hasSub) {
            content.innerHTML = subBlockHtml();
            return;
        }

        state.currentSumPage = 0;

        content.innerHTML = `
        <div class="sum-menu space-y-2">
            <div class="apple-card menu-card" onclick="showSumCalculator()">
                <div class="menu-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                </div>
                <div class="flex-1">
                    <h3 class="menu-title">Подсчёт суммы</h3>
                    <p class="menu-subtitle">Заработок за период</p>
                </div>
                <svg class="menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
            </div>
            
            <div class="apple-card menu-card" onclick="showReportForm()">
                <div class="menu-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                </div>
                <div class="flex-1">
                    <h3 class="menu-title">Отчёт на почту</h3>
                    <p class="menu-subtitle">Запланировать отправку</p>
                </div>
                <svg class="menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
            </div>
        </div>
        <div id="sumCalculator" class="hidden mt-4"></div>
        <p id="sumError" class="text-red-400 text-xs mt-3 hidden"></p>
        <div id="actionResult" class="mt-4"></div>
        `;
    }
}

window.showSumCalculator = function() {
    document.querySelector('.sum-menu')?.classList.add('hidden');
    document.getElementById('actionResult').innerHTML = '';
    const calc = document.getElementById('sumCalculator');
    calc.classList.remove('hidden');
    calc.innerHTML = `
        <button onclick="backToSumButtons()" class="flex items-center gap-2 text-zinc-400 hover:text-white text-sm mb-4 transition-colors">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg> Назад
        </button>
        <div class="apple-card mb-4">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
                <div><label class="text-xs text-zinc-400 mb-1 block">Дата с</label><input id="dateStart" type="text" class="flatpickr-input" placeholder="Выберите дату"></div>
                <div><label class="text-xs text-zinc-400 mb-1 block">Дата по</label><input id="dateEnd" type="text" class="flatpickr-input" placeholder="Выберите дату"></div>
            </div>
            <button onclick="executeSum()" class="apple-save-btn">Рассчитать</button>
        </div>
    `;
    flatpickr('#dateStart', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });
    flatpickr('#dateEnd', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });
};

window.showReportForm = function() {
    document.querySelector('.sum-menu')?.classList.add('hidden');
    document.getElementById('actionResult').innerHTML = '';
    import('/js/modules/scheduled-report.js').then(m => {
        const calc = document.getElementById('sumCalculator');
        if (calc) calc.classList.add('hidden');
        document.getElementById('actionResult').innerHTML = `
            <button onclick="backToSumButtons()" class="flex items-center gap-2 text-zinc-400 hover:text-white text-sm mb-4 transition-colors">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg> Назад
            </button>
        ` + m.renderReportForm();
        flatpickr('#reportDateStart', { locale: 'ru', dateFormat: 'Y-m-d' });
        flatpickr('#reportDateEnd', { locale: 'ru', dateFormat: 'Y-m-d' });
        flatpickr('#reportScheduledDate', { locale: 'ru', dateFormat: 'Y-m-d', minDate: 'today' });
    });
};

window.backToSumButtons = function() {
    document.querySelector('.sum-menu')?.classList.remove('hidden');
    document.getElementById('sumCalculator').classList.add('hidden');
    document.getElementById('actionResult').innerHTML = '';
};

async function logout() { await API.logout(); window.location.replace('/'); }

function toggleTheme() {
    const html = document.documentElement;
    const current = html.getAttribute('data-theme') || 'dark';
    const next = current === 'dark' ? 'ocean' : 'dark';
    html.setAttribute('data-theme', next);
    fetch('/api/player/me/theme', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin',
        body: JSON.stringify({ theme: next })
    }).catch(() => {});
}

// ============ PUSH ============

const SW_TIMEOUT = 3000;

function swReadyWithTimeout() {
    return Promise.race([
        navigator.serviceWorker.ready,
        new Promise((_, rej) => setTimeout(() => rej(new Error('SW timeout')), SW_TIMEOUT))
    ]);
}

async function checkPushStatus() {
    const container = document.getElementById('pushToggleContainer');
    if (!container) return;

    // Нет поддержки — показываем блок
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
        container.style.display = '';
        return;
    }

    try {
        const reg = await swReadyWithTimeout();

        // pushManager может быть undefined (iOS < 16.4)
        if (!reg.pushManager) {
            container.style.display = '';
            return;
        }

        const sub = await reg.pushManager.getSubscription();
        container.style.display = sub ? 'none' : '';
    } catch (e) {
        console.warn('Push check:', e.message);
        container.style.display = '';
    }
}
window.checkPushStatus = checkPushStatus;

async function togglePush() {
    const toggle = document.getElementById('pushToggle');
    if (!toggle) return;

    if (toggle.checked) {
        const ok = await enablePushNotifications();
        if (!ok) {
            toggle.checked = false;
        } else {
            document.getElementById('pushToggleContainer')?.remove();
        }
    } else {
        await disablePushNotifications();
    }
}
window.togglePush = togglePush;

async function enablePushNotifications() {
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
        alert('Браузер не поддерживает push-уведомления');
        return false;
    }

    if (Notification.permission === 'denied') {
        alert('Уведомления запрещены. Разрешите их в настройках браузера для этого сайта.');
        return false;
    }

    if (Notification.permission !== 'granted') {
        const permission = await Notification.requestPermission();
        if (permission !== 'granted') {
            return false;
        }
    }

    try {
        const reg = await navigator.serviceWorker.register('/sw.js');
        await swReadyWithTimeout();

        const vapidKey = await API.getVapidKey();
        const sub = await reg.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: urlB64ToUint8Array(vapidKey)
        });

        const subJson = sub.toJSON();
        await API.subscribePush({
            endpoint: subJson.endpoint,
            p256dh: subJson.keys.p256dh,
            auth: subJson.keys.auth
        });

        return true;
    } catch (e) {
        console.error('Push subscribe error:', e);
        return false;
    }
}

async function disablePushNotifications() {
    try {
        const reg = await swReadyWithTimeout();
        const sub = await reg.pushManager.getSubscription();
        if (sub) {
            await sub.unsubscribe();
            await API.unsubscribePush({ endpoint: sub.endpoint });
        }
    } catch (e) {}
}

function urlB64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) outputArray[i] = rawData.charCodeAt(i);
    return outputArray;
}

// ============ INIT ============

function hideLoader() {
    const loader = document.getElementById('appLoader');
    if (!loader) return;
    loader.style.opacity = '0';
    setTimeout(() => loader.remove(), 400);
}

async function init() {
    try {
        const res = await fetch('/api/player/me', { credentials: 'same-origin' });
        if (!res.ok) throw new Error('Not authenticated');
        const data = await res.json();

        state.playerId = data.id;
        state.playerName = data.name || '';
        state.isAdmin = data.admin || false;

        if (data.admin) {
            document.getElementById('nav-admin')?.classList.remove('hidden');
            document.getElementById('mobile-nav-admin')?.classList.remove('hidden');
        }

        if (data.theme) {
            document.documentElement.setAttribute('data-theme', data.theme);
        }

        // Регистрируем SW сразу — иначе navigator.serviceWorker.ready висит вечно
        if ('serviceWorker' in navigator) {
            try {
                await navigator.serviceWorker.register('/sw.js');
            } catch (e) {
                console.warn('SW register failed:', e);
            }
        }

        await loadDashboardWidgets();
        loadTopWeek(null);
        loadSelectedHalls();

        const { initDashboardRouter } = await import('./modules/dashboard-router.js');
        initDashboardRouter();

        checkPushStatus();
    } catch (e) {
        console.error('Init error:', e);
    } finally {
        hideLoader();
    }
}

const ptr = document.getElementById('ptrIndicator');
let ptrStart = 0, ptrTriggered = false;
document.addEventListener('touchstart', e => { if (window.scrollY <= 5) { ptrStart = e.touches[0].clientX; ptrTriggered = false; } }, { passive: true });
document.addEventListener('touchmove', e => { if (ptrTriggered || ptrStart === 0 || window.scrollY > 5) return; if (e.touches[0].clientX - ptrStart > 60) { ptrTriggered = true; ptr.innerHTML = '<span class="spinner-sm"></span> Обновление...'; ptr.classList.add('active'); } }, { passive: true });
document.addEventListener('touchend', () => { if (ptrTriggered) { loadDashboardWidgets(); loadTopWeek(null); setTimeout(() => { ptr.innerHTML = '✓ Обновлено'; ptr.classList.add('done'); setTimeout(() => ptr.classList.remove('active', 'done'), 1200); }, 500); } ptrStart = 0; });

document.addEventListener('DOMContentLoaded', init);