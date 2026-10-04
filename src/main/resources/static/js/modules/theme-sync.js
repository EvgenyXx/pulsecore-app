export function initThemeSync() {
    const saved = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', saved);

    window.Me.load().then(me => {
        if (me && me.theme && me.theme !== saved) {
            document.documentElement.setAttribute('data-theme', me.theme);
            localStorage.setItem('theme', me.theme);
        }
    });
}