// js/compare/salary-card.js

function formatMoney(amount) {
    if (amount == null) return '0 ₽';
    return Number(amount).toLocaleString('ru-RU', { maximumFractionDigits: 0 }) + ' ₽';
}

function formatPercent(val) {
    return val != null ? Number(val).toFixed(1) + '%' : '0%';
}

window.toggleMethodology = function() {
    const body = document.getElementById('methodologyBody');
    const arrow = document.getElementById('methodologyArrow');
    if (body.style.display === 'none') {
        body.style.display = 'block';
        arrow.style.transform = 'rotate(180deg)';
    } else {
        body.style.display = 'none';
        arrow.style.transform = 'rotate(0deg)';
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

    const total = p1.totalAmount + p2.totalAmount;
    const p1Share = total > 0 ? Math.round((p1.totalAmount / total) * 100) : 50;
    const p2Share = 100 - p1Share;

    return `
        <!-- Высота уменьшена: было calc(100dvh - 270px), стало calc(100dvh - 340px) -->
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

                <!-- Полоска больше -->
                <div class="h2h-bar" style="height: 16px; border-radius: 8px; margin-bottom: 16px;">
                    <div class="h2h-bar-left" style="width: ${p1Share}%; display: flex; align-items: center; justify-content: center;">
                        <span class="h2h-bar-value" style="font-size: 10px;">${p1Share}%</span>
                    </div>
                    <div class="h2h-bar-right" style="width: ${p2Share}%; display: flex; align-items: center; justify-content: center;">
                        <span class="h2h-bar-value" style="font-size: 10px;">${p2Share}%</span>
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

                <!-- Итоговый рейтинг (Со стеклянным эффектом) -->
                <div style="
                    background: rgba(255, 255, 255, 0.03);
                    backdrop-filter: blur(12px);
                    -webkit-backdrop-filter: blur(12px);
                    border: 1px solid rgba(255,255,255,0.08);
                    box-shadow: inset 0 1px 0 rgba(255,255,255,0.05);
                    border-radius: 16px; 
                    padding: 18px 16px 14px; 
                    text-align: left;
                ">
                    
                    <!-- Убрали имя сверху, оставили только текст -->
                    <div style="font-size: 14px; color: #d4d4d8; line-height: 1.5; margin-bottom: 12px; text-align: center;">
                        ${isDraw ? 'Игроки показали равные результаты.' : 'За данный период игрок ' + winnerName + ' находился в более стабильной и сильной форме по соотношению ключевых показателей.'}
                    </div>

                    <!-- Кнопка "Методика" -->
                    <div style="margin-bottom: 10px; border-top: 1px solid rgba(255,255,255,0.08); padding-top: 10px;">
                        <div onclick="toggleMethodology()" style="display: flex; justify-content: space-between; align-items: center; cursor: pointer; color: #ffffff; font-size: 13px; font-weight: 700; user-select: none;">
                            <span>Методика расчета</span>
                            <svg id="methodologyArrow" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="transition: transform 0.2s;">
                                <polyline points="6 9 12 15 18 9"/>
                            </svg>
                        </div>
                        
                        <div id="methodologyBody" style="display: none; margin-top: 10px; font-size: 12px; color: #a1a1aa; line-height: 1.5; text-align: left;">
                            <p style="margin-bottom: 8px; text-align: left;">Рейтинг честно сравнивает игроков, независимо от их лиги и общего количества игр. Мы смотрим только на то, как игрок показывает себя относительно своего уровня:</p>
                            <ul style="list-style-type: disc; padding-left: 16px; margin: 0; display: flex; flex-direction: column; gap: 6px;">
                                <li style="text-align: left;">Эффективность (отношение среднего чека игрока к среднему чеку по его лиге)</li>
                                <li style="text-align: left;">Процент выигранных матчей</li>
                                <li style="text-align: left;">Количество сыгранных турниров</li>
                            </ul>
                        </div>
                    </div>

                    <div class="salary-topsis-score" style="margin-bottom: 0; gap: 12px;">
                        <span class="${p1Win ? 'win' : ''}" style="font-size: 18px;">${formatPercent(p1.topsisScore)}</span>
                        <span class="vs" style="font-size: 11px;">vs</span>
                        <span class="${p2Win ? 'win' : ''}" style="font-size: 18px;">${formatPercent(p2.topsisScore)}</span>
                    </div>
                </div>
            </div>
        </div>
    `;
}