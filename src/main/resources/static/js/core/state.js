export const state = {
    playerId: null,
    playerName: '',
    isAdmin: false,
    primaryLeague: 'A',
    editingTournamentResultId: null,
    currentPeriod: 'week',
    currentSumPage: 0,
    selectedHalls: [],
    hallsDate: new Date().toISOString().split('T')[0]
};