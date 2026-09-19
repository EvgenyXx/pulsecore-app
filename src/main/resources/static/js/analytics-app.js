import { AnalyticsAPI } from './core/analytics-api.js';
import { state } from './core/state.js';
import { loadLeagueAvg } from './modules/analytics-league.js';
import { initMonthlyYear, loadMonthly, prevMonthlyYear, nextMonthlyYear, onYearChange } from './modules/analytics-monthly.js';
import { initDailyMonth, loadDaily, prevDailyMonth, nextDailyMonth } from './modules/analytics-daily.js';
import { loadBestTime, setBestTimePeriod } from './modules/analytics-best-time.js';
import { subBlockHtml } from './modules/subscription-block.js';

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

        // Проверяем подписку через статус 402 на защищённом эндпоинте
        const testRes = await fetch('/api/tournament/analytics', { credentials: 'same-origin' });

        if (testRes.status === 402) {
            document.querySelectorAll('#analyticsPage > *').forEach(el => {
                if (el.id !== 'analyticsNoSub') el.style.display = 'none';
            });
            const noSubBlock = document.getElementById('analyticsNoSub');
            noSubBlock.innerHTML = subBlockHtml();
            noSubBlock.classList.remove('hidden');
            return;
        }

        // Показываем всё обратно
        document.querySelectorAll('#analyticsPage > *').forEach(el => {
            el.style.display = '';
        });
        document.getElementById('analyticsNoSub').classList.add('hidden');

        flatpickr('#bestTimeStart', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });
        flatpickr('#bestTimeEnd', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });

        initDailyMonth();
        initMonthlyYear();
        populateYears();
        switchTab('league');
    } catch (e) {
        document.getElementById('analyticsLoading').innerHTML = '<p class="text-red-400">❌ Ошибка</p>';
    }
}

window.initAnalyticsApp = init;

if (document.getElementById('analyticsPage')) {
    document.addEventListener('DOMContentLoaded', init);
}