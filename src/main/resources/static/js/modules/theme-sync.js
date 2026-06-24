// js/modules/theme-sync.js
export function initThemeSync() {
    const saved = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', saved);

    fetch('/api/auth/me', { credentials: 'same-origin' })
        .then(r => r.json())
        .then(data => {
            if (data.theme && data.theme !== saved) {
                document.documentElement.setAttribute('data-theme', data.theme);
                localStorage.setItem('theme', data.theme);
            }
        })
        .catch(() => {});
}