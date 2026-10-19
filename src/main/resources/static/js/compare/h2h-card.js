// js/compare/h2h-card.js

function formatShortDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: '2-digit', month: 'short', year: 'numeric' });
}

export function renderH2HCard(data) {
    if (!data || !data.summary || data.summary.totalMatches === 0) {
        return `
            <div class="h2h-card p-6 text-center">
                <p class="text-zinc-400 text-sm">Между этими игроками нет матчей</p>
            </div>
        `;
    }

    const { summary } = data;
    const total = summary.totalMatches;
    const p1Wins = summary.player1Wins;
    const p2Wins = summary.player2Wins;
    const p1Percent = Math.round((p1Wins / total) * 100);
    const p2Percent = Math.round((p2Wins / total) * 100);

    const p1Class = p1Wins > p2Wins ? 'is-leader' : '';
    const p2Class = p2Wins > p1Wins ? 'is-leader' : '';

    return `
        <div class="h2h-card">
            <h3 class="h2h-title">Личные встречи <span style="opacity: 0.5; font-weight: normal; margin-left: 4px;">(${total})</span></h3>

            <div class="h2h-players">
                <div class="h2h-player ${p1Class}">
                    <p class="h2h-player-name">${data.player1Name}</p>
                    <p class="h2h-player-wins">${p1Wins}</p>
                </div>
                <div class="h2h-vs">
                    <span class="h2h-vs-text">VS</span>
                </div>
                <div class="h2h-player ${p2Class}">
                    <p class="h2h-player-name">${data.player2Name}</p>
                    <p class="h2h-player-wins">${p2Wins}</p>
                </div>
            </div>

            <div class="h2h-bar">
                <div class="h2h-bar-left" style="width: ${p1Percent}%">
                    <span class="h2h-bar-value">${p1Percent}%</span>
                </div>
                <div class="h2h-bar-right" style="width: ${p2Percent}%">
                    <span class="h2h-bar-value">${p2Percent}%</span>
                </div>
            </div>

            <div class="h2h-matches-scroll">
                <div class="h2h-matches-list" id="h2hMatchesList">
                    <div class="h2h-matches-loader">
                        <div class="spinner-sm"></div>
                        <span>Загрузка матчей…</span>
                    </div>
                </div>
                <div class="h2h-matches-empty hidden" id="h2hMatchesEmpty">
                    Матчи не найдены
                </div>
            </div>
        </div>
    `;
}

export function renderMatchRow(match, player1Name, player2Name) {
    const winnerIsP1 = match.winnerName?.toLowerCase() === player1Name?.toLowerCase();
    const winnerIsP2 = match.winnerName?.toLowerCase() === player2Name?.toLowerCase();

    const sideClass = winnerIsP1 ? 'is-p1' : (winnerIsP2 ? 'is-p2' : '');

    return `
        <div class="h2h-match-row ${sideClass}">
            <span class="h2h-match-date">${formatShortDate(match.playedAt)}</span>
            <span class="h2h-match-score">${match.score || '—'}</span>
            <span class="h2h-match-winner">
                <svg class="h2h-match-crown" width="11" height="11" viewBox="0 0 24 24" fill="currentColor" stroke="none">
                    <path d="M5 16L3 5l5.5 5L12 4l3.5 6L21 5l-2 11H5zm14 3c0 .6-.4 1-1 1H6c-.6 0-1-.4-1-1v-1h14v1z"/>
                </svg>
                <span>${match.winnerName || '—'}</span>
            </span>
        </div>
    `;
}