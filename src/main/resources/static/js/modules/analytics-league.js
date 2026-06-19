import { AnalyticsAPI } from '../core/analytics-api.js';
import { formatMoney } from '../core/utils.js';

let leagueChart = null;

export function buildLeagueChart(data) {
    if (!data?.leagueStats?.length) return;
    if (leagueChart) leagueChart.destroy();

    const ctx = document.getElementById('leagueChart').getContext('2d');
    const all = ['A', 'B', 'C', 'D', 'SUPER_LEAGUE'];

    const labels = [], values = [];
    all.forEach(l => {
        const f = data.leagueStats.find(s => s.league === l);
        labels.push(l === 'SUPER_LEAGUE' ? 'СУПЕР' : l);
        values.push(f ? Math.round(f.averageAmount) : 0);
    });

    const columnColors = [
        ['#818cf8', '#6366f1'],
        ['#6366f1', '#4f46e5'],
        ['#4f46e5', '#4338ca'],
        ['#4338ca', '#3730a3'],
        ['#8b5cf6', '#7c3aed']
    ];

    const bgGradients = columnColors.map(([top, bottom]) => {
        const grad = ctx.createLinearGradient(0, 0, 0, 350);
        grad.addColorStop(0, top);
        grad.addColorStop(1, bottom);
        return grad;
    });

    leagueChart = new Chart(ctx, {
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
            animation: { duration: 1200, easing: 'easeOutQuart', delay: (ctx) => ctx.index * 80 },
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
                    ticks: { color: '#e5e5e5', font: { weight: '700', size: 15, family: 'Inter' }, padding: 14 },
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
}

function getLeagueLabel(league) {
    if (!league) return '';
    return league === 'SUPER_LEAGUE' ? 'Супер' : league;
}

export async function loadLeagueAvg() {
    try {
        const data = await AnalyticsAPI.getLeagueAvg();
        document.getElementById('playerAvgPill').textContent = formatMoney(data.playerAverage);

        // Бэк возвращает closestLeague — ближайшую лигу игрока
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