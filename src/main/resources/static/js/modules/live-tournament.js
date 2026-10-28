import { escapeHtml, SEND_ICON } from './chat-core.js';
import { renderMessage } from './chat-render.js';
import { showContextMenu } from './chat-context-menu.js';
import { connectChatWebSocket } from './chat-websocket.js';

window.escapeHtml = escapeHtml;
window.SEND_ICON = SEND_ICON;

let lineupId = null;
let playerName = '', playerId = '';
let replyTo = null;
let editingMessageId = null;
let chatSocket = null;
let lastMessageId = 0;
let pressTimer = null;

window.lastMessageId = 0;

// Функция авто-расширения textarea
function autoResizeTextarea(textarea) {
    if (!textarea) return;

    // Сбрасываем высоту, чтобы правильно рассчитать scrollHeight
    textarea.style.height = 'auto';

    // Максимальная высота (примерно 5-6 строк текста)
    const maxHeight = 120;

    if (textarea.scrollHeight > maxHeight) {
        textarea.style.height = maxHeight + 'px';
        textarea.style.overflowY = 'auto'; // Включаем скролл
    } else {
        textarea.style.height = textarea.scrollHeight + 'px';
        textarea.style.overflowY = 'hidden'; // Скрываем скролл
    }
}

function scrollToBottom() {
    const container = document.getElementById('chatMessages');
    if (container) {
        setTimeout(() => {
            container.scrollTop = container.scrollHeight;
        }, 20);
    }
}

function isUserAtBottom() {
    const container = document.getElementById('chatMessages');
    return container.scrollHeight - container.scrollTop - container.clientHeight < 60;
}

function attachLongPressHandler(msgElement) {
    const startPress = (e) => {
        if (e.target.tagName === 'A' || e.target.tagName === 'BUTTON') return;
        e.preventDefault();

        pressTimer = setTimeout(() => {
            if (navigator.vibrate) navigator.vibrate(50);
            const touch = e.touches ? e.touches[0] : e;
            showContextMenu(touch, msgElement, playerName, {
                delete: deleteMessage,
                edit: startEdit,
                reply: setReply
            });
            if (window.getSelection) window.getSelection().removeAllRanges();
        }, 500);
    };

    const cancelPress = () => {
        if (pressTimer) {
            clearTimeout(pressTimer);
            pressTimer = null;
        }
    };

    msgElement.addEventListener('pointerdown', startPress);
    msgElement.addEventListener('pointerup', cancelPress);
    msgElement.addEventListener('pointerleave', cancelPress);
    msgElement.addEventListener('pointercancel', cancelPress);
    msgElement.addEventListener('pointermove', cancelPress);
}

async function deleteMessage(messageId) {
    try {
        const res = await fetch(`/api/tournament/message/${messageId}`, { method: 'DELETE', credentials: 'same-origin' });
        if (res.ok) {
            const msgEl = document.querySelector(`.chat-message[data-message-id="${messageId}"]`);
            if (msgEl) msgEl.remove();
        }
    } catch(e) {}
}

function startEdit(messageId, content) {
    editingMessageId = messageId;
    replyTo = null;
    document.getElementById('replyBarSender').textContent = 'Редактирование';
    document.getElementById('replyBarText').textContent = content;
    document.getElementById('replyBar').classList.remove('hidden');

    const input = document.getElementById('chatInput');
    input.value = content;
    input.placeholder = 'Исправьте сообщение...';
    input.focus();
    autoResizeTextarea(input); // Растягиваем под текст

    document.getElementById('sendBtn').innerHTML = SEND_ICON;
}

