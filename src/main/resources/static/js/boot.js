// js/boot.js — единственная точка входа SPA

const AUTH_RETRY_DELAYS = [300, 600, 900];
const sleep = (ms) => new Promise(r => setTimeout(r, ms));

// Минимальное время показа заставки — чтобы юзер успел насладиться
const MIN_BOOT_TIME = 3500;

// Запомним время старта
const __bootStartedAt = Date.now();

const hint = (text) => {
    const el = document.getElementById('bootHint');
    if (el) el.textContent = text;
};

/**
 * Ждёт, чтобы общее время показа заставки было >= MIN_BOOT_TIME.
 * Вызывать перед hideBoot().
 */
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

async function startApp() {
    // 1. Показываем разметку
    showApp();

    // 2. Каркас
    const { initRoleCheck } = await import('/js/modules/role-check.js');
    const { initSidebar }   = await import('/js/sidebar.js');
    const { initBottomTab } = await import('/js/modules/bottom-tab.js');

    await initRoleCheck();
    await initSidebar('home');
    initBottomTab('home');

    // 3. ВСЕ модули, которые вешают window.load* — ДО app.init()
    await import('/js/modules/top.js');                 // window.loadTopWeek
    await import('/js/modules/subscription-block.js');  // window.checkSubscription
    await import('/js/modules/live.js');                // window.loadLive
    await import('/js/modules/live-tournament.js');     // window.loadTournamentData

    // compare-app: init() → initSettingsSheet() + initTooltips()
    const { init: initCompare } = await import('/js/compare/compare-app.js');
    initCompare();                                       // ← фикс: шестерёнка H2H

    await import('/js/profile-app.js');                 // window.initProfileApp
    await import('/js/analytics-app.js');               // window.initAnalyticsApp
    await import('/js/modules/scheduled-report.js');    // window.openScheduledReports и т.д.
    await import('/js/show-settings-page.js');          // window.showSettingsPage

    // 4. app.js — определяет window.loadDashboardWidgets, showAction и т.д.
    //    Внутри app.init() вызывается initDashboardRouter → showHome() → window.loadTopWeek()
    //    Поэтому app.init() — ПОСЛЕДНИМ после всех window.load*
    const app = await import('/js/app.js');
    await app.init();

    // 5. Звёзды
    const { initStars } = await import('/js/modules/stars.js');
    initStars();

    // 6. Темы
    const { initThemePicker, toggleThemeSheet, setTheme } = await import('/js/modules/theme-picker.js');
    initThemePicker();
    window.toggleThemeSheet = toggleThemeSheet;
    window.setTheme = setTheme;

    // 7. Свайпы + тапы
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

    const hallOfFameContext = {
        container: document.body,
        tabSelector: '.league-pill',
        activeClass: 'active',
        priority: 5,
        isEnabled: () => {
            const panel = document.getElementById('topWeekPanel');
            const homePage = document.getElementById('homePage');
            return panel && panel.querySelector('.league-pill')
                && homePage && !homePage.classList.contains('hidden');
        }
    };

    initSwipeTabs({ contexts: [scheduleContext, hallOfFameContext] });

    initTapPeriod({
        container: document.body,
        excludeSelectors: ['.league-pill', '.period-pill', 'input', 'button', 'a', 'select', 'textarea'],
        isEnabled: () => {
            const panel = document.getElementById('topWeekPanel');
            const homePage = document.getElementById('homePage');
            return panel && panel.querySelector('.period-pill')
                && homePage && !homePage.classList.contains('hidden');
        },
        getCurrent: () => state.currentPeriod || 'week',
        setPeriod: (period) => window.switchPeriod(period)
    });

    // 8. Ждём минимальное время показа заставки
    await ensureMinBootTime();
    hint('Почти готово…');
    await sleep(300);
    hideBoot();
}

async function boot() {
    // Расписание текстов — независимо от того, как быстро грузится
    hint('Проверяем сессию…');
    setTimeout(() => hint('Загружаем данные…'), 1200);

    const me = await fetchMeWithRetry();

    if (!me) {
        // Для формы логина — минимальное время не нужно (юзер сам решает)
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