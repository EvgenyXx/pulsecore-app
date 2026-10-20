// js/compare/salary-card.js

function formatMoney(amount) {
    if (amount == null) return '0 ₽';
    return Number(amount).toLocaleString('ru-RU', { maximumFractionDigits: 0 }) + ' ₽';
}

function formatPercent(val) {
    return val != null ? Number(val).toFixed(1) + '%' : '0%';
}

window.toggleMethodologySheet = function() {
    const overlay = document.getElementById('methodologySheetOverlay');
    if (overlay) {
        overlay.classList.toggle('open');
    }
};

export function renderSalaryH2HCard(data) {
    if (!data || !data.player1 || !data.player2) {
        return `<div class="h2h-card p-6 text-center"><p class="text-zinc-400 text-sm">Нет данных для сравнения</p></div>`;
    }

    const { player1: p1, player2: p2, winnerName } = data;
    const isDraw = winnerName === 'Ничья';

    const p1MoreMoney = p1.totalAmount > p2.totalAmount;
    const p2MoreMoney = p2.totalAmount > p1.totalAmount;
    const diffTotal = Math.abs(p1.totalAmount - p2.totalAmount);

    const total = p1.totalAmount + p2.totalAmount;
    const p1Share = total > 0 ? Math.round((p1.totalAmount / total) * 100) : 50;
    const p2Share = 100 - p1Share;

    return `
        <div class="h2h-card salary-mode" style="height: calc(100dvh - 340px); padding-bottom: 24px;">
            
            <!-- ФИКСИРОВАННАЯ ШАПКА -->
            <div style="flex-shrink: 0;">
                
                <div class="h2h-players" style="margin-bottom: 16px;">
                    <div class="h2h-player">
                        <p class="h2h-player-name" style="color: #e4e4e7; font-size: 14px;">${p1.name}</p>
                        <p class="h2h-player-wins" style="font-size: 1.4rem; font-weight: 800; color: #fff; text-shadow: 0 0 15px rgba(255,255,255,0.2);">${formatMoney(p1.totalAmount)}</p>
                        <p style="font-size: 13px; font-weight: 600; margin-top: 2px; color: ${p1MoreMoney ? '#34d399' : '#71717a'};">
                            ${p1MoreMoney ? `(+${formatMoney(diffTotal)})` : `(-${formatMoney(diffTotal)})`}
                        </p>
                    </div>
                    <div class="h2h-vs">
                        <span class="h2h-vs-text" style="font-size: 1.2rem; color: #a1a1aa; opacity: 0.5;">VS</span>
                    </div>
                    <div class="h2h-player">
                        <p class="h2h-player-name" style="color: #e4e4e7; font-size: 14px;">${p2.name}</p>
                        <p class="h2h-player-wins" style="font-size: 1.4rem; font-weight: 800; color: #fff; text-shadow: 0 0 15px rgba(255,255,255,0.2);">${formatMoney(p2.totalAmount)}</p>
                        <p style="font-size: 13px; font-weight: 600; margin-top: 2px; color: ${p2MoreMoney ? '#34d399' : '#71717a'};">
                            ${p2MoreMoney ? `(+${formatMoney(diffTotal)})` : `(-${formatMoney(diffTotal)})`}
                        </p>
                    </div>
                </div>

                <!-- Полоска больше (БЕЗ ПРОЦЕНТОВ ВНУТРИ) -->
                <div class="h2h-bar" style="height: 16px; border-radius: 8px; margin-bottom: 16px;">
                    <div class="h2h-bar-left" style="width: ${p1Share}%;"></div>
                    <div class="h2h-bar-right" style="width: ${p2Share}%;"></div>
                </div>

                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; padding: 0 2px;">
                    <span style="font-size: 12px; font-weight: 600; color: #d4d4d8; background: rgba(255,255,255,0.05); padding: 3px 10px; border-radius: 4px; border: 1px solid rgba(255,255,255,0.05);">${p1.league || 'Лига ?'}</span>
                    <span style="font-size: 10px; font-weight: 700; color: #71717a; text-transform: uppercase; letter-spacing: 1px;">Лига</span>
                    <span style="font-size: 12px; font-weight: 600; color: #d4d4d8; background: rgba(255,255,255,0.05); padding: 3px 10px; border-radius: 4px; border: 1px solid rgba(255,255,255,0.05);">${p2.league || 'Лига ?'}</span>
                </div>
            </div>

            <!-- СКРОЛЛИРУЕМАЯ ОБЛАСТЬ -->
            <div class="h2h-matches-scroll" style="margin-top: 8px; display: flex; flex-direction: column; gap: 16px; padding-bottom: 8px;">
                
                <!-- Таблица метрик -->
                <div class="salary-metrics-table" style="margin-top: 0;">
                    <div class="salary-metric-row" style="padding: 12px 0;">
                        <span class="salary-val ${p1.tournamentsCount > p2.tournamentsCount ? 'win' : ''}" style="font-size: 16px;">${p1.tournamentsCount}</span>
                        <span class="salary-label" style="font-size: 12px;">Сыграно турниров</span>
                        <span class="salary-val ${p2.tournamentsCount > p1.tournamentsCount ? 'win' : ''}" style="font-size: 16px;">${p2.tournamentsCount}</span>
                    </div>
                    <div class="salary-metric-row" style="padding: 12px 0;">
                        <span class="salary-val ${p1.avgAmount > p2.avgAmount ? 'win' : ''}" style="font-size: 16px;">${formatMoney(p1.avgAmount)}</span>
                        <span class="salary-label" style="font-size: 12px;">Средний чек</span>
                        <span class="salary-val ${p2.avgAmount > p1.avgAmount ? 'win' : ''}" style="font-size: 16px;">${formatMoney(p2.avgAmount)}</span>
                    </div>
                    <div class="salary-metric-row" style="padding: 12px 0;">
                        <span class="salary-val ${p1.winratePercent > p2.winratePercent ? 'win' : ''}" style="font-size: 16px;">${formatPercent(p1.winratePercent)}</span>
                        <span class="salary-label" style="font-size: 12px;">Процент побед</span>
                        <span class="salary-val ${p2.winratePercent > p1.winratePercent ? 'win' : ''}" style="font-size: 16px;">${formatPercent(p2.winratePercent)}</span>
                    </div>
                </div>

                <!-- Итоговый рейтинг (Со стеклянным эффектом, БЕЗ ПРОЦЕНТОВ) -->
                <div style="
                    background: rgba(255, 255, 255, 0.03);
                    backdrop-filter: blur(12px);
                    -webkit-backdrop-filter: blur(12px);
                    border: 1px solid rgba(255,255,255,0.08);
                    box-shadow: inset 0 1px 0 rgba(255,255,255,0.05);
                    border-radius: 16px; 
                    padding: 18px 16px 14px; 
                    text-align: center;
                ">
                    
                    <!-- Текст с результатом и иконкой методики -->
                    <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
                        <div style="font-size: 14px; color: #d4d4d8; line-height: 1.5; text-align: center;">
                            ${isDraw ? 'Игроки показали равные результаты.' : 'За данный период игрок ' + winnerName + ' находился в более стабильной и сильной форме по соотношению ключевых показателей.'}
                        </div>
                        
                        <!-- Иконка вопроса -->
                        <div onclick="toggleMethodologySheet()" style="flex-shrink: 0; width: 18px; height: 18px; border-radius: 50%; background: rgba(255,255,255,0.1); display: flex; align-items: center; justify-content: center; cursor: pointer; color: #a1a1aa; font-size: 11px; font-weight: bold; transition: background 0.2s;">
                            ?
                        </div>
                    </div>

                </div>
            </div>
        </div>
    `;
}