async function sendEditMessage() {
    const input = document.getElementById('chatInput'), newText = input.value.trim();
    if (!newText || !editingMessageId) return;
    const btn = document.getElementById('sendBtn'); btn.disabled = true;
    try {
        const res = await fetch(`/api/tournament/message/${editingMessageId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify({ message: newText })
        });
        if (res.ok) {
            const msgEl = document.querySelector(`.chat-message[data-message-id="${editingMessageId}"]`);
            if (msgEl) {
                msgEl.querySelector('.chat-text').textContent = newText;
                msgEl.dataset.content = newText;
            }
        }
    } catch(e) {} finally {
        btn.disabled = false;
        cancelReply();
    }
}

function setReply(messageId, sender, content) {
    editingMessageId = null;
    replyTo = { id: messageId, sender, content };
    document.getElementById('replyBarSender').textContent = sender;
    document.getElementById('replyBarText').textContent = content;
    document.getElementById('replyBar').classList.remove('hidden');

    const input = document.getElementById('chatInput');
    input.placeholder = 'Ответ...';
    input.value = '';
    input.focus();
    autoResizeTextarea(input);

    document.getElementById('sendBtn').innerHTML = SEND_ICON;
}

function cancelReply() {
    replyTo = null;
    editingMessageId = null;
    document.getElementById('replyBar').classList.add('hidden');

    const input = document.getElementById('chatInput');
    input.placeholder = 'Написать сообщение...';
    input.value = '';
    input.style.height = 'auto'; // Сброс высоты
    input.style.overflowY = 'hidden';

    document.getElementById('sendBtn').innerHTML = SEND_ICON;
}

function addMessageToChat(m) {
    const container = document.getElementById('chatMessages');
    const placeholder = container.querySelector('.text-center');
    if (placeholder) placeholder.remove();

    const wasAtBottom = isUserAtBottom();
    const isMine = m.playerName && playerName && m.playerName.toLowerCase() === playerName.toLowerCase();

    container.insertAdjacentHTML('beforeend', renderMessage(m, playerName));

    const newMsgElement = container.lastElementChild;
    if (newMsgElement) attachLongPressHandler(newMsgElement);

    if (wasAtBottom || isMine) scrollToBottom();
}

async function loadChatHistory() {
    const currentId = lineupId;
    try {
        const msgs = await (await fetch(`/api/tournament/${currentId}`, { credentials: 'same-origin' })).json();

        if (lineupId !== currentId) return;

        const container = document.getElementById('chatMessages');
        if (msgs.length > 0) {
            lastMessageId = msgs[msgs.length - 1].id || 0;
            window.lastMessageId = lastMessageId;
            container.innerHTML = msgs.map(m => renderMessage(m, playerName)).join('');

            container.querySelectorAll('.chat-message').forEach(attachLongPressHandler);
            scrollToBottom();
        }
    } catch(e) {}
}

async function sendMessage() {
    if (editingMessageId) { sendEditMessage(); return; }

    const input = document.getElementById('chatInput'), msg = input.value.trim();
    if (!msg) return;
    const btn = document.getElementById('sendBtn'); btn.disabled = true;
    try {
        const body = { playerId, playerName, message: msg };
        if (replyTo) body.replyToId = replyTo.id;

        if (chatSocket) {
            chatSocket.sendMessage(body);
        } else {
            const res = await fetch(`/api/tournament/${lineupId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify(body)
            });
            const saved = await res.json();
            if (saved.id > lastMessageId) { lastMessageId = saved.id; window.lastMessageId = saved.id; }
            addMessageToChat(saved);
        }

        input.value = '';
        input.style.height = 'auto'; // СБРОС ВЫСОТЫ
        input.style.overflowY = 'hidden';
        cancelReply();
        scrollToBottom();
    } catch(e) {} finally { btn.disabled = false; }
}

function toggleFullscreen() {
    const wrapper = document.getElementById('videoWrapper');
    if (!document.fullscreenElement) {
        wrapper.requestFullscreen().catch(() => {});
    } else {
        document.exitFullscreen();
    }
}

async function loadData() {
    try {
        const me = await (await fetch('/api/player/me', { credentials: 'same-origin' })).json().catch(() => ({}));
        playerName = me.name || 'Аноним';
        playerId = me.id || '00000000-0000-0000-0000-000000000000';

        const res = await fetch(`/api/tournament/lineups/${lineupId}`, { credentials: 'same-origin' });
        if (!res.ok) throw new Error('Турнир не найден');
        const lineup = await res.json();

        document.getElementById('tournamentLoading').classList.add('hidden');
        document.getElementById('content').classList.remove('hidden');
        document.getElementById('leagueTitle').textContent = lineup.league || 'Турнир';
        document.getElementById('tournamentInfo').textContent = `${lineup.hall || ''} • ${lineup.time || ''}`;
        document.getElementById('playersList').innerHTML = '';

        const streamUrl = lineup.streamUrl || lineup.stream_url;
        if (streamUrl) {
            document.getElementById('videoPlaceholder').style.display = 'none';
            const frame = document.getElementById('streamFrame');
            frame.src = streamUrl;
            frame.style.display = 'block';
        } else {
            document.getElementById('videoPlaceholder').style.display = 'block';
            document.getElementById('videoPlaceholder').innerHTML = `<div style="display:flex;align-items:center;justify-content:center;gap:10px;color:#a1a1aa;font-size:0.95rem;padding:20px;">Трансляция недоступна</div>`;
        }

        await loadChatHistory();

        if (chatSocket) chatSocket.disconnect();
        const currentId = lineupId;

        chatSocket = connectChatWebSocket(
            currentId,
            function(msg) {
                if (lineupId !== currentId) return;

                if (msg.type === 'DELETE') {
                    const el = document.querySelector(`.chat-message[data-message-id="${msg.messageId}"]`);
                    if (el) el.remove();
                    return;
                }
                if (msg.type === 'EDIT') {
                    const el = document.querySelector(`.chat-message[data-message-id="${msg.messageId}"]`);
                    if (el) {
                        el.querySelector('.chat-text').textContent = msg.message;
                        el.dataset.content = msg.message;
                    }
                    return;
                }
                if (msg.id > lastMessageId) {
                    lastMessageId = msg.id;
                    window.lastMessageId = msg.id;
                    addMessageToChat(msg);
                }
            },
            function(count) {
                if (lineupId !== currentId) return;
                const el = document.getElementById('onlineCount');
                if (count > 0) {
                    el.textContent = count + ' онлайн';
                    el.style.display = 'inline';
                } else {
                    el.style.display = 'none';
                }
            },
            function() {}
        );

        window.showContextMenu = function(e, msgElement) {
            showContextMenu(e, msgElement, playerName, {
                delete: deleteMessage,
                edit: startEdit,
                reply: setReply
            });
        };

        // Инициализация поля ввода
        const chatInput = document.getElementById('chatInput');
        if (chatInput) {
            chatInput.addEventListener('input', () => autoResizeTextarea(chatInput));
            chatInput.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    sendMessage();
                }
            });
        }

        setupMentions();
    } catch(e) {
        document.getElementById('tournamentLoading').innerHTML = '<p class="text-red-400">Ошибка загрузки</p>';
    }
}

