window.toggleTheme = function() {
    const html = document.documentElement;
    const current = html.getAttribute('data-theme') || 'dark';
    const next = current === 'dark' ? 'ocean' : 'dark';
    html.setAttribute('data-theme', next);
    fetch('/api/auth/me/theme', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin',
        body: JSON.stringify({ theme: next })
    }).catch(() => {});
};

(function() {
    fetch('/api/auth/me', { credentials: 'same-origin' })
        .then(r => r.json())
        .then(data => {
            if (data.theme) {
                document.documentElement.setAttribute('data-theme', data.theme);
            }
        })
        .catch(() => {});
})();