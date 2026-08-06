import { state } from './state.js';

const BASE = '/api';

async function apiRequest(endpoint, options = {}) {
    const res = await fetch(`${BASE}${endpoint}`, { credentials: 'same-origin', ...options });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

export const SubscribeAPI = {
    getMe: () => apiRequest('/auth/me'),
    getPrices: () => apiRequest('/payment/prices'),
    getSubscription: () => apiRequest('/player/subscription'),
    createPayment: (months) => apiRequest(`/payment/pay?months=${months}`, { method: 'POST' }),
    logout: () => fetch(`${BASE}/auth/logout`, { method: 'POST', credentials: 'same-origin' })
};