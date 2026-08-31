export function initLiveRouter() {
    window.showLiveList = function() {
        document.getElementById('liveTournamentScreen').classList.add('hidden');
        document.getElementById('liveListScreen').classList.remove('hidden');
        window.location.hash = '';

        // Перерисовываем список при возврате
        if (window.applyFilter) {
            window.applyFilter();
        }
    };

    window.showLiveTournament = function() {
        document.getElementById('liveListScreen').classList.add('hidden');
        document.getElementById('liveTournamentScreen').classList.remove('hidden');
    };

    window.openTournament = function(externalId) {
        window.location.hash = '/live/' + externalId;
        showLiveTournament();
        if (window.loadTournamentData) {
            window.loadTournamentData(externalId);
        }
    };

    window.addEventListener('hashchange', () => {
        const hash = window.location.hash;
        if (hash.startsWith('#/live/')) {
            const externalId = hash.replace('#/live/', '');
            showLiveTournament();
            if (window.loadTournamentData) {
                window.loadTournamentData(externalId);
            }
        } else {
            showLiveList();
        }
    });
}