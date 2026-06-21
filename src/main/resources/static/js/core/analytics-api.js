import { state } from './state.js';

const BASE = '/api';

async function apiRequest(endpoint, options = {}) {
    const res = await fetch(`${BASE}${endpoint}`, { credentials: 'same-origin', ...options });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

export const AnalyticsAPI = {
    getMe: () => apiRequest('/auth/me'),
    getSubscription: () => apiRequest('/player/subscription'),
    getLeagueAvg: () => apiRequest('/player/analytics'),
    getMonthly: (year) => apiRequest(`/player/${state.playerId}/monthly-income?year=${year}`),
    getDaily: (year, month) => apiRequest(`/player/${state.playerId}/daily-income?year=${year}&month=${month}`),
    getBestTime: (params) => {
        const query = new URLSearchParams(params).toString();
        return apiRequest(`/player/best-time?${query}`);
    }
};

export async function checkSubscription() {
    try {
        const sub = await AnalyticsAPI.getSubscription();
        return sub && sub.active;
    } catch (e) {
        return false;
    }
}