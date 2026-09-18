// js/modules/bottom-tab.js
export function initBottomTab(activePage = null) {
    if (window.innerWidth >= 768) return;
    if (document.getElementById('bottomTab')) return;

    const isDashboard = window.location.pathname === '/dashboard' || window.location.pathname === '/';
    const links = {
        home:    isDashboard ? '#/' : '/dashboard',
        halls:   isDashboard ? '#/halls' : '/dashboard#/halls',
        sum:     isDashboard ? '#/sum' : '/dashboard#/sum',
        more:    isDashboard ? '#/more' : '/more',
        profile: isDashboard ? '#/profile' : '/profile',
    };

    const items = [
        { id: 'home',    label: 'Главная',    href: links.home,
            icon: '<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>' },
        { id: 'halls',   label: 'Расписание', href: links.halls,
            icon: '<rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>' },
        { id: 'sum',     label: 'Сумма',      href: links.sum,
            icon: '<line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>' },
        { id: 'more',    label: 'Прочее',     href: links.more,
            icon: '<circle cx="5" cy="12" r="1.8"/><circle cx="12" cy="12" r="1.8"/><circle cx="19" cy="12" r="1.8"/>' },
        { id: 'profile', label: 'Профиль',    href: links.profile,
            icon: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>' }
    ];

    const nav = document.createElement('nav');
    nav.id = 'bottomTab';
    nav.className = 'bottom-tab';
    nav.innerHTML = items.map(it => `
        <a href="${it.href}" class="bottom-tab-item" data-tab="${it.id}">
            <span class="bottom-tab-icon" data-anim="${it.id}">
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    ${it.icon}
                </svg>
            </span>
            <span class="bottom-tab-label">${it.label}</span>
        </a>
    `).join('');

    document.body.appendChild(nav);
    document.body.classList.add('has-bottom-tab');

    nav.querySelectorAll('.bottom-tab-item').forEach(el => {
        el.addEventListener('click', (e) => {
            nav.querySelectorAll('.bottom-tab-item').forEach(i => i.classList.remove('active'));
            el.classList.add('active');

            const icon = el.querySelector('.bottom-tab-icon');
            if (icon) {
                icon.classList.remove('icon-anim');
                void icon.offsetWidth;
                icon.classList.add('icon-anim');
                setTimeout(() => icon.classList.remove('icon-anim'), 400);
            }

            const rect = el.getBoundingClientRect();
            const x = (e.clientX || rect.left + rect.width / 2) - rect.left;
            const y = (e.clientY || rect.top + rect.height / 2) - rect.top;
            const ripple = document.createElement('span');
            ripple.className = 'bottom-tab-ripple';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            el.appendChild(ripple);
            setTimeout(() => ripple.remove(), 500);
        });
    });

    autoHighlight(activePage);
    watchLiveScreen();
}

function autoHighlight(activePage) {
    const path = window.location.pathname + window.location.hash;
    document.querySelectorAll('.bottom-tab-item').forEach(el => el.classList.remove('active'));

    if (activePage) {
        const el = document.querySelector(`.bottom-tab-item[data-tab="${activePage}"]`);
        if (el) { el.classList.add('active'); return; }
    }

    let found = 'home';
    if (path.includes('/halls')) found = 'halls';
    else if (path.includes('/sum')) found = 'sum';
    else if (path.includes('/more')) found = 'more';
    else if (path.includes('/profile')) found = 'profile';
    else if (path.includes('/live') || path.includes('/analytics') || path.includes('/compare')) found = 'more';

    const el = document.querySelector(`.bottom-tab-item[data-tab="${found}"]`);
    if (el) el.classList.add('active');
}

// Скрываем нижнюю панель на экране трансляции
function watchLiveScreen() {
    function toggle() {
        const liveScreen = document.getElementById('liveTournamentScreen');
        const nav = document.getElementById('bottomTab');
        if (!nav) return;

        const isLiveOpen = liveScreen && !liveScreen.classList.contains('hidden');
        nav.style.display = isLiveOpen ? 'none' : '';
        document.body.style.paddingBottom = isLiveOpen ? '' : '';
    }

    // Следим за изменениями hash и класса hidden
    window.addEventListener('hashchange', toggle);

    const liveScreen = document.getElementById('liveTournamentScreen');
    if (liveScreen) {
        const observer = new MutationObserver(toggle);
        observer.observe(liveScreen, { attributes: true, attributeFilter: ['class', 'style'] });
    }

    toggle();
}