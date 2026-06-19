import { AnalyticsAPI } from '../core/analytics-api.js';
import { formatMoney } from '../core/utils.js';

let dailyChart = null;
export let dailyYear, dailyMonth;
const monthNames = ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'];

export function initDailyMonth() { const now = new Date(); dailyYear = now.getFullYear(); dailyMonth = now.getMonth() + 1; }
export function updateDailyLabel() { document.getElementById('dailyMonthLabel').textContent = monthNames[dailyMonth - 1] + ' ' + dailyYear; }
export function prevDailyMonth() { dailyMonth--; if (dailyMonth < 1) { dailyMonth = 12; dailyYear--; } updateDailyLabel(); loadDaily(); }
export function nextDailyMonth() { dailyMonth++; if (dailyMonth > 12) { dailyMonth = 1; dailyYear++; } updateDailyLabel(); loadDaily(); }

export function buildDailyChart(data) {
    if (!data?.days?.length) return;
    if (dailyChart) dailyChart.destroy();

    const ctx = document.getElementById('dailyChart').getContext('2d');
    const labels = data.days.map(d => d.day);
    const values = data.days.map(d => Math.round(d.total));
    const isMobile = window.innerWidth < 768;

    const bgGradients = values.map(v => {
        const grad = ctx.createLinearGradient(0, 0, 0, 350);
        if (v > 0) {
            grad.addColorStop(0, '#818cf8');
            grad.addColorStop(1, '#4f46e5');
        } else {
            grad.addColorStop(0, 'rgba(255,255,255,0.03)');
            grad.addColorStop(1, 'rgba(255,255,255,0.01)');
        }
        return grad;
    });

    dailyChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: bgGradients,
                borderRadius: isMobile ? 4 : 6,
                borderSkipped: false,
                barPercentage: isMobile ? 0.9 : 0.8,
                categoryPercentage: isMobile ? 0.95 : 0.85
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 600, easing: 'easeOutQuart' },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#18181b',
                    titleColor: '#e5e5e5',
                    bodyColor: '#a5b4fc',
                    borderColor: 'rgba(165,180,252,0.4)',
                    borderWidth: 1,
                    padding: 12,
                    cornerRadius: 10,
                    callbacks: { label: (ctx) => ctx.raw > 0 ? ' ' + formatMoney(ctx.raw) : ' Нет дохода' }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: Math.max(...values, 1) * 1.3,
                    grid: { color: 'rgba(255,255,255,0.04)', drawBorder: false, lineWidth: 1 },
                    ticks: { color: '#a1a1aa', font: { size: isMobile ? 8 : 10 }, padding: 6, callback: v => formatMoney(v) },
                    border: { display: false }
                },
                x: {
                    grid: { display: false },
                    ticks: { color: '#71717a', font: { size: isMobile ? 8 : 10 }, padding: 0, maxRotation: 0, autoSkip: true },
                    border: { display: false }
                }
            }
        }
    });
}

export async function loadDaily() {
    updateDailyLabel();
    document.getElementById('dailyNoData').classList.add('hidden');
    document.getElementById('dailyTotal').textContent = '...';
    document.getElementById('dailyAvg').textContent = '...';
    document.getElementById('dailyBest').textContent = '...';
    try {
        const data = await AnalyticsAPI.getDaily(dailyYear, dailyMonth);
        if (!data.days || data.days.length === 0 || data.days.every(day => day.total === 0)) {
            if (dailyChart) { dailyChart.destroy(); dailyChart = null; }
            document.getElementById('dailyNoData').classList.remove('hidden');
            document.getElementById('dailyTotal').textContent = '0 ₽';
            document.getElementById('dailyAvg').textContent = '0 ₽';
            document.getElementById('dailyBest').textContent = '—';
        } else {
            buildDailyChart(data);
            document.getElementById('dailyTotal').textContent = formatMoney(data.monthTotal);
            document.getElementById('dailyAvg').textContent = formatMoney(data.dailyAverage);
            const best = data.days.reduce((max, x) => x.total > max.total ? x : max, { day: 0, total: 0 });
            document.getElementById('dailyBest').textContent = best.total > 0 ? `${best.day} числа — ${formatMoney(best.total)}` : '—';
        }
    } catch (e) {
        document.getElementById('dailyNoData').classList.remove('hidden');
    }
}