function setupMentions() {
    const input = document.getElementById('chatInput');
    let dropdown = document.getElementById('mentionDropdown');

    if (!dropdown) {
        dropdown = document.createElement('div');
        dropdown.id = 'mentionDropdown';
        input.parentElement.style.position = 'relative';
        input.parentElement.appendChild(dropdown);
    }

    input.addEventListener('input', function() {
        const val = input.value, cursorPos = input.selectionStart;
        const textBefore = val.substring(0, cursorPos);
        const atIndex = textBefore.lastIndexOf('@');

        if (atIndex === -1 || textBefore.substring(atIndex + 1).includes(' ')) {
            dropdown.style.display = 'none';
            return;
        }

        const query = textBefore.substring(atIndex + 1).trim();
        if (query.length === 0) {
            dropdown.style.display = 'none';
            return;
        }

        fetch('/api/tournament/players/search?q=' + encodeURIComponent(query), { credentials: 'same-origin' })
            .then(r => {
                if (!r.ok) throw new Error('Network error');
                return r.json();
            })
            .then(players => {
                if (players && players.length > 0) {
                    dropdown.innerHTML = players.map((p, i) => `
                        <div class="mention-item" data-idx="${i}" style="padding:10px 16px;cursor:pointer;color:#ffffff;font-size:14px;display:flex;align-items:center;gap:8px;">
                            <div style="width:24px;height:24px;border-radius:50%;background:rgba(99,102,241,0.2);display:flex;align-items:center;justify-content:center;font-size:10px;color:#818cf8;font-weight:700;">
                                ${p.name.charAt(0).toUpperCase()}
                            </div>
                            ${escapeHtml(p.name)}
                        </div>
                    `).join('');
                    dropdown.style.display = 'block';

                    dropdown.querySelectorAll('.mention-item').forEach(el => {
                        el.addEventListener('click', function() {
                            const before = input.value.substring(0, atIndex);
                            const after = input.value.substring(cursorPos);
                            input.value = before + '@' + players[parseInt(this.dataset.idx)].name + ' ' + after;
                            dropdown.style.display = 'none';
                            input.focus();
                            autoResizeTextarea(input);
                        });
                    });
                } else {
                    dropdown.style.display = 'none';
                }
            })
            .catch(() => {
                dropdown.style.display = 'none';
            });
    });
}

window.loadTournamentData = async function(externalId) {
    if (chatSocket) {
        chatSocket.disconnect();
        chatSocket = null;
    }

    lineupId = externalId;

    document.getElementById('tournamentLoading').classList.remove('hidden');
    document.getElementById('content').classList.add('hidden');

    lastMessageId = 0;
    window.lastMessageId = 0;
    document.getElementById('chatMessages').innerHTML = '<div class="text-center text-zinc-600 text-xs py-4">Сообщений пока нет</div>';

    await loadData();
};

window.sendMessage = sendMessage;
window.cancelReply = cancelReply;
window.toggleFullscreen = toggleFullscreen;

export function init() {
    const chatInput = document.getElementById('chatInput');
    if (chatInput) {
        chatInput.addEventListener('input', () => autoResizeTextarea(chatInput));
        chatInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }
}