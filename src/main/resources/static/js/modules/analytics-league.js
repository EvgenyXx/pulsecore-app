import { AnalyticsAPI } from '../core/analytics-api.js';
import { formatMoney } from '../core/utils.js';
import { externalTooltipHandler } from './chart-tooltip.js';

let leagueChart = null;

// Плагин свечения
const glowPlugin = {
    id: 'leagueGlow',
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
    id: 'leagueCrosshair',
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

export function buildLeagueChart(data) {
    if (!data?.leagueStats?.length) return;
    if (leagueChart) leagueChart.destroy();

    const canvas = document.getElementById('leagueChart');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    if (canvas.tagName !== 'CANVAS') {
        canvas.outerHTML = '<canvas id="leagueChart"></canvas>';
        return buildLeagueChart(data);
    }

    const all = ['A', 'B', 'C', 'D', 'SUPER_LEAGUE'];
    const labels = [], values = [];

    all.forEach(l => {
        const f = data.leagueStats.find(s => s.league === l);
        labels.push(l === 'SUPER_LEAGUE' ? 'СУПЕР' : l);
        values.push(f ? Math.round(f.averageAmount) : 0);
    });

    const isMobile = window.innerWidth < 768;

    // Реальный максимум из данных
    const maxValue = Math.max(...values, 1);

    // Верхняя граница — просто чуть выше максимума (для отступа точки)
    const yMax = maxValue * 1.08;

    // Шаг сетки — 4 части от максимума
    const stepSize = Math.round(maxValue / 4);

    // Градиент под линией
    const areaGrad = ctx.createLinearGradient(0, 0, 0, 380);
    areaGrad.addColorStop(0, 'rgba(129, 140, 248, 0.5)');
    areaGrad.addColorStop(0.6, 'rgba(99, 102, 241, 0.12)');
    areaGrad.addColorStop(1, 'rgba(79, 70, 229, 0)');

    leagueChart = new Chart(ctx, {
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
                pointRadius: isMobile ? 6 : 8,
                pointHoverRadius: isMobile ? 9 : 11,
                pointBackgroundColor: '#a5b4fc',
                pointBorderColor: '#fff',
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
                        title: (ctx) => `Лига ${ctx[0].label}`,
                        label: (ctx) => {
                            if (ctx.raw === 0) return 'Нет данных';
                            return formatMoney(ctx.raw);
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
                            // Скрываем верхнюю метку (которая = maxValue * 1.08)
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
                        font: { weight: '700', size: isMobile ? 13 : 15, family: 'Inter' },
                        padding: isMobile ? 8 : 14
                    },
                    border: { display: false }
                }
            }
        },
        plugins: [glowPlugin, crosshairPlugin]
    });
}

function getLeagueLabel(league) {
    if (!league) return '';
    return league === 'SUPER_LEAGUE' ? 'Супер' : league;
}

export async function loadLeagueAvg() {
    try {
        const data = await AnalyticsAPI.getLeagueAvg();
        document.getElementById('playerAvgPill').textContent = formatMoney(data.playerAverage);

        if (data.closestLeague) {
            const league = data.leagueStats.find(s => s.league === data.closestLeague);
            if (league) {
                const diff = data.playerAverage - league.averageAmount;
                const leagueName = getLeagueLabel(data.closestLeague);

                let infoHtml = `Ваш уровень — <span class="text-indigo-400 font-semibold">лига ${leagueName}</span>. `;

                if (diff >= 0) {
                    infoHtml += `Зарабатываете <span class="text-emerald-400 font-semibold">на ${formatMoney(Math.round(Math.abs(diff)))} больше</span>, чем в среднем в этой лиге.`;
                } else {
                    infoHtml += `Зарабатываете <span class="text-amber-400 font-semibold">на ${formatMoney(Math.round(Math.abs(diff)))} меньше</span>, чем в среднем в этой лиге.`;
                }

                document.getElementById('closestInfo').innerHTML = infoHtml;
            }
        }

        buildLeagueChart(data);
    } catch (e) {
        console.error('Ошибка загрузки аналитики по лигам:', e);
    }
}