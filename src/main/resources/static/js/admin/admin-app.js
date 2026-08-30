import { AdminAPI } from './admin-api.js';
import { searchPlayers, selectPlayer, togglePlayerRole, deletePlayerTournaments, resyncPlayerTournaments, deletePlayerAccount, updatePlayer, togglePlayerStatus } from './admin-players.js';
import { giveSub, removeSub, setSelectedPlayer } from './admin-subscriptions.js';
import { loadCurrentPrices, updatePrices } from './admin-prices.js';
import { adminCalculate } from './admin-calculate.js';
import { sendBroadcast } from './admin-broadcast.js';
import { loadPageStats } from './admin-stats.js';
import { loadTournaments, toggleTournamentExpand, toggleStatus, saveTournament } from './admin-tournaments.js';
import { loadSchedulerStatus, toggleScheduler } from './admin-scheduler.js';

window.searchPlayers = searchPlayers;
window.selectPlayer = (id, name, email, section) => {
    setSelectedPlayer(id);
    selectPlayer(id, name, email, section);
};
window.togglePlayerRole = togglePlayerRole;
window.deletePlayerTournaments = deletePlayerTournaments;
window.resyncPlayerTournaments = resyncPlayerTournaments;
window.deletePlayerAccount = deletePlayerAccount;
window.updatePlayer = updatePlayer;
window.togglePlayerStatus = togglePlayerStatus;
window.giveSub = giveSub;
window.removeSub = removeSub;
window.loadCurrentPrices = loadCurrentPrices;
window.updatePrices = updatePrices;
window.adminCalculate = adminCalculate;
window.sendBroadcast = sendBroadcast;
window.loadPageStats = loadPageStats;
window.loadTournaments = loadTournaments;
window.toggleTournamentExpand = toggleTournamentExpand;
window.toggleStatus = toggleStatus;
window.saveTournament = saveTournament;
window.loadSchedulerStatus = loadSchedulerStatus;
window.toggleScheduler = toggleScheduler;
window.logout = logout;
window.showSection = showSection;
window.toggleAdminSheet = toggleAdminSheet;
window.closeAdminSheet = closeAdminSheet;
window.toggleSubmenu = toggleSubmenu;
window.selectAction = selectAction;
window.openAccordion = openAccordion;
window.toggleAccordion = toggleAccordion;

function getTodayString() {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

function showSection(s) {
    document.querySelectorAll('.nav-item').forEach(e => e.classList.remove('active'));

    if (s === 'players' || s === 'calculate' || s === 'broadcast') {
        document.getElementById('nav-players')?.classList.add('active');
    } else if (s === 'subscriptions' || s === 'sub-manage' || s === 'prices') {
        document.getElementById('nav-subscriptions')?.classList.add('active');
    } else if (s === 'tournaments') {
        document.getElementById('nav-tournaments')?.classList.add('active');
    } else if (s === 'scheduler') {
        document.getElementById('nav-tournaments')?.classList.add('active');
    } else if (s === 'stats') {
        document.getElementById('nav-stats')?.classList.add('active');
    }

    const sections = ['players', 'subscriptions', 'tournaments', 'scheduler', 'stats'];
    sections.forEach(sec => {
        document.getElementById('section-' + sec)?.classList.toggle('hidden', sec !== s);
    });

    if (s === 'tournaments') {
        const dateInput = document.getElementById('tournamentDateInput');
        const today = getTodayString();
        if (dateInput && !dateInput.value) dateInput.value = today;
        loadTournaments(dateInput?.value || today);
    }

    if (s === 'scheduler') loadSchedulerStatus();

    if (s === 'stats') loadPageStats();
}

function toggleAdminSheet() {
    document.getElementById('adminSheetOverlay').classList.toggle('open');
}

function closeAdminSheet() {
    document.getElementById('adminSheetOverlay').classList.remove('open');
}

function toggleSubmenu(id) {
    const sub = document.getElementById(id);
    if (!sub) return;

    document.querySelectorAll('.sheet-accordion-body').forEach(b => {
        if (b.id !== id && b.id.startsWith('submenu-mobile')) {
            b.style.display = 'none';
            const otherArrow = document.getElementById('arrow-' + b.id);
            if (otherArrow) otherArrow.style.transform = 'rotate(0deg)';
        }
    });

    const isHidden = sub.style.display === 'none' || sub.style.display === '';
    sub.style.display = isHidden ? 'flex' : 'none';

    const arrow = document.getElementById('arrow-' + id);
    if (arrow) arrow.style.transform = isHidden ? 'rotate(180deg)' : 'rotate(0deg)';
}

function selectAction(section, accordionId, btn) {
    showSection(section);
    if (accordionId) openAccordion(accordionId);
    document.querySelectorAll('.submenu-btn').forEach(b => b.classList.remove('active'));
    if (btn) btn.classList.add('active');
    closeAdminSheet();
}

function openAccordion(id) {
    document.querySelectorAll('.sheet-accordion').forEach(acc => {
        if (!acc.closest('.period-sheet')) {
            acc.style.display = 'none';
        }
    });
    const wrapper = document.getElementById(id)?.closest('.sheet-accordion');
    if (wrapper) wrapper.style.display = '';
    const body = document.getElementById(id);
    if (body) body.style.display = 'block';
    const arrow = document.getElementById('arrow-' + id);
    if (arrow) arrow.style.transform = 'rotate(180deg)';
}

function toggleAccordion(id) {
    const body = document.getElementById(id);
    const arrow = document.getElementById('arrow-' + id);
    if (!body) return;
    document.querySelectorAll('.sheet-accordion-body').forEach(b => {
        if (b.id !== id) b.style.display = 'none';
    });
    body.style.display = (body.style.display === 'none' || body.style.display === '') ? 'block' : 'none';
    if (arrow) arrow.style.transform = body.style.display === 'block' ? 'rotate(180deg)' : 'rotate(0deg)';
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
    showSection('players');
}

document.addEventListener('DOMContentLoaded', init);