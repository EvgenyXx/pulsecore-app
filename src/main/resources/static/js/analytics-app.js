import { AnalyticsAPI, checkSubscription } from './core/analytics-api.js';
import { state } from './core/state.js';
import { loadLeagueAvg } from './modules/analytics-league.js';
import { initMonthlyYear, loadMonthly, prevMonthlyYear, nextMonthlyYear, onYearChange } from './modules/analytics-monthly.js';
import { initDailyMonth, loadDaily, prevDailyMonth, nextDailyMonth } from './modules/analytics-daily.js';
import { loadBestTime, setBestTimePeriod } from './modules/analytics-best-time.js';

window.switchTab = switchTab;
window.prevDailyMonth = prevDailyMonth;
window.nextDailyMonth = nextDailyMonth;
window.prevMonthlyYear = prevMonthlyYear;
window.nextMonthlyYear = nextMonthlyYear;
window.onYearChange = onYearChange;
window.setBestTimePeriod = setBestTimePeriod;
window.toggleAnalyticsSheet = toggleAnalyticsSheet;

function updateAnalyticsSlider(tab) {
    const slider = document.querySelector('.analytics-slider');
    if (!slider) return;
    const positions = { 'league': 0, 'monthly': 1, 'daily': 2, 'best-time': 3 };
    slider.className = `analytics-slider pos-${positions[tab] ?? 0}`;
}

function updateAnalyticsTabs(tab) {
    document.querySelectorAll('.analytics-tab').forEach(btn => {
        btn.classList.remove('active');
    });
    const activeBtn = document.querySelector(`.analytics-tab[onclick*="${tab}"]`);
    if (activeBtn) activeBtn.classList.add('active');
}

function switchTab(tab) {
    // Убираем active ТОЛЬКО у вкладок аналитики, не трогаем сайдбар
    document.querySelectorAll('.analytics-tab').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-sheet-btn').forEach(btn => btn.classList.remove('active'));

    document.getElementById('nav-' + tab)?.classList.add('active');
    document.getElementById('sheet-' + tab)?.classList.add('active');

    document.getElementById('tab-league').classList.toggle('hidden', tab !== 'league');
    document.getElementById('tab-monthly').classList.toggle('hidden', tab !== 'monthly');
    document.getElementById('tab-daily').classList.toggle('hidden', tab !== 'daily');
    document.getElementById('tab-best-time').classList.toggle('hidden', tab !== 'best-time');

    updateAnalyticsSlider(tab);
    updateAnalyticsTabs(tab);

    if (tab === 'league') loadLeagueAvg();
    if (tab === 'monthly') { initMonthlyYear(); loadMonthly(); }
    if (tab === 'daily') { initDailyMonth(); loadDaily(); }
    if (tab === 'best-time') loadBestTime();
    updateAnalyticsSheet();
}

function toggleAnalyticsSheet() {
    const overlay = document.getElementById('analyticsSheetOverlay');
    if (overlay) overlay.classList.toggle('open');
}

function updateAnalyticsSheet() {
    const activeTab = ['league', 'monthly', 'daily', 'best-time'].find(t => !document.getElementById('tab-' + t).classList.contains('hidden'));
    document.querySelectorAll('.analytics-sheet-btn, .tab-sheet-btn').forEach(b => b.classList.remove('active'));
    if (activeTab) {
        document.getElementById('sheet-' + activeTab)?.classList.add('active');
    }
}

function initSwipes() {
    const dA = document.getElementById('dailyChartCard');
    if (dA) {
        let sx = 0;
        dA.addEventListener('touchstart', e => { sx = e.touches[0].clientX; }, { passive: true });
        dA.addEventListener('touchend', e => {
            if (!sx) return;
            const dx = e.changedTouches[0].clientX - sx;
            if (Math.abs(dx) > 35) { if (dx > 0) prevDailyMonth(); else nextDailyMonth(); }
            sx = 0;
        });
    }
    const mA = document.getElementById('monthlyChartCard');
    if (mA) {
        let sx = 0;
        mA.addEventListener('touchstart', e => { sx = e.touches[0].clientX; }, { passive: true });
        mA.addEventListener('touchend', e => {
            if (!sx) return;
            const dx = e.changedTouches[0].clientX - sx;
            if (Math.abs(dx) > 35) { if (dx > 0) prevMonthlyYear(); else nextMonthlyYear(); }
            sx = 0;
        });
    }
}

function populateYears() {
    const s = document.getElementById('yearSelect');
    if (!s) return;
    s.innerHTML = '';
    const currentYear = new Date().getFullYear();
    for (let i = currentYear; i >= 2025; i--) {
        const o = document.createElement('option');
        o.value = i;
        o.textContent = i;
        if (i === currentYear) o.selected = true;
        s.appendChild(o);
    }
}

async function init() {
    try {
        const user = await AnalyticsAPI.getMe();
        if (!user || !user.id) { window.location.href = '/'; return; }
        state.playerId = user.id;

        const hasSub = await checkSubscription();
        if (!hasSub) {
            document.getElementById('analyticsLoading').classList.add('hidden');
            document.getElementById('analyticsNoSub').classList.remove('hidden');
            return;
        }

        document.getElementById('analyticsLoading').classList.add('hidden');

        flatpickr('#bestTimeStart', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });
        flatpickr('#bestTimeEnd', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });

        initDailyMonth();
        initMonthlyYear();
        initSwipes();
        populateYears();
        switchTab('league');
    } catch (e) {
        document.getElementById('analyticsLoading').innerHTML = '<p class="text-red-400">❌ Ошибка</p>';
    }
}

// Экспортируем для роутера
window.initAnalyticsApp = init;

// Для отдельной страницы analytics.html
if (document.getElementById('analyticsPage')) {
    document.addEventListener('DOMContentLoaded', init);
}