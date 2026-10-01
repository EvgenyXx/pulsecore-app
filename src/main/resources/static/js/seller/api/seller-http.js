window.SellerHttp = (function () {
    const BASE = '/api/seller';

    async function request(path, options = {}) {
        const res = await fetch(BASE + path, {
            credentials: 'same-origin',
            ...options
        });

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.message || `HTTP ${res.status}`);
        }

        if (res.status === 204) return null;

        const text = await res.text();
        return text ? JSON.parse(text) : null;
    }

    return { request };
})();