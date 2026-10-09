window.Http = (function () {
    const BASE = '/api/shop';

    async function request(path, options = {}) {
        const res = await fetch(BASE + path, {
            credentials: 'same-origin',
            ...options
        });

        if (!res.ok) {
            const data = await res.json().catch(() => ({}));

            const err = new Error(data.message || `HTTP ${res.status}`);
            err.status = res.status;
            err.problems = data.problems || null;
            err.type = data.type || null;

            throw err;
        }

        if (res.status === 204) return null;

        const text = await res.text();
        return text ? JSON.parse(text) : null;
    }

    return { request };
})();