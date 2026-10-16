// js/show-settings-page.js — глобальная функция showSettingsPage для разметки профиля
// Разметка использует onclick="showSettingsPage('account')" — функция должна быть в window.

window.showSettingsPage = function (page) {
    document.getElementById('settings-main').style.display = page === 'main' ? 'block' : 'none';
    ['account', 'subscription', 'appearance', 'security', 'share'].forEach(t => {
        const el = document.getElementById('settings-' + t);
        if (el) el.style.display = t === page ? 'block' : 'none';
    });
};