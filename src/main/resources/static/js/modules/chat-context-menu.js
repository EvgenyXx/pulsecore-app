export function showContextMenu(e, msgElement, playerName, callbacks) {
    e.preventDefault();
    e.stopPropagation();
    removeContextMenu();

    const messageId = msgElement.dataset.messageId;
    const sender = msgElement.dataset.sender;
    const content = msgElement.dataset.content;

    const menu = document.createElement('div');
    menu.className = 'context-menu';
    menu.style.cssText = `
        position: fixed; z-index: 100; background: #1c1c1e; border: 1px solid rgba(255,255,255,0.08);
        border-radius: 14px; padding: 6px; min-width: 150px; box-shadow: 0 12px 32px rgba(0,0,0,0.5);
        animation: fadeIn 0.2s cubic-bezier(0.4, 0, 0.2, 1); backdrop-filter: blur(20px);
    `;

    const isMyMessage = sender === playerName;

    if (isMyMessage) {
        menu.appendChild(createMenuItem('Удалить', () => { callbacks.delete(messageId); removeContextMenu(); }, '#ef4444'));
        menu.appendChild(createMenuItem('Редактировать', () => { callbacks.edit(messageId, content); removeContextMenu(); }));
    }
    menu.appendChild(createMenuItem('Ответить', () => { callbacks.reply(messageId, sender, content); removeContextMenu(); }));

    document.body.appendChild(menu);

    const rect = msgElement.getBoundingClientRect();
    menu.style.top = Math.min(rect.top, window.innerHeight - menu.offsetHeight - 10) + 'px';
    menu.style.left = Math.min(rect.right - menu.offsetWidth, window.innerWidth - 10) + 'px';

    setTimeout(() => document.addEventListener('click', removeContextMenu, { once: true }), 0);
}

function createMenuItem(text, onClick, color = '#e4e4e7') {
    const item = document.createElement('div');
    item.textContent = text;
    item.style.cssText = `padding:10px 14px; border-radius:10px; cursor:pointer; font-size:0.85rem; color:${color}; transition:background 0.2s;`;
    item.onmouseenter = () => item.style.background = 'rgba(255,255,255,0.08)';
    item.onmouseleave = () => item.style.background = 'transparent';
    item.onclick = (e) => { e.stopPropagation(); onClick(); };
    return item;
}

export function removeContextMenu() {
    document.querySelectorAll('.context-menu').forEach(m => m.remove());
}