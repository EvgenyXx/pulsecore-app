window.toggleTheme = function () {
    const html = document.documentElement;
    const current = html.getAttribute('data-theme') || 'dark';
    const next = current === 'dark' ? 'ocean' : 'dark';
    html.setAttribute('data-theme', next);
    fetch('/api/player/me/theme', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        credentials: 'same-origin',
        body: JSON.stringify({theme: next})
    }).catch(() => {
    });
};

(function () {
    window.Me.load().then(data => {
        if (data && data.theme) {
            document.documentElement.setAttribute('data-theme', data.theme);
        }
    }).catch(() => {
    });
})();