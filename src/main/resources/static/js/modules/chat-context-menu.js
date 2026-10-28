export function showContextMenu(e, msgElement, playerName, callbacks) {
    e.preventDefault();
    e.stopPropagation();
    removeContextMenu();

    const messageId = msgElement.dataset.messageId;
    const sender = msgElement.dataset.sender;
    const content = msgElement.dataset.content;

    const menu = document.createElement('div');
    menu.className = 'context-menu';

    const isMyMessage = sender === playerName;

    if (isMyMessage) {
        menu.appendChild(createMenuItem('Удалить', () => {
            callbacks.delete(messageId);
            removeContextMenu();
        }, 'delete'));

        menu.appendChild(createMenuItem('Редактировать', () => {
            callbacks.edit(messageId, content);
            removeContextMenu();
        }));
    }

    menu.appendChild(createMenuItem('Ответить', () => {
        callbacks.reply(messageId, sender, content);
        removeContextMenu();
    }));

    document.body.appendChild(menu);

    const menuWidth = menu.offsetWidth || 140;
    const menuHeight = menu.offsetHeight || 100;

    let left = e.clientX - (menuWidth / 2);
    let top = e.clientY - menuHeight - 10;

    if (left < 10) left = 10;
    if (left + menuWidth > window.innerWidth - 10) left = window.innerWidth - menuWidth - 10;
    if (top < 10) top = e.clientY + 20;

    menu.style.left = left + 'px';
    menu.style.top = top + 'px';

    setTimeout(() => document.addEventListener('click', removeContextMenu, { once: true }), 10);
}

function createMenuItem(text, onClick, extraClass = '') {
    const item = document.createElement('div');
    item.className = `context-menu-item ${extraClass}`;
    item.textContent = text;
    item.onclick = (e) => {
        e.stopPropagation();
        onClick();
    };
    return item;
}

export function removeContextMenu() {
    document.querySelectorAll('.context-menu').forEach(m => m.remove());
}