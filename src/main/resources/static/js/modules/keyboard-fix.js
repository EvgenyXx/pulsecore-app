// js/modules/keyboard-fix.js
export function initKeyboardFix() {
    if (!window.visualViewport) return;

    const vv = window.visualViewport;
    const bottomTab = document.getElementById('bottomTab');
    const appRoot = document.getElementById('appRoot');

    vv.addEventListener('resize', () => {
        // Если высота уменьшилась более чем на 150px — значит открылась клавиатура
        const isKeyboardOpen = window.innerHeight - vv.height > 150;

        if (isKeyboardOpen) {
            // 1. Жестко прячем панель
            if (bottomTab) bottomTab.style.display = 'none';

            // 2. Сжимаем контейнер ровно под высоту экрана над клавиатурой
            if (appRoot) {
                appRoot.style.height = `${vv.height}px`;
                appRoot.style.overflow = 'hidden';
            }

            // 3. Чуть прокручиваем, чтобы инпут был виден
            const activeEl = document.activeElement;
            if (activeEl && activeEl.tagName === 'INPUT') {
                setTimeout(() => {
                    activeEl.scrollIntoView({ block: 'center' });
                }, 50);
            }
        } else {
            // Возвращаем всё как было
            if (bottomTab) bottomTab.style.display = '';
            if (appRoot) {
                appRoot.style.height = '';
                appRoot.style.overflow = '';
            }
        }
    });
}