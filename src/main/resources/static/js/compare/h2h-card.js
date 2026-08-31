// js/compare/h2h-card.js

const STAGE_LABELS = {
    'GROUP': 'Группа',
    'SEMIFINAL': 'Полуфинал',
    'THIRD_PLACE': '3-е место',
    'FINAL': 'Финал'
};

function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
}

export function renderH2HCard(data, dateRange = null) {
    if (!data || !data.summary || data.summary.totalMatches === 0) {
        return `
            <div class="apple-card p-8 text-center">
                <p class="text-zinc-400">Между этими игроками нет матчей</p>
                ${dateRange ? `<p class="text-zinc-600 text-xs mt-2">${dateRange}</p>` : ''}
            </div>
        `;
    }

    const { summary, stages } = data;
    const total = summary.totalMatches;
    const p1Wins = summary.player1Wins;
    const p2Wins = summary.player2Wins;
    const p1Percent = Math.round((p1Wins / total) * 100);
    const p2Percent = Math.round((p2Wins / total) * 100);

    const stagesHtml = (stages || []).map(s => {
        const diff = s.player1Wins - s.player2Wins;
        const diffText = diff > 0 ? `+${diff}` : diff === 0 ? '0' : `${diff}`;
        const diffColor = diff > 0 ? 'h2h-diff-left' : diff < 0 ? 'h2h-diff-right' : 'h2h-diff-zero';
        return `
            <div class="h2h-stage-row">
                <span class="h2h-stage-label">${STAGE_LABELS[s.stage] || s.stage}</span>
                <div class="h2h-stage-score">
                    <span class="h2h-stage-col h2h-wins-left">${s.player1Wins}</span>
                    <span class="h2h-stage-col h2h-stage-total">${s.totalMatches}</span>
                    <span class="h2h-stage-col h2h-wins-right">${s.player2Wins}</span>
                    <span class="h2h-stage-col h2h-diff ${diffColor}">${diffText}</span>
                </div>
            </div>
        `;
    }).join('');

    return `
        <div class="apple-card h2h-card">
            <h3 class="h2h-title">Личные встречи</h3>

            <div class="h2h-players">
                <div class="h2h-player">
                    <p class="h2h-player-name">${data.player1Name}</p>
                    <p class="h2h-player-wins">${p1Wins}</p>
                    <p class="h2h-player-label">побед (${p1Percent}%)</p>
                </div>
                <div class="h2h-vs">
                    <span class="h2h-total">${total} матчей</span>
                    <span class="h2h-vs-text">VS</span>
                </div>
                <div class="h2h-player">
                    <p class="h2h-player-name">${data.player2Name}</p>
                    <p class="h2h-player-wins">${p2Wins}</p>
                    <p class="h2h-player-label">побед (${p2Percent}%)</p>
                </div>
            </div>

            <div class="h2h-bar">
                <div class="h2h-bar-left" style="width: ${p1Percent}%"></div>
                <div class="h2h-bar-right" style="width: ${p2Percent}%"></div>
            </div>

            ${dateRange ? `<p class="h2h-date-range">📅 ${dateRange}</p>` : ''}

            <div class="h2h-stages">
                <h4 class="h2h-stages-title">По стадиям</h4>
                <div class="h2h-stage-header">
                    <span class="h2h-header-stage">Стадия</span>
                    <div class="h2h-stage-header-score">
                        <span class="h2h-header-col">${data.player1Name.split(' ')[0]}</span>
                        <span class="h2h-header-col">Матчи</span>
                        <span class="h2h-header-col">${data.player2Name.split(' ')[0]}</span>
                        <span class="h2h-header-col">Разница</span>
                    </div>
                </div>
                ${stagesHtml || '<p class="text-zinc-500 text-sm text-center">Нет данных</p>'}
            </div>
        </div>
    `;
}