export function initDashboardRouter() {
    const PAGES = ['homePage','actionPage','livePage','liveTournamentScreen','comparePage','profilePage','analyticsPage','morePage'];

    function hideAll() {
        PAGES.forEach(id => {
            const el = document.getElementById(id);
            if (el) { el.classList.add('hidden'); el.style.display = 'none'; }
        });
    }

    function showHome() {
        hideAll();
        const homePage = document.getElementById('homePage');
        homePage.classList.remove('hidden');
        homePage.style.display = '';
        homePage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-home')?.classList.add('active');

        if (window.loadDashboardWidgets) window.loadDashboardWidgets();
        if (window.loadTopWeek) window.loadTopWeek(null);
        if (window.loadSelectedHalls) window.loadSelectedHalls();
    }

    function showHalls() {
        hideAll();
        const actionPage = document.getElementById('actionPage');
        actionPage.classList.remove('hidden');
        actionPage.style.display = '';
        actionPage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-halls')?.classList.add('active');

        if (window.showAction) window.showAction('halls');
    }

    function showSum() {
        hideAll();
        const actionPage = document.getElementById('actionPage');
        actionPage.classList.remove('hidden');
        actionPage.style.display = '';
        actionPage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-sum')?.classList.add('active');

        if (window.showAction) window.showAction('sum');
    }

    function showLive() {
        hideAll();
        const livePage = document.getElementById('livePage');
        livePage.classList.remove('hidden');
        livePage.style.display = '';
        livePage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-live')?.classList.add('active');

        if (window.loadLive) window.loadLive();
    }

    function showLiveTournament(externalId) {
        hideAll();
        const liveTournamentScreen = document.getElementById('liveTournamentScreen');
        liveTournamentScreen.classList.remove('hidden');
        liveTournamentScreen.style.display = '';
        liveTournamentScreen.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-live')?.classList.add('active');

        if (window.loadTournamentData) window.loadTournamentData(externalId);
    }

    function showCompare() {
        hideAll();
        const comparePage = document.getElementById('comparePage');
        comparePage.classList.remove('hidden');
        comparePage.style.display = 'flex';
        comparePage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-compare')?.classList.add('active');

        if (window.initCompareApp) window.initCompareApp();
    }

    function showProfile() {
        hideAll();
        const profilePage = document.getElementById('profilePage');
        profilePage.classList.remove('hidden');
        profilePage.style.display = 'block';
        profilePage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-profile')?.classList.add('active');

        if (window.initProfileApp) window.initProfileApp();
    }

    function showAnalytics() {
        hideAll();
        const analyticsPage = document.getElementById('analyticsPage');
        analyticsPage.classList.remove('hidden');
        analyticsPage.style.display = 'block';
        analyticsPage.style.opacity = '1';

        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.getElementById('nav-analytics')?.classList.add('active');

        if (window.initAnalyticsApp) window.initAnalyticsApp();
        else if (window.switchTab) window.switchTab('league');
    }

    function showMore() {
        hideAll();
        const morePage = document.getElementById('morePage');
        if (!morePage) return;
        morePage.classList.remove('hidden');
        morePage.style.display = 'block';
        morePage.style.opacity = '1';
    }

    function handleRoute() {
        const hash = window.location.hash || '';

        if (hash.startsWith('#/live/')) {
            const externalId = hash.replace('#/live/', '');
            showLiveTournament(externalId);
            return;
        }

        switch(hash) {
            case '#/halls':     showHalls();     break;
            case '#/sum':       showSum();       break;
            case '#/live':      showLive();      break;
            case '#/compare':   showCompare();   break;
            case '#/profile':   showProfile();   break;
            case '#/analytics': showAnalytics(); break;
            case '#/more':      showMore();      break;
            default:            showHome();      break;
        }
    }

    window.addEventListener('hashchange', handleRoute);
    handleRoute();

    window.navigate = function(page) {
        if (page === 'halls') window.location.hash = '#/halls';
        else if (page === 'sum') window.location.hash = '#/sum';
        else if (page === 'live') window.location.hash = '#/live';
        else if (page === 'compare') window.location.hash = '#/compare';
        else if (page === 'profile') window.location.hash = '#/profile';
        else if (page === 'analytics') window.location.hash = '#/analytics';
        else if (page === 'more') window.location.hash = '#/more';
        else if (page === 'home') window.location.hash = '#/';
        else if (typeof page === 'number' || /^\d+$/.test(page)) window.location.hash = '#/live/' + page;
    };

    window.openTournament = function(externalId) {
        window.location.hash = '#/live/' + externalId;
    };
}