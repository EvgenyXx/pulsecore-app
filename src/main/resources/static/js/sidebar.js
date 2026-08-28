export async function initSidebar(activePage = null) {
    const container = document.getElementById('sidebarContainer');
    if (!container) return;

    let playerName = '';
    let isAdmin = false;

    try {
        const res = await fetch('/api/player/me', { credentials: 'same-origin' });
        if (res.ok) {
            const user = await res.json();
            playerName = user.name || '';
            isAdmin = user.admin === true;
        }
    } catch(e) {}

    const getNavItem = (id, page, label, sublabel, iconSvg, onClick, extraClass = '') => `
        <div class="nav-item ${activePage === page ? 'active' : ''} ${extraClass}" id="${id}" onclick="${onClick}">
            <span class="nav-icon">${iconSvg}</span>
            <div>
                <div class="font-medium text-sm">${label}</div>
                <div class="text-xs text-zinc-500">${sublabel}</div>
            </div>
        </div>
    `;

    container.innerHTML = `
        <aside class="hidden md:flex glass-sidebar w-72 min-h-screen p-6 flex-col relative z-10 shrink-0">
            <div class="flex items-center gap-3 mb-10 px-1">
                <img src="/img.png" alt="PulseCore" class="w-11 h-11 rounded-xl shadow-lg object-cover">
                <div>
                    <h1 class="text-lg font-bold bg-gradient-to-r from-indigo-400 to-indigo-300 bg-clip-text text-transparent tracking-tight">PulseCore</h1>
                    <p class="text-xs text-zinc-400">${playerName}</p>
                </div>
            </div>
            <nav class="flex flex-col gap-1 flex-1">
                ${getNavItem('nav-home', 'dashboard', 'Главная', 'Обзор',
        '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>',
        "window.location.href='/dashboard'")}
                ${getNavItem('nav-halls', 'halls', 'Расписание', 'Турниры и составы',
        '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>',
        "window.location.href='/dashboard?page=halls'")}
                ${getNavItem('nav-compare', 'compare', 'Сравнение', 'Игроки',
        '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 3h5v5"/><path d="M8 3H3v5"/><path d="M21 3l-7 7"/><path d="M3 3l7 7"/><path d="M16 21h5v-5"/><path d="M8 21H3v-5"/><path d="M21 21l-7-7"/><path d="M3 21l7 7"/></svg>',
        "window.location.href='/compare'")}
                ${getNavItem('nav-sum', 'sum', 'Сумма за период', 'Подсчёт и список',
        '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>',
        "window.location.href='/dashboard?page=sum'")}
                ${getNavItem('nav-live', 'live', 'Live', 'Трансляции',
        '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2" ry="2"/></svg>',
        "window.location.href='/live'")}
                ${getNavItem('nav-analytics', 'analytics', 'Аналитика', 'Статистика по лигам',
        '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>',
        "window.location.href='/analytics'")}
                ${isAdmin ? getNavItem('nav-admin', 'admin', 'Админка', '',
        '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>',
        "window.location.href='/admin'", 'admin-item') : ''}
                ${getNavItem('nav-profile', 'profile', 'Настройки', 'Аккаунт и подписка',
        '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>',
        "window.location.href='/profile'")}
            </nav>
            <button onclick="logout()" class="flex items-center gap-2 text-zinc-500 hover:text-red-400 text-sm mt-auto py-2 px-3 rounded-lg hover:bg-red-500/5 w-full">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                <span>Выйти</span>
            </button>
        </aside>
    `;

    window.logout = async function() {
        await fetch('/api/player/logout', { method: 'POST', credentials: 'same-origin' });
        window.location.href = '/';
    };
}