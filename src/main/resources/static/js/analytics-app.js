import { AnalyticsAPI } from './core/analytics-api.js';
import { state } from './core/state.js';
import { loadLeagueAvg } from './modules/analytics-league.js';
import { initMonthlyYear, loadMonthly, prevMonthlyYear, nextMonthlyYear, onYearChange } from './modules/analytics-monthly.js';
import { initDailyMonth, loadDaily, prevDailyMonth, nextDailyMonth } from './modules/analytics-daily.js';
import { loadBestTime, setBestTimePeriod } from './modules/analytics-best-time.js';

// Глобальные функции
window.switchTab = switchTab;
window.prevDailyMonth = prevDailyMonth;
window.nextDailyMonth = nextDailyMonth;
window.prevMonthlyYear = prevMonthlyYear;
window.nextMonthlyYear = nextMonthlyYear;
window.onYearChange = onYearChange;
window.setBestTimePeriod = setBestTimePeriod;

function switchTab(tab) {
    document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.mobile-nav-item').forEach(el => el.classList.remove('active'));
    document.getElementById('nav-' + tab)?.classList.add('active');
    document.getElementById('mob-nav-' + tab)?.classList.add('active');
    document.getElementById('tab-league').classList.toggle('hidden', tab !== 'league');
    document.getElementById('tab-monthly').classList.toggle('hidden', tab !== 'monthly');
    document.getElementById('tab-daily').classList.toggle('hidden', tab !== 'daily');
    document.getElementById('tab-best-time').classList.toggle('hidden', tab !== 'best-time');
    if (tab === 'league') loadLeagueAvg();
    if (tab === 'monthly') { initMonthlyYear(); loadMonthly(); }
    if (tab === 'daily') { initDailyMonth(); loadDaily(); }
    if (tab === 'best-time') loadBestTime();
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

        // Сохраняем playerId в state для использования в API
        state.playerId = user.id;

        const sub = await AnalyticsAPI.getSubscription();
        if (!sub || !sub.active) {
            document.getElementById('loading').innerHTML = '';
            document.getElementById('loading').classList.add('hidden');
            document.querySelector('main').innerHTML = `
                <div class="glass rounded-2xl p-8 text-center max-w-md mx-auto mt-20">
                    <div class="text-5xl mb-4">🔒</div>
                    <h3 class="text-xl font-bold text-amber-400 mb-2">Требуется подписка</h3>
                    <p class="text-gray-400 text-sm mb-6">Оформите подписку чтобы открыть аналитику</p>
                    <a href="/subscribe" class="inline-block bg-amber-500 hover:bg-amber-400 text-black font-semibold px-8 py-3 rounded-xl">💳 Оформить подписку</a>
                    <br><a href="/dashboard" class="text-xs text-gray-500 mt-4 inline-block">← На главную</a>
                </div>`;
            return;
        }

        document.getElementById('loading').classList.add('hidden');

        flatpickr('#bestTimeStart', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });
        flatpickr('#bestTimeEnd', { locale: 'ru', dateFormat: 'Y-m-d', maxDate: 'today' });

        initDailyMonth();
        initMonthlyYear();
        initSwipes();
        populateYears();
        switchTab('league');
    } catch (e) {
        document.getElementById('loading').innerHTML = '<p class="text-red-400">❌ Ошибка</p>';
    }
}

document.addEventListener('DOMContentLoaded', init);