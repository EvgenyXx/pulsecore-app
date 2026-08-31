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

const subBlockHtml = () => `
    <div class="flex items-center justify-between mb-4">
        <div></div>
        <button onclick="toggleMobileMenu()" class="md:hidden w-9 h-9 flex items-center justify-center rounded-lg bg-white/5 hover:bg-white/10 active:scale-90 text-white">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
        </button>
    </div>
    <div class="apple-card p-8 text-center" style="animation: fadeIn 0.2s ease">
        <div class="w-14 h-14 rounded-full bg-indigo-500/10 flex items-center justify-center mx-auto mb-4">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
        </div>
        <h3 class="text-lg font-bold text-white mb-2">Требуется подписка</h3>
        <p class="text-zinc-400 text-sm mb-4">Оформите подписку чтобы открыть все функции</p>
        <a href="/subscribe" class="inline-block bg-indigo-600 hover:bg-indigo-500 text-white font-semibold rounded-full px-6 py-3 text-sm transition-all">Оформить подписку</a>
    </div>
`;

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
            // Скрываем ВСЁ внутри analyticsPage кроме analyticsNoSub
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
        initSwipes();
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