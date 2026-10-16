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
            <div class="h2h-card p-6 text-center">
                <p class="text-zinc-400 text-sm">Между этими игроками нет матчей</p>
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
            <div class="h2h-stage-tile">
                <div class="h2h-tile-label">${STAGE_LABELS[s.stage] || s.stage}</div>
                <div class="h2h-tile-scores">
                    <span class="h2h-tile-p1">${s.player1Wins}</span>
                    <span class="h2h-tile-vs">vs</span>
                    <span class="h2h-tile-p2">${s.player2Wins}</span>
                </div>
                <div class="h2h-tile-bottom">
                    <span class="h2h-tile-total">${s.totalMatches} матчей</span>
                    <span class="h2h-tile-diff ${diffColor}">${diffText}</span>
                </div>
            </div>
        `;
    }).join('');

    return `
        <div class="h2h-card">
            <h3 class="h2h-title">Личные встречи</h3>

            <div class="h2h-players">
                <div class="h2h-player">
                    <p class="h2h-player-name">${data.player1Name}</p>
                    <p class="h2h-player-wins">${p1Wins}</p>
                    <p class="h2h-player-label">${p1Percent}% побед</p>
                </div>
                <div class="h2h-vs">
                    <span class="h2h-total">${total} матчей</span>
                    <span class="h2h-vs-text">VS</span>
                </div>
                <div class="h2h-player">
                    <p class="h2h-player-name">${data.player2Name}</p>
                    <p class="h2h-player-wins">${p2Wins}</p>
                    <p class="h2h-player-label">${p2Percent}% побед</p>
                </div>
            </div>

            <div class="h2h-bar">
                <div class="h2h-bar-left" style="width: ${p1Percent}%"></div>
                <div class="h2h-bar-right" style="width: ${p2Percent}%"></div>
            </div>

            ${dateRange ? `<p class="h2h-date-range">📅 ${dateRange}</p>` : ''}

            <div class="h2h-stages">
                <h4 class="h2h-stages-title">По стадиям</h4>
                <div class="h2h-stages-grid">
                    ${stagesHtml || '<p class="text-zinc-500 text-sm text-center" style="grid-column: 1 / -1;">Нет данных</p>'}
                </div>
            </div>
        </div>
    `;
}