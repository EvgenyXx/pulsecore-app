import { AdminAPI } from './core/admin-api.js';
import { searchPlayers, selectPlayer, togglePlayerRole, deletePlayerTournaments, resyncPlayerTournaments, deletePlayerAccount } from './modules/admin-players.js';
import { giveSub, removeSub, setSelectedPlayer } from './modules/admin-subscriptions.js';
import { loadCurrentPrices, updatePrices } from './modules/admin-prices.js';
import { adminCalculate } from './modules/admin-calculate.js';
import { sendBroadcast } from './modules/admin-broadcast.js';
import { loadPageStats } from './modules/admin-stats.js';

window.searchPlayers = searchPlayers;
window.selectPlayer = (id, name, email, section) => {
    setSelectedPlayer(id);
    selectPlayer(id, name, email, section);
};
window.togglePlayerRole = togglePlayerRole;
window.deletePlayerTournaments = deletePlayerTournaments;
window.resyncPlayerTournaments = resyncPlayerTournaments;
window.deletePlayerAccount = deletePlayerAccount;
window.giveSub = giveSub;
window.removeSub = removeSub;
window.loadCurrentPrices = loadCurrentPrices;
window.updatePrices = updatePrices;
window.adminCalculate = adminCalculate;
window.sendBroadcast = sendBroadcast;
window.loadPageStats = loadPageStats;
window.logout = logout;
window.showSection = showSection;

function showSection(s) {
    document.querySelectorAll('.nav-item').forEach(e => e.classList.remove('active'));
    document.querySelectorAll('.mobile-nav-item').forEach(e => e.classList.remove('active'));
    document.getElementById('nav-' + s)?.classList.add('active');
    document.getElementById('mob-nav-' + s)?.classList.add('active');
    ['players', 'subscriptions', 'prices', 'calculate', 'broadcast', 'stats'].forEach(sec => {
        document.getElementById('section-' + sec).classList.toggle('hidden', sec !== s);
    });
    if (s === 'stats') loadPageStats();
}

async function logout() {
    await AdminAPI.logout();
    window.location.replace('/');
}

async function init() {
    try {
        const user = await AdminAPI.getMe();
        if (!user || !user.admin) {
            window.location.href = '/dashboard';
            return;
        }
    } catch (e) {
        window.location.href = '/';
    }
}

document.addEventListener('DOMContentLoaded', init);