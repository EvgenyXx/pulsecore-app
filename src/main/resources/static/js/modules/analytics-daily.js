import { AnalyticsAPI } from '../core/analytics-api.js';
import { formatMoney } from '../core/utils.js';
import { externalTooltipHandler } from './chart-tooltip.js';

let dailyChart = null;
export let dailyYear, dailyMonth;
const monthNames = ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'];

export function initDailyMonth() { const now = new Date(); dailyYear = now.getFullYear(); dailyMonth = now.getMonth() + 1; }
export function updateDailyLabel() { document.getElementById('dailyMonthLabel').textContent = monthNames[dailyMonth - 1] + ' ' + dailyYear; }
export function prevDailyMonth() { dailyMonth--; if (dailyMonth < 1) { dailyMonth = 12; dailyYear--; } updateDailyLabel(); loadDaily(); }
export function nextDailyMonth() { dailyMonth++; if (dailyMonth > 12) { dailyMonth = 1; dailyYear++; } updateDailyLabel(); loadDaily(); }

// Плагин свечения
const glowPlugin = {
    id: 'dailyGlow',
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
    id: 'dailyCrosshair',
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

export function buildDailyChart(data) {
    if (!data?.days?.length) return;
    if (dailyChart) dailyChart.destroy();

    const canvas = document.getElementById('dailyChart');
    const wrap = document.getElementById('dailyChartWrap');
    const ctx = canvas.getContext('2d');
    const labels = data.days.map(d => d.day);
    const values = data.days.map(d => Math.round(d.total));
    const counts = data.days.map(d => d.count);
    const isMobile = window.innerWidth < 768;

    // Ширина canvas = 40px на день (мобилка) / 50px (десктоп)
    const pxPerDay = isMobile ? 40 : 50;
    const canvasWidth = Math.max(labels.length * pxPerDay, wrap.clientWidth);

    canvas.style.width = canvasWidth + 'px';
    canvas.style.height = '100%';
    canvas.width = canvasWidth;
    canvas.height = wrap.clientHeight;

    // Градиент под линией
    const areaGrad = ctx.createLinearGradient(0, 0, 0, 380);
    areaGrad.addColorStop(0, 'rgba(129, 140, 248, 0.45)');
    areaGrad.addColorStop(0.6, 'rgba(99, 102, 241, 0.12)');
    areaGrad.addColorStop(1, 'rgba(79, 70, 229, 0)');

    dailyChart = new Chart(ctx, {
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
                pointRadius: isMobile ? 3 : 4,
                pointHoverRadius: isMobile ? 6 : 8,
                pointBackgroundColor: '#a5b4fc',
                pointBorderColor: 'rgba(255,255,255,0.9)',
                pointBorderWidth: 2,
                pointHoverBackgroundColor: '#c4b5fc',
                pointHoverBorderColor: '#fff',
                pointHoverBorderWidth: 3
            }]
        },
        options: {
            responsive: false,
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
                        title: (ctx) => `${ctx[0].label} ${monthNames[dailyMonth - 1].toLowerCase()}`,
                        label: (ctx) => {
                            const v = ctx.raw;
                            const c = counts[ctx.dataIndex];
                            if (v === 0) return 'Нет дохода';
                            return `${formatMoney(v)} · турниров: ${c}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: Math.max(...values, 1) * 1.25,
                    grid: { color: 'rgba(255,255,255,0.04)', drawBorder: false, lineWidth: 1 },
                    ticks: {
                        color: '#a1a1aa',
                        font: { size: isMobile ? 11 : 12 },
                        padding: 8,
                        callback: v => formatMoney(v)
                    },
                    border: { display: false }
                },
                x: {
                    grid: { display: false },
                    ticks: {
                        color: '#e5e5e5',
                        font: { size: isMobile ? 11 : 13, weight: '600' },
                        padding: 6,
                        maxRotation: 0,
                        autoSkip: false
                    },
                    border: { display: false }
                }
            }
        },
        plugins: [glowPlugin, crosshairPlugin]
    });

    // Автоскролл к сегодняшнему дню
    const today = new Date();
    if (today.getFullYear() === dailyYear && today.getMonth() + 1 === dailyMonth) {
        const todayX = (today.getDate() - 1) * pxPerDay;
        wrap.scrollLeft = Math.max(0, todayX - wrap.clientWidth / 2);
    }
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