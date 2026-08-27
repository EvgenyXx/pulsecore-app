// js/compare/metrics.js

export const compareMetrics = [
    {
        id: 'money',
        label: 'Банк',
        title: 'Заработок',
        description: 'Сумма заработка игрока за выбранный период',
        endpoint: '/api/tournament/compare/players',
        matchKey: 'playerId',
        fields: [
            { key: 'tournaments', label: 'Турниров:', type: 'text' },
            { key: 'totalAmount', label: 'Заработано:', type: 'money' },
            { key: 'averageAmount', label: 'Средний:', type: 'money' }
        ]
    },
    {
        id: 'stats',
        label: 'Стейджи',
        title: 'Победы по стадиям',
        description: 'Процент побед игрока на разных стадиях турнира',
        endpoint: '/api/tournament/compare/match-stats',
        matchKey: 'playerName',
        fields: [
            { key: 'groupWinPercent', label: 'Группа', type: 'progress' },
            { key: 'semifinalWinPercent', label: 'Полуфинал', type: 'progress' },
            { key: 'thirdPlaceWinPercent', label: '3-е место', type: 'progress' },
            { key: 'finalWinPercent', label: 'Финал', type: 'progress' }
        ]
    }
];