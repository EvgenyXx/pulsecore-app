import { AnalyticsAPI } from '../core/analytics-api.js';
import { formatMoney } from '../core/utils.js';

let monthlyChart = null;
export let monthlyYear = new Date().getFullYear();

export function initMonthlyYear() { monthlyYear = new Date().getFullYear(); document.getElementById('yearSelect').value = monthlyYear; }
export function prevMonthlyYear() { monthlyYear--; document.getElementById('yearSelect').value = monthlyYear; loadMonthly(); }
export function nextMonthlyYear() { monthlyYear++; document.getElementById('yearSelect').value = monthlyYear; loadMonthly(); }
export function onYearChange() { monthlyYear = parseInt(document.getElementById('yearSelect').value); loadMonthly(); }

export function buildMonthlyChart(data, overallAvg) {
    if (!data?.length) return;
    if (monthlyChart) monthlyChart.destroy();

    const ctx = document.getElementById('monthlyChart').getContext('2d');
    const months = ['Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек'];
    const labels = data.map(m => months[parseInt(m.month.split('-')[1]) - 1]);
    const values = data.map(m => Math.round(m.total));

    const bgGradients = values.map(() => {
        const grad = ctx.createLinearGradient(0, 0, 0, 350);
        grad.addColorStop(0, '#818cf8');
        grad.addColorStop(1, '#4f46e5');
        return grad;
    });

    monthlyChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: bgGradients,
                borderRadius: { topLeft: 14, topRight: 14, bottomLeft: 6, bottomRight: 6 },
                borderSkipped: false,
                barPercentage: 0.5,
                categoryPercentage: 0.7
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 1200, easing: 'easeOutQuart', delay: (ctx) => ctx.index * 50 },
            plugins: { legend: { display: false }, tooltip: { enabled: false } },
            scales: {
                y: {
                    beginAtZero: true,
                    max: Math.max(...values, 1) * 1.35,
                    grid: { color: 'rgba(255,255,255,0.06)', drawBorder: false, lineWidth: 1 },
                    ticks: { color: '#a1a1aa', font: { size: 11 }, padding: 10, callback: v => formatMoney(v) },
                    border: { display: false }
                },
                x: {
                    grid: { display: false },
                    ticks: { color: '#e5e5e5', font: { weight: '600', size: 12, family: 'Inter' }, padding: 10 },
                    border: { display: false }
                }
            }
        },
        plugins: [{
            id: 'valueLabels',
            afterDatasetsDraw(chart) {
                const { ctx } = chart;
                const meta = chart.getDatasetMeta(0);
                meta.data.forEach((bar, i) => {
                    if (values[i] === 0) return;
                    const x = bar.x, y = bar.y;
                    const lineY = y - 40;

                    ctx.strokeStyle = '#a5b4fc';
                    ctx.lineWidth = 2;
                    ctx.setLineDash([4, 4]);
                    ctx.beginPath();
                    ctx.moveTo(x, y - 10);
                    ctx.lineTo(x, lineY);
                    ctx.stroke();
                    ctx.setLineDash([]);

                    ctx.fillStyle = '#c4b5fc';
                    ctx.shadowColor = '#a5b4fc';
                    ctx.shadowBlur = 14;
                    ctx.beginPath();
                    ctx.arc(x, y - 10, 5, 0, Math.PI * 2);
                    ctx.fill();
                    ctx.shadowBlur = 0;

                    const text = formatMoney(values[i]);
                    const textW = ctx.measureText(text).width + 24;
                    const textH = 28;
                    const textX = x - textW / 2;
                    const textY = lineY - textH - 10;

                    ctx.fillStyle = 'rgba(0,0,0,0.6)';
                    ctx.beginPath();
                    ctx.roundRect(textX + 2, textY + 2, textW, textH, 10);
                    ctx.fill();

                    ctx.fillStyle = '#18181b';
                    ctx.beginPath();
                    ctx.roundRect(textX, textY, textW, textH, 10);
                    ctx.fill();

                    ctx.strokeStyle = '#a5b4fc';
                    ctx.lineWidth = 2;
                    ctx.stroke();

                    ctx.fillStyle = '#fff';
                    ctx.font = '700 13px Inter';
                    ctx.textAlign = 'center';
                    ctx.fillText(text, x, textY + 20);
                });
            }
        }]
    });

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