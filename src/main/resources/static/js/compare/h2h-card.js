// js/compare/h2h-card.js

function formatShortDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: '2-digit', month: 'short', year: 'numeric' });
}

export function renderH2HCard(data) {
    if (!data || !data.summary || data.summary.totalMatches === 0) {
        return `
            <div class="h2h-card salary-mode" style="height: auto; padding: 12px 12px 16px 12px;">
                <p class="text-zinc-400 text-sm text-center">Между этими игроками нет матчей</p>
            </div>
        `;
    }

    const { summary } = data;
    const total = summary.totalMatches;
    const p1Wins = summary.player1Wins;
    const p2Wins = summary.player2Wins;
    const p1Percent = Math.round((p1Wins / total) * 100);
    const p2Percent = Math.round((p2Wins / total) * 100);

    const p1MoreWins = p1Wins > p2Wins;
    const p2MoreWins = p2Wins > p1Wins;
    const diffWins = Math.abs(p1Wins - p2Wins);

    const scoreStats = data.scoreStats || [];

    let totalScoreMatches = 0;
    scoreStats.forEach(s => {
        totalScoreMatches += (s.player1Wins || 0) + (s.player2Wins || 0);
    });
    if (total > totalScoreMatches) totalScoreMatches = total;
    if (totalScoreMatches === 0) totalScoreMatches = 1;

    const scoreRowsHtml = scoreStats.map(s => {
        const matchCount = (s.player1Wins || 0) + (s.player2Wins || 0);
        const percent = Math.round((matchCount / totalScoreMatches) * 100);

        return `
            <div class="h2h-score-row">
                <span class="h2h-score-wins left">${s.player1Wins}</span>
                
                <div class="h2h-score-bar-wrap">
                    <div class="h2h-score-bar-fill-single" style="width: ${percent}%;"></div>
                    
                    <div class="h2h-score-bar-center">
                        <span class="h2h-bar-score">${s.score}</span>
                        <span class="h2h-bar-divider">•</span>
                        <span class="h2h-bar-pct">${percent}%</span>
                    </div>
                </div>

                <span class="h2h-score-wins right">${s.player2Wins}</span>
            </div>
        `;
    }).join('');

    setTimeout(moveModalToBody, 0);

    return `
        <div class="h2h-card salary-mode" style="height: auto; padding: 12px 12px 16px 12px;">
            
            <!-- 1. ШАПКА -->
            <div class="h2h-glass-header" style="margin-bottom: 12px; padding: 16px; border-radius: 24px;">
                <div class="h2h-players" style="margin-bottom: 12px; align-items: flex-start;">
                    
                    <!-- Левый игрок -->
                    <div class="h2h-player" style="text-align: left; flex: 1; min-width: 0;">
                        <p class="h2h-player-name" style="color: #ffffff; font-size: 14px; font-weight: 600; margin-bottom: 4px; line-height: 1.2; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${data.player1Name}</p>
                        <p class="h2h-player-wins" style="font-size: 1.6rem; font-weight: 800; color: #fff; line-height: 1; margin-top: 4px; text-shadow: 0 0 20px rgba(255,255,255,0.4);">${p1Wins}</p>
                        <div class="h2h-diff-badge ${p1MoreWins ? 'win' : 'lose'}" style="margin-top: 8px; padding: 3px 10px; font-size: 11px; font-weight: 700; border-radius: 12px; backdrop-filter: blur(8px); display: inline-block;">
                            ${p1MoreWins ? `+${diffWins}` : `-${diffWins}`}
                        </div>
                    </div>

                    <!-- VS -->
                    <div class="h2h-vs" style="margin-top: 12px; flex-shrink: 0; width: 40px; display: flex; justify-content: center;">
                        <span class="h2h-vs-text" style="font-size: 1.1rem; color: #a1a1aa; opacity: 0.5; font-weight: 900; font-style: italic; text-shadow: 0 0 10px rgba(255,255,255,0.2);">VS</span>
                    </div>

                    <!-- Правый игрок -->
                    <div class="h2h-player" style="text-align: right; flex: 1; min-width: 0;">
                        <p class="h2h-player-name" style="color: #ffffff; font-size: 14px; font-weight: 600; margin-bottom: 4px; line-height: 1.2; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${data.player2Name}</p>
                        <p class="h2h-player-wins" style="font-size: 1.6rem; font-weight: 800; color: #fff; line-height: 1; margin-top: 4px; text-shadow: 0 0 20px rgba(255,255,255,0.4);">${p2Wins}</p>
                        <div class="h2h-diff-badge ${p2MoreWins ? 'win' : 'lose'}" style="margin-top: 8px; padding: 3px 10px; font-size: 11px; font-weight: 700; border-radius: 12px; backdrop-filter: blur(8px); display: inline-block;">
                            ${p2MoreWins ? `+${diffWins}` : `-${diffWins}`}
                        </div>
                    </div>
                </div>

                <!-- ВЕРХНИЙ БАР -->
                <div class="h2h-bar-top-wrap">
                    <div class="h2h-bar-top-fill left" style="width: ${p1Percent}%;">
                        <div class="h2h-bar-top-glare"></div>
                    </div>
                    <div class="h2h-bar-top-fill right" style="width: ${p2Percent}%;">
                        <div class="h2h-bar-top-glare"></div>
                    </div>
                    
                    <div class="h2h-bar-top-capsule">
                        <span class="h2h-bar-score">${p1Percent}%</span>
                        <span class="h2h-bar-divider">•</span>
                        <span class="h2h-bar-pct">${p2Percent}%</span>
                    </div>
                </div>
            </div>

            <!-- 2. ТАБЛИЦА СЧЕТОВ -->
            <div class="h2h-glass-table" style="margin-bottom: 12px; padding: 8px 16px; border-radius: 20px;">
                <div class="salary-metrics-table" style="margin-bottom: 0;">
                    <div class="salary-metric-row" style="border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 8px; margin-bottom: 4px;">
                        <span class="salary-val" style="text-align: center; font-size: 11px; color: #8b87a8;"></span>
                        <span class="salary-label" style="font-size: 10px; color: #8b87a8; text-transform: uppercase; letter-spacing: 1px; text-align: center;">Счёт</span>
                        <span class="salary-val" style="text-align: center; font-size: 11px; color: #8b87a8;"></span>
                    </div>
                    ${scoreRowsHtml || '<div class="text-center text-zinc-500 text-xs py-4">Нет данных о счетах</div>'}
                </div>
            </div>

            <!-- 3. КНОПКА -->
            <div class="salary-topsis-block" style="padding: 0; overflow: hidden; background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.1); box-shadow: inset 0 1px 0 rgba(255,255,255,0.05); border-radius: 16px;">
                <button onclick="window.openH2HHistory()" 
                        style="width: 100%; padding: 14px; display: flex; justify-content: space-between; align-items: center; background: transparent; border: none; cursor: pointer; color: #fff;">
                    <span style="font-size: 12px; font-weight: 600;">Показать историю матчей</span>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </button>
            </div>
        </div>

        <!-- 4. МОДАЛЬНОЕ ОКНО -->
        <div id="h2hHistoryModal" class="h2h-history-modal" onclick="if(event.target === this) window.closeH2HHistory()">
            <div class="h2h-history-modal-content" onclick="event.stopPropagation()">
                <div class="h2h-history-modal-header">
                    <span>История матчей</span>
                    <button onclick="window.closeH2HHistory()">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                    </button>
                </div>
                <div class="h2h-history-modal-body h2h-matches-scroll" onclick="event.stopPropagation()">
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
        </div>
    `;
}

function moveModalToBody() {
    const modal = document.getElementById('h2hHistoryModal');
    if (modal && modal.parentElement !== document.body) {
        document.body.appendChild(modal);
    }
}

window.openH2HHistory = function() {
    const modal = document.getElementById('h2hHistoryModal');
    if (modal) {
        if (modal.parentElement !== document.body) {
            document.body.appendChild(modal);
        }
        modal.classList.add('open');
    }
};

window.closeH2HHistory = function() {
    const modal = document.getElementById('h2hHistoryModal');
    if (modal) modal.classList.remove('open');
};

export function renderMatchRow(match, player1Name, player2Name) {
    return `
        <div class="h2h-match-row">
            <span class="h2h-match-date">${formatShortDate(match.playedAt)}</span>
            <span class="h2h-match-score">${match.score || '—'}</span>
            <span class="h2h-match-winner">${match.winnerName || '—'}</span>
        </div>
    `;
}