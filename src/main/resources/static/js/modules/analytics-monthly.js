import { AnalyticsAPI } from '../core/analytics-api.js';
import { formatMoney } from '../core/utils.js';
import { externalTooltipHandler } from './chart-tooltip.js';

let monthlyChart = null;
export let monthlyYear = new Date().getFullYear();

export function initMonthlyYear() { monthlyYear = new Date().getFullYear(); document.getElementById('yearSelect').value = monthlyYear; }
export function prevMonthlyYear() { monthlyYear--; document.getElementById('yearSelect').value = monthlyYear; loadMonthly(); }
export function nextMonthlyYear() { monthlyYear++; document.getElementById('yearSelect').value = monthlyYear; loadMonthly(); }
export function onYearChange() { monthlyYear = parseInt(document.getElementById('yearSelect').value); loadMonthly(); }

// Плагин свечения
const glowPlugin = {
    id: 'monthlyGlow',
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
    id: 'monthlyCrosshair',
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

const monthNamesFull = ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'];
const monthNamesShort = ['Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек'];

export function buildMonthlyChart(data, overallAvg) {
    if (!data?.length) return;
    if (monthlyChart) monthlyChart.destroy();

    const canvas = document.getElementById('monthlyChart');
    const wrap = canvas.parentElement;
    const ctx = canvas.getContext('2d');

    // Сортируем по номеру месяца
    const sorted = [...data].sort((a, b) => {
        const ma = parseInt(a.month.split('-')[1]);
        const mb = parseInt(b.month.split('-')[1]);
        return ma - mb;
    });

    const monthNumbers = sorted.map(m => parseInt(m.month.split('-')[1]));
    const labels = monthNumbers.map(n => monthNamesShort[n - 1]);
    const values = sorted.map(m => Math.round(m.total));
    const isMobile = window.innerWidth < 768;

    // Ширина canvas — на мобилке 50px/месяц, на десктопе по контейнеру
    const pxPerMonth = isMobile ? 55 : 70;
    const canvasWidth = Math.max(labels.length * pxPerMonth, wrap.clientWidth);

    canvas.style.width = canvasWidth + 'px';
    canvas.style.height = '100%';
    canvas.width = canvasWidth;
    canvas.height = wrap.clientHeight;

    // Градиент под линией
    const areaGrad = ctx.createLinearGradient(0, 0, 0, 380);
    areaGrad.addColorStop(0, 'rgba(129, 140, 248, 0.5)');
    areaGrad.addColorStop(0.6, 'rgba(99, 102, 241, 0.12)');
    areaGrad.addColorStop(1, 'rgba(79, 70, 229, 0)');

    monthlyChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                data: values,
                borderColor: '#a5b4fc',
                borderWidth: 3,
                fill: true,
                backgroundColor: areaGrad,
                tension: 0.4,
                pointRadius: isMobile ? 5 : 7,
                pointHoverRadius: isMobile ? 8 : 10,
                pointBackgroundColor: '#a5b4fc',
                pointBorderColor: '#fff',
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
                        title: (ctx) => {
                            const idx = ctx[0].dataIndex;
                            return monthNamesFull[monthNumbers[idx] - 1];
                        },
                        label: (ctx) => {
                            const v = ctx.raw;
                            if (v === 0) return 'Нет дохода';
                            return formatMoney(v);
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
                        font: { size: isMobile ? 12 : 14, weight: '600' },
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

    // Автоскролл к последнему заполненному месяцу
    const lastFilled = values.map((v, i) => v > 0 ? i : -1).filter(i => i >= 0).pop();
    if (lastFilled !== undefined && lastFilled >= 0) {
        const targetX = lastFilled * pxPerMonth;
        wrap.scrollLeft = Math.max(0, targetX - wrap.clientWidth / 2);
    }

    document.getElementById('monthlyAvgPill').style.display = 'inline-flex';
    document.getElementById('monthlyAvg').textContent = formatMoney(overallAvg);
}

export async function loadMonthly() {
    const year = monthlyYear;
    document.getElementById('monthlyAvgPill').style.display = 'none';
    document.getElementById('monthlyNoData').classList.add('hidden');
    try {
        const data = await AnalyticsAPI.getMonthly(year);
        if (!data.months || data.months.length === 0 || data.months.every(m => m.total === 0)) {
            if (monthlyChart) { monthlyChart.destroy(); monthlyChart = null; }
            document.getElementById('monthlyNoData').classList.remove('hidden');
        } else {
            buildMonthlyChart(data.months, data.overallAverage);
        }
    } catch (e) { document.getElementById('monthlyNoData').classList.remove('hidden'); }
}