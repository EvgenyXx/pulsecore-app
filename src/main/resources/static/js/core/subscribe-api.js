import { state } from './state.js';

const BASE = '/api';

async function apiRequest(endpoint, options = {}) {
    const res = await fetch(`${BASE}${endpoint}`, { credentials: 'same-origin', ...options });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

export const SubscribeAPI = {
    getMe: () => apiRequest('/auth/me'),
    getPrices: () => apiRequest('/prices'),
    getSubscription: () => apiRequest('/player/subscription'),
    createPayment: (months) => apiRequest(`/player/pay?months=${months}`, { method: 'POST' }),
    logout: () => fetch(`${BASE}/auth/logout`, { method: 'POST', credentials: 'same-origin' })
};