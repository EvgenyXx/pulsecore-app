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
    const p1Win = winnerName === p1.name;
    const p2Win = winnerName === p2.name;
    const isDraw = winnerName === 'Ничья';

    const p1MoreMoney = p1.totalAmount > p2.totalAmount;
    const p2MoreMoney = p2.totalAmount > p1.totalAmount;
    const diffTotal = Math.abs(p1.totalAmount - p2.totalAmount);

    const winBadgeStyle = "display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; font-weight: 700; background: rgba(52, 211, 153, 0.15); color: #34d399; margin-top: 6px;";
    const loseBadgeStyle = "display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; font-weight: 700; background: rgba(255, 255, 255, 0.05); color: #71717a; margin-top: 6px;";

    // Стиль для имен: ограничение в 2 строки, фиксированная высота
    const nameStyle = "color: #ffffff; font-size: 14px; font-weight: 600; margin-bottom: 4px; line-height: 1.3; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; min-height: 2.6em; word-break: break-word;";

    return `
        <div class="h2h-card salary-mode" style="height: calc(100dvh - 340px); padding-bottom: 24px;">
            
            <!-- ФИКСИРОВАННАЯ ШАПКА -->
            <div style="flex-shrink: 0;">
                
                <div class="h2h-players" style="margin-bottom: 20px; align-items: flex-start;">
                    
                    <!-- Игрок 1 -->
                    <div class="h2h-player" style="text-align: left; padding-right: 4px;">
                        <p class="h2h-player-name" style="${nameStyle}">${p1.name}</p>
                        <p class="h2h-player-wins" style="font-size: 1.2rem; font-weight: 800; color: #fff; text-shadow: none; line-height: 1.1;">${formatMoney(p1.totalAmount)}</p>
                        <div style="${p1MoreMoney ? winBadgeStyle : loseBadgeStyle}">
                            ${p1MoreMoney ? `+${formatMoney(diffTotal)}` : `-${formatMoney(diffTotal)}`}
                        </div>
                    </div>

                    <!-- VS -->
                    <div class="h2h-vs" style="margin-top: 10px;">
                        <span class="h2h-vs-text" style="font-size: 1.1rem; color: #a1a1aa; opacity: 0.4; font-weight: 900; font-style: italic;">VS</span>
                    </div>

                    <!-- Игрок 2 -->
                    <div class="h2h-player" style="text-align: right; padding-left: 4px;">
                        <p class="h2h-player-name" style="${nameStyle}">${p2.name}</p>
                        <p class="h2h-player-wins" style="font-size: 1.2rem; font-weight: 800; color: #fff; text-shadow: none; line-height: 1.1;">${formatMoney(p2.totalAmount)}</p>
                        <div style="${p2MoreMoney ? winBadgeStyle : loseBadgeStyle}">
                            ${p2MoreMoney ? `+${formatMoney(diffTotal)}` : `-${formatMoney(diffTotal)}`}
                        </div>
                    </div>
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

                <!-- Итоговый рейтинг -->
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
                    
                    <!-- Проценты TOPSIS -->
                    <div class="salary-topsis-score" style="display: flex; justify-content: center; align-items: center; gap: 12px; margin-bottom: 12px; font-size: 1.4rem; font-weight: 800;">
                        <span class="${p1Win ? 'win' : ''}" style="color: ${p1Win ? '#ffffff' : '#71717a'}; text-shadow: ${p1Win ? '0 0 16px rgba(255,255,255,0.4)' : 'none'};">${formatPercent(p1.topsisScore)}</span>
                        <span class="vs" style="font-size: 11px; color: #52525b; font-weight: 800; font-style: normal;">VS</span>
                        <span class="${p2Win ? 'win' : ''}" style="color: ${p2Win ? '#ffffff' : '#71717a'}; text-shadow: ${p2Win ? '0 0 16px rgba(255,255,255,0.4)' : 'none'};">${formatPercent(p2.topsisScore)}</span>
                    </div>

                    <!-- Текст с результатом и иконкой методики -->
                    <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
                        <div style="font-size: 14px; color: #d4d4d8; line-height: 1.5; text-align: center;">
                            ${isDraw ? 'Игроки показали равные результаты.' : 'За данный период ' + winnerName + ' находился в более стабильной и сильной форме по соотношению ключевых показателей.'}
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