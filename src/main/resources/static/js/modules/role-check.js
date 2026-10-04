export async function initRoleCheck() {
    try {
        const me = await window.Me.load();
        if (!me) return;

        const isAdmin = window.Me.isAdmin(me);
        const isSeller = window.Me.isSeller(me);

        if (isAdmin) {
            document.getElementById('nav-admin-item')?.classList.remove('hidden');
        }

        if (isAdmin || isSeller) {
            document.getElementById('nav-seller-item')?.classList.remove('hidden');
        }
    } catch (e) {}
}