export function initBurgerMenu() {
    const html = `
        <div id="mobileMenuOverlay" class="md:hidden fixed inset-0 bg-black/60 backdrop-blur-sm z-40 hidden" onclick="window.toggleMobileMenu()"></div>
        <div id="mobileMenu" class="md:hidden fixed right-0 h-full w-[300px] max-w-[85vw] glass-sidebar z-50 p-5 flex flex-col transform translate-x-full transition-transform duration-300 ease-out shadow-2xl" onclick="event.stopPropagation()">
            <div class="flex items-center justify-between mb-6">
                <div class="flex items-center gap-3">
                    <img src="/img.png" alt="PulseCore" class="w-8 h-8 rounded-xl shadow-lg object-cover">
                    <span class="font-bold text-white text-sm">PulseCore</span>
                </div>
                <button onclick="window.toggleMobileMenu()" class="w-9 h-9 flex items-center justify-center rounded-xl bg-white/5 hover:bg-white/10 active:scale-90 text-white text-lg">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                </button>
            </div>
            <nav class="flex flex-col gap-1.5 flex-1 overflow-y-auto">
                <a href="/dashboard" class="mobile-nav-item" onclick="window.toggleMobileMenu()">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                    Главная
                </a>
                <a href="/dashboard?page=halls" class="mobile-nav-item" onclick="window.toggleMobileMenu()">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                    Расписание
                </a>
                <a href="/dashboard?page=sum" class="mobile-nav-item" onclick="window.toggleMobileMenu()">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                    Сумма за период
                </a>
                <a href="/live" class="mobile-nav-item" onclick="window.toggleMobileMenu()">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2" ry="2"/></svg>
                    Live
                </a>
                <a href="/analytics" class="mobile-nav-item" onclick="window.toggleMobileMenu()">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
                    Аналитика
                </a>
                <a href="/profile" class="mobile-nav-item" onclick="window.toggleMobileMenu()">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
                    Настройки
                </a>
                <a href="/admin" class="mobile-nav-item hidden" id="mobile-nav-admin" style="background: rgba(16,185,129,0.12); border-left: 3px solid #34d399; color: #a7f3d0; font-weight: 500;" onclick="window.toggleMobileMenu()">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                    Админка
                </a>
            </nav>
        </div>
    `;

    const container = document.getElementById('burgerMenuContainer');
    if (container) {
        container.innerHTML = html;
        import('/js/modules/role-check.js').then(m => m.initRoleCheck());
    }

    window.toggleMobileMenu = function() {
        const menu = document.getElementById('mobileMenu');
        const overlay = document.getElementById('mobileMenuOverlay');
        if (!menu || !overlay) return;
        const isOpen = !menu.classList.contains('translate-x-full');
        if (isOpen) {
            menu.classList.add('translate-x-full');
            overlay.classList.add('hidden');
        } else {
            menu.classList.remove('translate-x-full');
            overlay.classList.remove('hidden');
        }
    };
}