// js/compare/player-card.js

function formatMoney(value) {
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        maximumFractionDigits: 0
    }).format(value || 0);
}

export function renderPlayerCard(player, metric, statsData = null) {
    if (!player) {
        return '<p class="text-zinc-500 text-sm text-center">Выберите игрока</p>';
    }

    const data = statsData || player;

    let fieldsHtml = '';

    metric.fields.forEach(field => {
        const value = data?.[field.key] ?? 0;

        if (field.type === 'progress') {
            fieldsHtml += `
                <div class="stat-bar-row">
                    <span class="stat-label" style="font-size: 13px;">${field.label}</span>
                    <div class="stat-bar">
                        <div class="stat-bar-fill" style="width: ${value}%"></div>
                    </div>
                    <span class="stat-value" style="font-size: 12px;">${value}%</span>
                </div>
            `;
        } else if (field.type === 'money') {
            fieldsHtml += `
                <div class="stat-row">
                    <span class="stat-label" style="font-size: 13px;">${field.label}</span>
                    <span class="stat-value" style="font-size: 12px;">${formatMoney(value)}</span>
                </div>
            `;
        } else {
            fieldsHtml += `
                <div class="stat-row">
                    <span class="stat-label" style="font-size: 13px;">${field.label}</span>
                    <span class="stat-value" style="font-size: 12px;">${value}</span>
                </div>
            `;
        }
    });

    return `
        <p class="player-name">${player.playerName}</p>
        ${metric.id === 'money' ? `
            <div class="player-league-row">
                <span class="player-league" style="font-size: 14px;">${player.primaryLeague || '—'}</span>
            </div>
        ` : ''}
        ${fieldsHtml}
    `;
}