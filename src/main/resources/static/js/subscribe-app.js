import { SubscribeAPI } from './core/subscribe-api.js';
import { state } from './core/state.js';
import { loadPrices, loadSubscription, selectPlan, handlePay, logout } from './modules/subscribe.js';

// Глобальные функции
window.selectPlan = selectPlan;
window.handlePay = handlePay;
window.logout = logout;

async function init() {
    try {
        const user = await SubscribeAPI.getMe();
        if (!user || !user.id) { window.location.href = '/'; return; }
        state.playerId = user.id;

        await Promise.all([loadPrices(), loadSubscription()]);
    } catch (e) {
        window.location.href = '/';
    }
}

document.addEventListener('DOMContentLoaded', init);