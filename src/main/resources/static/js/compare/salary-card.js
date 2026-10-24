// js/compare/salary-card.js

function formatMoney(amount) {
    if (amount == null) return '0 ₽';
    return Number(amount).toLocaleString('ru-RU', { maximumFractionDigits: 0 }) + ' ₽';
}

// Функция для шапки — выводит только цифры (без знака рубля)
function formatMoneyNoSign(amount) {
    if (amount == null) return '0';
    return Number(amount).toLocaleString('ru-RU', { maximumFractionDigits: 0 });
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

    return `
        <div class="h2h-card salary-mode" style="height: auto; padding: 12px 12px 16px 12px;">
            
            <!-- СТЕКЛЯННАЯ ШАПКА -->
            <div class="h2h-glass-header" style="margin-bottom: 12px; padding: 16px; border-radius: 16px;">
                <div class="h2h-players" style="margin-bottom: 0; align-items: flex-start; gap: 8px;">
                    
                    <!-- Левый игрок -->
                    <div class="h2h-player" style="text-align: left; min-width: 0; flex: 1; display: flex; flex-direction: column; align-items: flex-start;">
                        <p class="h2h-player-name" style="color: #ffffff; font-size: 16px; font-weight: 600; margin-bottom: 4px; line-height: 1.2; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%;">${p1.name}</p>
                        <div><span class="h2h-league-badge">Лига ${p1.league || '?'}</span></div>
                        
                        <!-- Сумма без знака рубля -->
                        <p class="h2h-player-wins" style="font-size: 1.4rem; font-weight: 800; color: #fff; line-height: 1.1; margin-top: 8px; white-space: nowrap; text-shadow: 0 0 15px rgba(255,255,255,0.4);">
                            ${formatMoneyNoSign(p1.totalAmount)}
                        </p>
                        
                        <div class="h2h-diff-badge ${p1MoreMoney ? 'win' : 'lose'}">
                            ${p1MoreMoney ? `+${formatMoney(diffTotal)}` : `-${formatMoney(diffTotal)}`}
                        </div>
                    </div>

                    <div class="h2h-vs" style="margin-top: 44px; flex-shrink: 0; width: 30px; display: flex; justify-content: center;">
                        <span class="h2h-vs-text" style="font-size: 1.1rem; color: #a1a1aa; opacity: 0.4; font-weight: 900; font-style: italic;">VS</span>
                    </div>

                    <!-- Правый игрок -->
                    <div class="h2h-player" style="text-align: right; min-width: 0; flex: 1; display: flex; flex-direction: column; align-items: flex-end;">
                        <p class="h2h-player-name" style="color: #ffffff; font-size: 16px; font-weight: 600; margin-bottom: 4px; line-height: 1.2; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%;">${p2.name}</p>
                        <div><span class="h2h-league-badge">Лига ${p2.league || '?'}</span></div>
                        
                        <!-- Сумма без знака рубля -->
                        <p class="h2h-player-wins" style="font-size: 1.4rem; font-weight: 800; color: #fff; line-height: 1.1; margin-top: 8px; white-space: nowrap; text-shadow: 0 0 15px rgba(255,255,255,0.4);">
                            ${formatMoneyNoSign(p2.totalAmount)}
                        </p>
                        
                        <div class="h2h-diff-badge ${p2MoreMoney ? 'win' : 'lose'}">
                            ${p2MoreMoney ? `+${formatMoney(diffTotal)}` : `-${formatMoney(diffTotal)}`}
                        </div>
                    </div>
                </div>
            </div>

            <!-- ТАБЛИЦА МЕТРИК В СТЕКЛЕ -->
            <div class="h2h-glass-table" style="margin-bottom: 12px; padding: 4px 16px; border-radius: 16px;">
                <div class="salary-metrics-table" style="margin-bottom: 0;">
                    <div class="salary-metric-row" style="border-bottom: 1px solid rgba(255,255,255,0.06);">
                        <span class="salary-val">${p1.tournamentsCount}</span>
                        <span class="salary-label">Сыграно турниров</span>
                        <span class="salary-val">${p2.tournamentsCount}</span>
                    </div>
                    <div class="salary-metric-row" style="border-bottom: 1px solid rgba(255,255,255,0.06);">
                        <span class="salary-val">${formatMoney(p1.avgAmount)}</span>
                        <span class="salary-label">Средний чек</span>
                        <span class="salary-val">${formatMoney(p2.avgAmount)}</span>
                    </div>
                    <div class="salary-metric-row" style="border-bottom: none;">
                        <span class="salary-val">${formatPercent(p1.winratePercent)}</span>
                        <span class="salary-label">Процент побед</span>
                        <span class="salary-val">${formatPercent(p2.winratePercent)}</span>
                    </div>
                </div>
            </div>

            <!-- ИТОГОВЫЙ БЛОК TOPSIS -->
            <div class="salary-topsis-block" style="border-radius: 16px; padding: 16px; text-align: center;">
                <div class="salary-topsis-score" style="display: flex; justify-content: center; align-items: center; gap: 12px; margin-bottom: 10px; font-size: 1.3rem; font-weight: 800;">
                    <span class="${p1Win ? 'win' : ''}">${formatPercent(p1.topsisScore)}</span>
                    <span class="vs">VS</span>
                    <span class="${p2Win ? 'win' : ''}">${formatPercent(p2.topsisScore)}</span>
                </div>

                <div class="salary-topsis-text-row">
                    <div class="salary-topsis-text">
                        ${isDraw ? 'Игроки показали равные результаты.' : 'За данный период ' + winnerName + ' находился в более стабильной и сильной форме по соотношению ключевых показателей.'}
                    </div>
                    <div onclick="toggleMethodologySheet()" class="salary-methodology-btn">?</div>
                </div>
            </div>
        </div>
    `;
}