// js/modules/share.js

// Функция для красивого вывода сообщений (вместо alert)
function showToast(message, isError = false) {
    const oldToast = document.getElementById('customToast');
    if (oldToast) oldToast.remove();

    const toast = document.createElement('div');
    toast.id = 'customToast';
    // Добавляем классы из CSS
    toast.className = `custom-toast ${isError ? 'error' : ''}`;
    toast.textContent = message;

    document.body.appendChild(toast);

    // Запускаем анимацию появления
    requestAnimationFrame(() => {
        toast.classList.add('show');
    });

    // Плавно скрываем и удаляем
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

document.addEventListener('DOMContentLoaded', () => {
    const btn = document.getElementById('shareAppBtn');
    if (!btn) return;

    btn.addEventListener('click', async () => {
        const url = 'https://pulsecore-app.ru';

        // 1. Пытаемся вызвать нативное меню
        if (navigator.share) {
            try {
                await navigator.share({
                    title: 'PulseCore',
                    text: 'Смотри свой заработок и статистику турниров в настольный теннис!',
                    url: url
                });
            } catch (err) {
                // Если юзер сам закрыл меню — это не ошибка
                if (err.name !== 'AbortError') {
                    console.log('Ошибка шаринга:', err);
                    showToast('Не удалось открыть меню. Скопируйте ссылку: ' + url, true);
                }
            }
        } else {
            // 2. Если меню нет (ПК или старый браузер)
            try {
                await navigator.clipboard.writeText(url);
                showToast('Ссылка скопирована!\nВставьте её в любой мессенджер.');
            } catch (err) {
                // Если буфер запрещен (HTTP), показываем окно с текстом
                showToast('Ссылка на приложение:\n' + url, false);
            }
        }
    });
});