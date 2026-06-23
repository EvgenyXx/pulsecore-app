export const state = {
    playerId: null,
    isAdmin: false,
    primaryLeague: 'A',
    editingTournamentResultId: null,
    currentPeriod: 'week',
    currentSumPage: 0,
    selectedHalls: [],
    hallsDate: new Date().toISOString().split('T')[0],
    hasSubscription: false
};