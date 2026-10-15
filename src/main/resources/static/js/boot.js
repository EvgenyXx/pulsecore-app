// js/boot.js — единственная точка входа SPA

const AUTH_RETRY_DELAYS = [300, 600, 900];
const sleep = (ms) => new Promise(r => setTimeout(r, ms));

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
    document.getElementById('bootOverlay')?.classList.add('hidden');
}

function showAuth() {
    hideBoot();
    document.getElementById('authScreen')?.classList.add('visible');
}

function showApp() {
    const root = document.getElementById('appRoot');
    if (root) root.style.display = '';
}

async function startApp() {
    // 1. Разметка уже в DOM — показываем
    showApp();

    // 2. Каркас
    const { initRoleCheck } = await import('/js/modules/role-check.js');
    const { initSidebar }   = await import('/js/sidebar.js');
    const { initBottomTab } = await import('/js/modules/bottom-tab.js');

    await initRoleCheck();
    await initSidebar('home');
    initBottomTab('home');

    // 3. app.js — грузит данные и рендерит дашборд
    const app = await import('/js/app.js');
    await app.init();

    // 4. Остальные модули — импорт + явный init
    await import('/js/modules/top.js');
    await import('/js/modules/subscription-block.js');
    await import('/js/modules/live.js');

    const liveTournament = await import('/js/modules/live-tournament.js');
    if (liveTournament.init) liveTournament.init();

    await import('/js/compare/compare-app.js');
    const compareApp = await import('/js/compare/compare-app.js');
    if (compareApp.init) compareApp.init();

    const profileApp = await import('/js/profile-app.js');
    if (profileApp.init) await profileApp.init();

    const analyticsApp = await import('/js/analytics-app.js');
    if (analyticsApp.init) await analyticsApp.init();

    // 5. Звёзды
    const { initStars } = await import('/js/modules/stars.js');
    initStars();

    // 6. Темы
    const { initThemePicker, toggleThemeSheet, setTheme } = await import('/js/modules/theme-picker.js');
    initThemePicker();
    window.toggleThemeSheet = toggleThemeSheet;
    window.setTheme = setTheme;

    // 7. Отчёты
    await import('/js/modules/scheduled-report.js');

    // 8. showSettingsPage — глобально (нужно для inline onclick в профиле)
    await import('/js/show-settings-page.js');

    // 9. Свайпы + тапы
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

    // 10. Всё готово — убираем спиннер
    hideBoot();
}

async function boot() {
    const me = await fetchMeWithRetry();

    if (!me) {
        showAuth();
        const { bindLoginUI } = await import('/js/login.js');
        bindLoginUI();
        return;
    }

    try {
        await startApp();
    } catch (e) {
        console.error('Boot error:', e);
        hideBoot();
    }
}

document.addEventListener('DOMContentLoaded', boot);