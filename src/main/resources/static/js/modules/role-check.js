export async function initRoleCheck() {
    try {
        const res = await fetch('/api/player/me', { credentials: 'same-origin' });
        if (!res.ok) return;
        const user = await res.json();
        const isAdmin = user.admin === true;

        if (isAdmin) {
            // Десктопный сайдбар
            const desktopAdmin = document.getElementById('nav-admin');
            if (desktopAdmin) desktopAdmin.classList.remove('hidden');

            // Бургер-меню (старое, правое)
            const mobileAdmin = document.getElementById('mobile-nav-admin');
            if (mobileAdmin) mobileAdmin.classList.remove('hidden');

            // ПРОФИЛЬ — вкладка из нижней панели
            const profileAdmin = document.getElementById('nav-admin-item');
            if (profileAdmin) profileAdmin.classList.remove('hidden');
        }
    } catch (e) {}
}