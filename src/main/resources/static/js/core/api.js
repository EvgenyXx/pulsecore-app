export async function apiRequest(endpoint, options = {}) {
    const res = await fetch(endpoint, { credentials: 'same-origin', ...options });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

export const API = {
    getMe: () => apiRequest('/api/player/me'),
    logout: () => fetch('/api/player/logout', { method: 'POST', credentials: 'same-origin' }),
    getDashboard: (playerId) => apiRequest(`/api/tournament/${playerId}/dashboard`),
    getHalls: () => apiRequest('/api/player/halls'),
    saveHalls: (hallsStr) => fetch('/api/player/halls', {
        method: 'PUT',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ halls: hallsStr })
    }),
    getMyLineups: (date) => apiRequest(`/api/tournament/my?date=${date}`),
    getAllLineups: (date) => apiRequest(`/api/tournament/all?date=${date}`),
    getTop: (period, league) => {
        const url = league ? `/api/tournament/top/${period}/${league}` : `/api/tournament/top/${period}`;
        return apiRequest(url);
    },
    getSum: (params) => {
        const query = new URLSearchParams(params).toString();
        return apiRequest(`/api/tournament/sum?${query}`);
    },
    updateResult: (resultId, amount, bonus) => fetch(`/api/tournament/result/${resultId}`, {
        method: 'PUT',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount, bonus })
    }),
    getVapidKey: () => fetch('/api/push/vapid-public-key', { headers: { 'Accept': 'text/plain' } }).then(r => r.text()),
    subscribePush: (data) => fetch('/api/push/subscribe', {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }),
    unsubscribePush: (data) => fetch('/api/push/unsubscribe', {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
};