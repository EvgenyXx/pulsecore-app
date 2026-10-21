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

    const scoreRowsHtml = scoreStats.map(s => {
        const isP1Win = s.player1Wins > s.player2Wins;
        const isP2Win = s.player2Wins > s.player1Wins;
        return `
            <div class="salary-metric-row" style="border-bottom: 1px solid rgba(255,255,255,0.04); padding: 10px 0;">
                <span class="salary-val" style="color: ${isP1Win ? '#34d399' : '#fff'}; font-weight: ${isP1Win ? '800' : '500'};">${s.player1Wins}</span>
                <span class="salary-label" style="font-weight: 700; color: #e4e4e7;">${s.score}</span>
                <span class="salary-val" style="color: ${isP2Win ? '#34d399' : '#fff'}; font-weight: ${isP2Win ? '800' : '500'};">${s.player2Wins}</span>
            </div>
        `;
    }).join('');

    // Важно: переносим модалку в body после рендера
    setTimeout(moveModalToBody, 0);

    return `
        <div class="h2h-card salary-mode" style="height: auto; padding: 12px 12px 16px 12px;">
            
            <!-- 1. ШАПКА -->
            <div class="h2h-glass-header" style="margin-bottom: 12px; padding: 16px; border-radius: 16px;">
                <div class="h2h-players" style="margin-bottom: 16px; align-items: flex-start;">
                    <div class="h2h-player" style="text-align: left;">
                        <p class="h2h-player-name" style="color: #ffffff; font-size: 16px; font-weight: 600; margin-bottom: 4px; line-height: 1.2;">${data.player1Name}</p>
                        <div><span class="h2h-league-badge">Победы</span></div>
                        <p class="h2h-player-wins" style="font-size: 1.3rem; font-weight: 800; color: #fff; line-height: 1.1; margin-top: 8px;">${p1Wins}</p>
                        <div class="h2h-diff-badge ${p1MoreWins ? 'win' : 'lose'}">
                            ${p1MoreWins ? `+${diffWins}` : `-${diffWins}`}
                        </div>
                    </div>

                    <div class="h2h-vs" style="margin-top: 44px;">
                        <span class="h2h-vs-text" style="font-size: 1.1rem; color: #a1a1aa; opacity: 0.4; font-weight: 900; font-style: italic;">VS</span>
                    </div>

                    <div class="h2h-player" style="text-align: right;">
                        <p class="h2h-player-name" style="color: #ffffff; font-size: 16px; font-weight: 600; margin-bottom: 4px; line-height: 1.2;">${data.player2Name}</p>
                        <div><span class="h2h-league-badge">Победы</span></div>
                        <p class="h2h-player-wins" style="font-size: 1.3rem; font-weight: 800; color: #fff; line-height: 1.1; margin-top: 8px;">${p2Wins}</p>
                        <div class="h2h-diff-badge ${p2MoreWins ? 'win' : 'lose'}">
                            ${p2MoreWins ? `+${diffWins}` : `-${diffWins}`}
                        </div>
                    </div>
                </div>

                <div class="h2h-bar" style="height: 24px; border-radius: 12px; flex-shrink: 0;">
                    <div class="h2h-bar-left" style="width: ${p1Percent}%; display: flex; align-items: center; justify-content: center;">
                        <span class="h2h-bar-value" style="font-size: 12px; font-weight: 700;">${p1Percent}%</span>
                    </div>
                    <div class="h2h-bar-right" style="width: ${p2Percent}%; display: flex; align-items: center; justify-content: center;">
                        <span class="h2h-bar-value" style="font-size: 12px; font-weight: 700;">${p2Percent}%</span>
                    </div>
                </div>
            </div>

            <!-- 2. ТАБЛИЦА СЧЕТОВ -->
            <div class="h2h-glass-table" style="margin-bottom: 12px; padding: 4px 16px; border-radius: 16px;">
                <div class="salary-metrics-table" style="margin-bottom: 0;">
                    <div class="salary-metric-row" style="border-bottom: 1px solid rgba(255,255,255,0.08); padding-bottom: 8px; margin-bottom: 4px;">
                        <span class="salary-val" style="font-size: 11px; color: #8b87a8; text-align: left;">${data.player1Name.split(' ')[0]}</span>
                        <span class="salary-label" style="font-size: 11px; color: #8b87a8;">Счёт</span>
                        <span class="salary-val" style="font-size: 11px; color: #8b87a8; text-align: right;">${data.player2Name.split(' ')[0]}</span>
                    </div>
                    ${scoreRowsHtml || '<div class="text-center text-zinc-500 text-xs py-4">Нет данных о счетах</div>'}
                </div>
            </div>

            <!-- 3. КНОПКА ОТКРЫТИЯ МОДАЛКИ -->
            <div class="salary-topsis-block" style="padding: 0; overflow: hidden; background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.08);">
                <button onclick="window.openH2HHistory()" 
                        style="width: 100%; padding: 16px; display: flex; justify-content: space-between; align-items: center; background: transparent; border: none; cursor: pointer; color: #fff;">
                    <span style="font-size: 14px; font-weight: 600;">Показать историю матчей</span>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
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
        // Гарантируем, что модалка в body
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