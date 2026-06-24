const BASE = '/api';

async function apiRequest(endpoint, options = {}) {
    const res = await fetch(`${BASE}${endpoint}`, { credentials: 'same-origin', ...options });
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error(err.message || `HTTP ${res.status}`);
    }
    return res.json();
}

export const ProfileAPI = {
    getMe: () => apiRequest('/auth/me'),
    getNotificationStatus: () => apiRequest('/player/notifications/status'),
    setNotification: (enabled) => apiRequest(`/player/notifications?enabled=${enabled}`, {
        method: 'PUT'
    }),
    getPushStatus: () => apiRequest('/push/push-status'),
    togglePush: () => apiRequest('/push/toggle', { method: 'POST' }),
    getSubscription: () => apiRequest('/player/subscription'),
    verifyPassword: (password) => apiRequest('/auth/verify-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password })
    }),
    changePassword: (oldPassword, newPassword) => apiRequest('/player/change-password', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ oldPassword, newPassword })
    }),
    saveEmail: (email) => apiRequest('/player/profile', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
    }),
    logout: () => fetch(`${BASE}/auth/logout`, { method: 'POST', credentials: 'same-origin' })
};