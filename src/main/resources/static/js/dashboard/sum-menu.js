export function renderSumMenu() {
    return `
        <div class="sum-menu space-y-2">
            <div class="apple-card menu-card" onclick="showSumCalculator()">
                <div class="menu-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                </div>
                <div class="flex-1">
                    <h3 class="menu-title">Подсчёт суммы</h3>
                    <p class="menu-subtitle">Заработок за период</p>
                </div>
                <svg class="menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
            </div>
            
            <div class="apple-card menu-card" onclick="showReportForm()">
                <div class="menu-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#818cf8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
                </div>
                <div class="flex-1">
                    <h3 class="menu-title">Отчёт на почту</h3>
                    <p class="menu-subtitle">Запланировать отправку</p>
                </div>
                <svg class="menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>
            </div>
        </div>
    `;
}