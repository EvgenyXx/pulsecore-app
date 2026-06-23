export async function initRoleCheck() {
    try {
        const res = await fetch('/api/auth/me', { credentials: 'same-origin' });
        if (!res.ok) return;
        const user = await res.json();
        const isAdmin = user.admin === true;

        if (isAdmin) {
            const desktopAdmin = document.getElementById('nav-admin');
            if (desktopAdmin) desktopAdmin.classList.remove('hidden');

            const mobileAdmin = document.getElementById('mobile-nav-admin');
            if (mobileAdmin) mobileAdmin.classList.remove('hidden');
        }
    } catch (e) {
        // не авторизован — ничего не делаем
    }
}