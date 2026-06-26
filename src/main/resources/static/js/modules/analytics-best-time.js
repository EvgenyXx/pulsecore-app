import { AnalyticsAPI } from '../core/analytics-api.js';
import { formatMoney } from '../core/utils.js';

let bestTimeChart = null;
export let bestTimePeriod = 'all';

export function setBestTimePeriod(period) {
    bestTimePeriod = period;
    document.querySelectorAll('#tab-best-time .period-btn').forEach(b => b.classList.remove('active'));

    if (period === 'custom') {
        // Ничего не подсвечиваем, оставляем кнопки неактивными
    } else {
        const btn = document.getElementById('bt-' + period);
        if (btn) btn.classList.add('active');
    }

    loadBestTime();
}

export async function loadBestTime() {
    try {
        const params = {};
        const now = new Date();

        if (bestTimePeriod === 'week') {
            const d = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
            params.start = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
            params.end = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0') + '-' + String(now.getDate()).padStart(2, '0');
        } else if (bestTimePeriod === 'month') {
            const d = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
            params.start = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
            params.end = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0') + '-' + String(now.getDate()).padStart(2, '0');
        } else if (bestTimePeriod === 'custom') {
            const start = document.getElementById('bestTimeStart')?.value;
            const end = document.getElementById('bestTimeEnd')?.value;
            if (start) params.start = start;
            if (end) params.end = end;
        }

        const data = await AnalyticsAPI.getBestTime(params);

        if (!data || data.length === 0) {
            document.getElementById('bestTimeTableBody').innerHTML = '<tr><td colspan="4" class="text-center text-zinc-500 py-4">Нет данных</td></tr>';
            if (bestTimeChart) { bestTimeChart.destroy(); bestTimeChart = null; }
            return;
        }

        document.getElementById('bestTimeTableBody').innerHTML = data.map((r, i) => {
            const isBest = i === 0;
            return `<tr>
                <td class="${isBest ? 'highlight' : 'text-zinc-300'}">${r.time || '—'}</td>
                <td class="text-zinc-400">${r.gamesCount}</td>
                <td class="text-zinc-300">${formatMoney(r.avgPoints)}</td>
                <td class="${isBest ? 'highlight' : 'text-zinc-300'}">${formatMoney(r.totalPoints)}</td>
            </tr>`;
        }).join('');

        if (bestTimeChart) bestTimeChart.destroy();
        const ctx = document.getElementById('bestTimeChart').getContext('2d');
        const labels = data.map(r => r.time || '—');
        const scores = data.map(r => r.avgPoints != null ? Math.round(r.avgPoints) : 0);
        const bgColors = labels.map((_, i) => i === 0 ? '#818cf8' : 'rgba(255,255,255,0.06)');

        bestTimeChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels,
                datasets: [{
                    data: scores,
                    backgroundColor: bgColors,
                    borderRadius: 12,
                    borderSkipped: false,
                    barPercentage: 0.5,
                    categoryPercentage: 0.7
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: { duration: 800, easing: 'easeOutQuart' },
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
                        callbacks: { label: (ctx) => ' ' + formatMoney(ctx.raw) }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: Math.max(...scores, 1) * 1.3,
                        grid: { color: 'rgba(255,255,255,0.04)', drawBorder: false, lineWidth: 1 },
                        ticks: { color: '#a1a1aa', font: { size: 10 }, padding: 8 },
                        border: { display: false }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: '#e5e5e5', font: { weight: '600', size: 13, family: 'Inter' }, padding: 10 },
                        border: { display: false }
                    }
                }
            }
        });

    } catch (e) {
        document.getElementById('bestTimeTableBody').innerHTML = '<tr><td colspan="4" class="text-center text-red-400 py-4">Ошибка загрузки</td></tr>';
    }
}