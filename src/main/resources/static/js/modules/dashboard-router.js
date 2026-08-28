export function initDashboardRouter() {
    function showHome() {
        const homePage = document.getElementById('homePage');
        const actionPage = document.getElementById('actionPage');
        const livePage = document.getElementById('livePage');
        const liveTournamentScreen = document.getElementById('liveTournamentScreen');
        const comparePage = document.getElementById('comparePage');

        homePage.style.display = '';
        homePage.style.opacity = '1';
        actionPage.classList.add('hidden');
        livePage.classList.add('hidden');
        liveTournamentScreen.classList.add('hidden');
        comparePage.classList.add('hidden');
        comparePage.style.display = 'none';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-home')?.classList.add('active');
    }

    function showHalls() {
        const homePage = document.getElementById('homePage');
        const actionPage = document.getElementById('actionPage');
        const livePage = document.getElementById('livePage');
        const liveTournamentScreen = document.getElementById('liveTournamentScreen');
        const comparePage = document.getElementById('comparePage');

        homePage.style.display = 'none';
        livePage.classList.add('hidden');
        liveTournamentScreen.classList.add('hidden');
        comparePage.classList.add('hidden');
        comparePage.style.display = 'none';
        actionPage.classList.remove('hidden');
        actionPage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-halls')?.classList.add('active');

        if (window.showAction) {
            window.showAction('halls');
        }
    }

    function showSum() {
        const homePage = document.getElementById('homePage');
        const actionPage = document.getElementById('actionPage');
        const livePage = document.getElementById('livePage');
        const liveTournamentScreen = document.getElementById('liveTournamentScreen');
        const comparePage = document.getElementById('comparePage');

        homePage.style.display = 'none';
        livePage.classList.add('hidden');
        liveTournamentScreen.classList.add('hidden');
        comparePage.classList.add('hidden');
        comparePage.style.display = 'none';
        actionPage.classList.remove('hidden');
        actionPage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-sum')?.classList.add('active');

        if (window.showAction) {
            window.showAction('sum');
        }
    }

    function showLive() {
        const homePage = document.getElementById('homePage');
        const actionPage = document.getElementById('actionPage');
        const livePage = document.getElementById('livePage');
        const liveTournamentScreen = document.getElementById('liveTournamentScreen');
        const comparePage = document.getElementById('comparePage');

        homePage.style.display = 'none';
        actionPage.classList.add('hidden');
        liveTournamentScreen.classList.add('hidden');
        comparePage.classList.add('hidden');
        comparePage.style.display = 'none';
        livePage.classList.remove('hidden');
        livePage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-live')?.classList.add('active');

        if (window.loadLive) {
            window.loadLive();
        }
    }

    function showLiveTournament(externalId) {
        const homePage = document.getElementById('homePage');
        const actionPage = document.getElementById('actionPage');
        const livePage = document.getElementById('livePage');
        const liveTournamentScreen = document.getElementById('liveTournamentScreen');
        const comparePage = document.getElementById('comparePage');

        homePage.style.display = 'none';
        actionPage.classList.add('hidden');
        livePage.classList.add('hidden');
        comparePage.classList.add('hidden');
        comparePage.style.display = 'none';
        liveTournamentScreen.classList.remove('hidden');
        liveTournamentScreen.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-live')?.classList.add('active');

        if (window.loadTournamentData) {
            window.loadTournamentData(externalId);
        }
    }

    function showCompare() {
        const homePage = document.getElementById('homePage');
        const actionPage = document.getElementById('actionPage');
        const livePage = document.getElementById('livePage');
        const liveTournamentScreen = document.getElementById('liveTournamentScreen');
        const comparePage = document.getElementById('comparePage');

        homePage.style.display = 'none';
        actionPage.classList.add('hidden');
        livePage.classList.add('hidden');
        liveTournamentScreen.classList.add('hidden');
        comparePage.classList.remove('hidden');
        comparePage.style.display = 'flex';
        comparePage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-compare')?.classList.add('active');

        if (window.initCompareApp) {
            window.initCompareApp();
        }
    }

    function handleRoute() {
        const hash = window.location.hash || '';

        if (hash.startsWith('#/live/')) {
            const externalId = hash.replace('#/live/', '');
            showLiveTournament(externalId);
            return;
        }

        switch(hash) {
            case '#/halls':
                showHalls();
                break;
            case '#/sum':
                showSum();
                break;
            case '#/live':
                showLive();
                break;
            case '#/compare':
                showCompare();
                break;
            default:
                showHome();
                break;
        }
    }

    window.addEventListener('hashchange', handleRoute);
    handleRoute();

    // Экспортируем для глобального доступа
    window.navigate = function(page) {
        if (page === 'halls') window.location.hash = '#/halls';
        else if (page === 'sum') window.location.hash = '#/sum';
        else if (page === 'live') window.location.hash = '#/live';
        else if (page === 'compare') window.location.hash = '#/compare';
        else if (page === 'home') window.location.hash = '#/';
        else if (typeof page === 'number' || /^\d+$/.test(page)) window.location.hash = '#/live/' + page;
    };

    // Экспортируем openTournament
    window.openTournament = function(externalId) {
        window.location.hash = '#/live/' + externalId;
    };
}