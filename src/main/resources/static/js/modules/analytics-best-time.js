import { AnalyticsAPI } from '../core/analytics-api.js';
import { formatMoney } from '../core/utils.js';
import { externalTooltipHandler } from './chart-tooltip.js';

let bestTimeChart = null;
export let bestTimePeriod = 'all';

export function setBestTimePeriod(period) {
    bestTimePeriod = period;
    document.querySelectorAll('#tab-best-time .period-btn').forEach(b => b.classList.remove('active'));

    if (period === 'custom') {
        // Ничего не подсвечиваем
    } else {
        const btn = document.getElementById('bt-' + period);
        if (btn) btn.classList.add('active');
    }

    loadBestTime();
}

// Плагин свечения
const glowPlugin = {
    id: 'bestTimeGlow',
    beforeDatasetsDraw(chart) {
        const { ctx } = chart;
        ctx.save();
        ctx.shadowColor = 'rgba(129, 140, 248, 0.55)';
        ctx.shadowBlur = 24;
    },
    afterDatasetsDraw(chart) {
        chart.ctx.restore();
    }
};

// Плагин crosshair
const crosshairPlugin = {
    id: 'bestTimeCrosshair',
    afterDatasetsDraw(chart) {
        if (chart.tooltip?._active?.length) {
            const x = chart.tooltip._active[0].element.x;
            const { top, bottom } = chart.chartArea;
            const ctx = chart.ctx;

            ctx.save();
            ctx.beginPath();
            ctx.moveTo(x, top);
            ctx.lineTo(x, bottom);
            ctx.lineWidth = 1;
            ctx.strokeStyle = 'rgba(165, 180, 252, 0.35)';
            ctx.setLineDash([4, 4]);
            ctx.stroke();
            ctx.restore();
        }
    }
};

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

        // Таблица
        document.getElementById('bestTimeTableBody').innerHTML = data.map((r, i) => {
            const isBest = i === 0;
            return `<tr>
                <td class="${isBest ? 'highlight' : 'text-zinc-300'}">${r.time || '—'}</td>
                <td class="text-zinc-400">${r.gamesCount}</td>
                <td class="text-zinc-300">${formatMoney(r.avgPoints)}</td>
                <td class="${isBest ? 'highlight' : 'text-zinc-300'}">${formatMoney(r.totalPoints)}</td>
            </tr>`;
        }).join('');

        // График
        if (bestTimeChart) bestTimeChart.destroy();

        const canvas = document.getElementById('bestTimeChart');
        if (!canvas) return;
        const ctx = canvas.getContext('2d');

        const labels = data.map(r => r.time || '—');
        const values = data.map(r => r.avgPoints != null ? Math.round(r.avgPoints) : 0);
        const counts = data.map(r => r.gamesCount || 0);
        const isMobile = window.innerWidth < 768;

        // Реальный максимум + 8% отступа
        const maxValue = Math.max(...values, 1);
        const yMax = maxValue * 1.08;
        const stepSize = Math.round(maxValue / 4);

        // Градиент под линией
        const areaGrad = ctx.createLinearGradient(0, 0, 0, 380);
        areaGrad.addColorStop(0, 'rgba(129, 140, 248, 0.45)');
        areaGrad.addColorStop(0.6, 'rgba(99, 102, 241, 0.12)');
        areaGrad.addColorStop(1, 'rgba(79, 70, 229, 0)');

        bestTimeChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels,
                datasets: [{
                    data: values,
                    borderColor: '#a5b4fc',
                    borderWidth: 2.5,
                    fill: true,
                    backgroundColor: areaGrad,
                    tension: 0.4,
                    pointRadius: isMobile ? 4 : 5,
                    pointHoverRadius: isMobile ? 7 : 9,
                    pointBackgroundColor: '#a5b4fc',
                    pointBorderColor: 'rgba(255,255,255,0.9)',
                    pointBorderWidth: 2,
                    pointHoverBackgroundColor: '#c4b5fc',
                    pointHoverBorderColor: '#fff',
                    pointHoverBorderWidth: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: { duration: 1200, easing: 'easeOutQuart' },
                interaction: {
                    mode: 'index',
                    intersect: false
                },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        enabled: false,
                        external: externalTooltipHandler,
                        callbacks: {
                            title: (ctx) => `Время: ${ctx[0].label}`,
                            label: (ctx) => {
                                const v = ctx.raw;
                                const c = counts[ctx.dataIndex];
                                if (v === 0) return 'Нет дохода';
                                return `${formatMoney(v)} · игр: ${c}`;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: yMax,
                        grid: { color: 'rgba(255,255,255,0.04)', drawBorder: false, lineWidth: 1 },
                        ticks: {
                            color: '#a1a1aa',
                            font: { size: isMobile ? 11 : 12 },
                            padding: 8,
                            stepSize: stepSize,
                            callback: function(v) {
                                if (v > maxValue) return '';
                                return formatMoney(v);
                            }
                        },
                        border: { display: false }
                    },
                    x: {
                        grid: { display: false },
                        ticks: {
                            color: '#e5e5e5',
                            font: { size: isMobile ? 11 : 13, weight: '600' },
                            padding: 8,
                            maxRotation: 0,
                            autoSkip: false
                        },
                        border: { display: false }
                    }
                }
            },
            plugins: [glowPlugin, crosshairPlugin]
        });

    } catch (e) {
        console.error('Ошибка загрузки лучшего времени:', e);
        document.getElementById('bestTimeTableBody').innerHTML = '<tr><td colspan="4" class="text-center text-red-400 py-4">Ошибка загрузки</td></tr>';
    }
}