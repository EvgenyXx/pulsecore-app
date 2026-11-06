// js/boot.js — единственная точка входа SPA

const AUTH_RETRY_DELAYS = [300, 600, 900];
const sleep = (ms) => new Promise(r => setTimeout(r, ms));

const MIN_BOOT_TIME = 3500;
const __bootStartedAt = Date.now();

const hint = (text) => {
    const el = document.getElementById('bootHint');
    if (el) el.textContent = text;
};

async function ensureMinBootTime() {
    const elapsed = Date.now() - __bootStartedAt;
    const remaining = MIN_BOOT_TIME - elapsed;
    if (remaining > 0) {
        await sleep(remaining);
    }
}

async function probeMe() {
    try {
        const res = await fetch('/api/player/me', { credentials: 'same-origin' });
        if (!res.ok) return null;
        return await res.json();
    } catch (e) { return null; }
}

async function fetchMeWithRetry() {
    for (let i = 0; i < AUTH_RETRY_DELAYS.length; i++) {
        const me = await probeMe();
        if (me) return me;
        await sleep(AUTH_RETRY_DELAYS[i]);
    }
    return null;
}

function hideBoot() {
    const overlay = document.getElementById('bootOverlay');
    if (!overlay) return;
    overlay.classList.add('fade-out');
    setTimeout(() => {
        overlay.classList.add('hidden');
    }, 400);
}

function showAuth() {
    const overlay = document.getElementById('bootOverlay');
    if (overlay) {
        overlay.classList.add('fade-out');
        setTimeout(() => {
            overlay.classList.add('hidden');
        }, 400);
    }
    setTimeout(() => {
        document.getElementById('authScreen')?.classList.add('visible');
    }, 400);
}

function showApp() {
    const root = document.getElementById('appRoot');
    if (root) root.style.display = '';
}

function showToast(message, isError = false) {
    const old = document.getElementById('customToast');
    if (old) old.remove();

    const toast = document.createElement('div');
    toast.id = 'customToast';
    toast.className = 'custom-toast' + (isError ? ' error' : '');
    toast.textContent = message;
    document.body.appendChild(toast);

    requestAnimationFrame(() => toast.classList.add('show'));
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

function initShareButton() {
    const btn = document.getElementById('shareAppBtn');
    if (!btn || btn.dataset.shareBound === 'true') return;
    btn.dataset.shareBound = 'true';

    btn.addEventListener('click', async () => {
        const url = 'https://pulsecore-app.ru';

        if (navigator.share) {
            try {
                await navigator.share({
                    title: 'PulseCore',
                    text: 'Смотри свой заработок и статистику турниров в настольный теннис!',
                    url: url
                });
            } catch (err) {
                if (err.name !== 'AbortError') {
                    console.error('Ошибка шаринга:', err);
                    showToast('Не удалось открыть меню. Скопируйте ссылку: ' + url, true);
                }
            }
        } else {
            try {
                await navigator.clipboard.writeText(url);
                showToast('Ссылка скопирована! Вставьте её в мессенджер.');
            } catch (err) {
                showToast('Ссылка: ' + url);
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', initShareButton);

async function startApp() {
    showApp();

    const { initRoleCheck } = await import('/js/modules/role-check.js');
    const { initSidebar }   = await import('/js/sidebar.js');
    const { initBottomTab } = await import('/js/modules/bottom-tab.js');

    await initRoleCheck();
    await initSidebar('home');
    initBottomTab('home');

    await import('/js/modules/top.js');
    await import('/js/modules/subscription-block.js');
    await import('/js/modules/live.js');
    await import('/js/modules/live-tournament.js');

    const { init: initCompare } = await import('/js/compare/compare-app.js');
    initCompare();

    await import('/js/profile-app.js');
    await import('/js/analytics-app.js');
    await import('/js/modules/scheduled-report.js');
    await import('/js/show-settings-page.js');

    const app = await import('/js/app.js');
    await app.init();

    const { initStars } = await import('/js/modules/stars.js');
    initStars();

    const { initThemePicker, toggleThemeSheet, setTheme } = await import('/js/modules/theme-picker.js');
    initThemePicker();
    window.toggleThemeSheet = toggleThemeSheet;
    window.setTheme = setTheme;

    const { initSwipeTabs } = await import('/js/modules/swipe-tabs.js');
    const { initTapPeriod } = await import('/js/modules/tap-period.js');
    const { state }         = await import('/js/core/state.js');

    const scheduleContext = {
        container: document.body,
        tabSelector: '.date-tab',
        activeClass: 'active',
        priority: 10,
        isEnabled: () => {
            const actionPage = document.getElementById('actionPage');
            const title = document.getElementById('actionTitle');
            if (!actionPage || actionPage.classList.contains('hidden')) return false;
            if (!title) return false;
            return title.textContent.includes('Расписание');
        }
    };

    // Свайп работает ТОЛЬКО в расписании
    initSwipeTabs({ contexts: [scheduleContext] });

    // ТАП ПО ЭКРАНУ (переключение периодов) тоже оставляем ТОЛЬКО для расписания
    initTapPeriod({
        container: document.body,
        excludeSelectors: ['.league-pill', '.period-pill', 'input', 'button', 'a', 'select', 'textarea'],
        isEnabled: () => {
            // Включаем тап только если мы в расписании, а не на главной
            const actionPage = document.getElementById('actionPage');
            const title = document.getElementById('actionTitle');
            if (!actionPage || actionPage.classList.contains('hidden')) return false;
            if (!title) return false;
            return title.textContent.includes('Расписание');
        },
        getCurrent: () => state.currentPeriod || 'week',
        setPeriod: (period) => window.switchPeriod(period)
    });

    const { initKeyboardFix } = await import('/js/modules/keyboard-fix.js');
    initKeyboardFix();

    await ensureMinBootTime();
    hint('Почти готово…');
    await sleep(300);
    hideBoot();
}

async function boot() {
    hint('Проверяем сессию…');
    setTimeout(() => hint('Загружаем данные…'), 1200);

    initShareButton();

    const me = await fetchMeWithRetry();

    if (!me) {
        const elapsed = Date.now() - __bootStartedAt;
        if (elapsed < 800) await sleep(800 - elapsed);
        showAuth();
        const { bindLoginUI } = await import('/js/login.js');
        bindLoginUI();
        return;
    }

    try {
        await startApp();
    } catch (e) {
        console.error('Boot error:', e);
        await ensureMinBootTime();
        hideBoot();
    }
}

document.addEventListener('DOMContentLoaded', boot);