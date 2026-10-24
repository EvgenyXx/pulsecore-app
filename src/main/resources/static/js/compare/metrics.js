// js/compare/metrics.js

export const compareMetrics = [
    {
        id: 'h2h',
        label: 'H2H',
        title: 'Личные встречи',
        description: 'Сравнение двух игроков между собой',
        endpoint: '/api/tournament/compare/h2h',
        matchKey: 'playerName',
        fields: [
            { key: 'totalMatches', label: 'Всего матчей', type: 'text' },
            { key: 'player1Wins', label: 'Победы игрока 1', type: 'text' },
            { key: 'player2Wins', label: 'Победы игрока 2', type: 'text' }
        ]
    },
    {
        id: 'salary',
        label: 'Зарплата',
        title: 'H2H по зарплате',
        description: 'Сравнение зарплат с учетом винрейта и силы лиги (TOPSIS)',
        endpoint: '/api/tournament/compare/salary/h2h',
        matchKey: 'name',
        fields: []
    }
];