// js/admin/admin-bottom-tab.js
const ADMIN_TABS = [
    {
        id: 'players', label: 'Игроки',
        icon: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>'
    },
    {
        id: 'subscriptions', label: 'Подписки',
        icon: '<rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/>'
    },
    {
        id: 'tournaments', label: 'Турниры',
        icon: '<circle cx="12" cy="8" r="6"/><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11"/>'
    },
    {
        id: 'stats', label: 'Статистика',
        icon: '<line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>'
    }
];

export function initAdminBottomTab(activeTab = 'players') {
    if (window.innerWidth >= 768) return;
    if (document.getElementById('adminBottomTab')) return;

    const nav = document.createElement('nav');
    nav.id = 'adminBottomTab';
    nav.className = 'bottom-tab';

    nav.innerHTML = `
        <div class="bottom-tab-indicator" id="adminBottomTabIndicator"></div>
        ${ADMIN_TABS.map(it => `
            <button class="bottom-tab-item" data-tab="${it.id}" type="button">
                <span class="bottom-tab-icon" data-anim="${it.id}">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        ${it.icon}
                    </svg>
                </span>
                <span class="bottom-tab-label">${it.label}</span>
            </button>
        `).join('')}
    `;

    (document.getElementById('appRoot') || document.body).appendChild(nav);
    document.body.classList.add('has-bottom-tab');

    const indicator = nav.querySelector('#adminBottomTabIndicator');
    const positions = Object.fromEntries(ADMIN_TABS.map((t, i) => [t.id, i]));
    const total = ADMIN_TABS.length;
    let moveTimeout = null;

    function updateIndicator(tabId) {
        const pos = positions[tabId];
        if (pos === undefined) return;
        indicator.classList.add('is-moving');
        indicator.style.left = `${(pos / total) * 100}%`;
        indicator.style.width = `${100 / total}%`;
        clearTimeout(moveTimeout);
        moveTimeout = setTimeout(() => indicator.classList.remove('is-moving'), 400);
    }

    nav.querySelectorAll('.bottom-tab-item').forEach(el => {
        el.addEventListener('click', () => {
            if (navigator.vibrate) navigator.vibrate(10);
            const tabId = el.dataset.tab;

            nav.querySelectorAll('.bottom-tab-item').forEach(i => i.classList.remove('active'));
            el.classList.add('active');
            updateIndicator(tabId);

            const icon = el.querySelector('.bottom-tab-icon');
            if (icon) {
                icon.classList.remove('icon-anim');
                void icon.offsetWidth;
                icon.classList.add('icon-anim');
                setTimeout(() => icon.classList.remove('icon-anim'), 400);
            }

            showAdminSection(tabId);
        });
    });

    setTimeout(() => {
        const el = nav.querySelector(`.bottom-tab-item[data-tab="${activeTab}"]`);
        if (el) el.classList.add('active');
        updateIndicator(activeTab);
        showAdminSection(activeTab);
    }, 50);
}

function showAdminSection(tabId) {
    document.querySelectorAll('[id^="section-"]').forEach(s => s.classList.add('hidden'));
    const target = document.getElementById(`section-${tabId}`);
    if (target) target.classList.remove('hidden');

    if (tabId === 'tournaments') {
        const subT = document.getElementById('sub-tournaments');
        const subS = document.getElementById('sub-scheduler');
        if (subT) subT.style.display = 'none';
        if (subS) subS.style.display = 'block';
        const seg = document.getElementById('seg-tournaments');
        if (seg) {
            seg.querySelectorAll('.segmented-btn').forEach(b => b.classList.remove('active'));
            const first = seg.querySelector('[data-sub="scheduler"]');
            if (first) first.classList.add('active');
        }
        if (window.loadSchedulerStatus) window.loadSchedulerStatus();
    }

    if (tabId === 'subscriptions' && window.loadCurrentPrices) {
        window.loadCurrentPrices();
    }

    if (tabId === 'stats' && window.loadPageStats) window.loadPageStats();

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

/* =====================================================
   СЕГМЕНТ-КОНТРОЛЫ
   ===================================================== */

export function initAdminSegments() {
    bindSegment('seg-players', (subId) => {
        const section = document.getElementById('section-players');
        if (!section) return;
        section.querySelectorAll('.sub-panel').forEach(w => w.style.display = 'none');
        const panel = document.getElementById(`${subId}-panel`);
        if (panel) panel.style.display = 'block';

        if (subId === 'acc-subs-overview' && window.loadSubscriptionsOverview) window.loadSubscriptionsOverview();
        if (subId === 'acc-last-login' && window.loadLastLogin) window.loadLastLogin(0);
    });

    bindSegment('seg-tournaments', (subId) => {
        const t = document.getElementById('sub-tournaments');
        const s = document.getElementById('sub-scheduler');
        if (!t || !s) return;
        if (subId === 'scheduler') {
            t.style.display = 'none';
            s.style.display = 'block';
            if (window.loadSchedulerStatus) window.loadSchedulerStatus();
        } else {
            s.style.display = 'none';
            t.style.display = 'block';
            const inp = document.getElementById('tournamentDateInput');
            const today = new Date();
            const todayStr = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`;
            if (inp && !inp.value) inp.value = todayStr;
            if (window.loadTournaments) window.loadTournaments(inp?.value || todayStr);
        }
    });

    activateFirst('seg-players');
    activateFirst('seg-tournaments');
}

function bindSegment(segId, onSwitch) {
    const seg = document.getElementById(segId);
    if (!seg) return;
    seg.querySelectorAll('.segmented-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            if (navigator.vibrate) navigator.vibrate(8);
            seg.querySelectorAll('.segmented-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            onSwitch(btn.dataset.sub);
        });
    });
}

function activateFirst(segId) {
    const seg = document.getElementById(segId);
    if (!seg) return;
    const first = seg.querySelector('.segmented-btn');
    if (!first) return;
    first.classList.add('active');
    first.click();
}