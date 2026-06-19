const BASE = '/api';

async function apiRequest(endpoint, options = {}) {
    const res = await fetch(`${BASE}${endpoint}`, { credentials: 'same-origin', ...options });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.message || `HTTP ${res.status}`);
    }
    return res.json();
}

export const AdminAPI = {
    getMe: () => apiRequest('/auth/me'),
    searchPlayers: (q) => apiRequest(`/player/search?q=${encodeURIComponent(q)}`),
    getPlayerSubscription: (playerId) => apiRequest(`/admin/players/${playerId}/subscription`),
    getPlayerRoles: (playerId) => apiRequest(`/admin/players/${playerId}/roles`),
    deletePlayerTournaments: (playerId) => apiRequest(`/admin/players/${playerId}/tournaments`, { method: 'DELETE' }),
    resyncPlayerTournaments: (playerId) => apiRequest(`/admin/players/${playerId}/tournaments/resync`, { method: 'POST' }),
    deletePlayerAccount: (playerId) => apiRequest(`/admin/players/${playerId}`, { method: 'DELETE' }),
    togglePlayerRole: (playerId, role, isGrant) => apiRequest(`/admin/players/${playerId}/roles/${isGrant ? 'grant' : 'revoke'}?role=${role}`, { method: isGrant ? 'POST' : 'DELETE' }),
    giveSubscription: (playerId, days) => apiRequest(`/admin/players/${playerId}/subscribe?days=${days}`, { method: 'POST' }),
    removeSubscription: (playerId) => apiRequest(`/admin/players/${playerId}/unsubscribe`, { method: 'DELETE' }),
    getPrices: () => apiRequest('/prices'),
    updatePrices: (prices) => apiRequest('/admin/prices', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(prices)
    }),
    calculatePlayer: (data) => apiRequest('/admin/tournaments/calculate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }),
    broadcast: (message) => apiRequest('/admin/broadcast', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message })
    }),
    getPageStats: (days) => apiRequest(`/admin/stats/page-views?days=${days}`),
    getPlayerStats: (days) => apiRequest(`/admin/stats/page-views/players?days=${days}`),
    logout: () => fetch(`${BASE}/auth/logout`, { method: 'POST', credentials: 'same-origin' })
};