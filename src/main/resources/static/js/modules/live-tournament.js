import { escapeHtml, SEND_ICON } from './chat-core.js';
import { renderMessage } from './chat-render.js';
import { showContextMenu } from './chat-context-menu.js';
import { connectChatWebSocket } from './chat-websocket.js';

// Экспортируем для глобального доступа
window.escapeHtml = escapeHtml;
window.SEND_ICON = SEND_ICON;

let lineupId = null;
let playerName = '', playerId = '';
let replyTo = null;
let editingMessageId = null;
let chatSocket = null;
let lastMessageId = 0;

let mentionList = [];
let mentionIndex = -1;
let mentionStart = -1;
let mentionJustSelected = false;

window.lastMessageId = 0;

function isUserAtBottom() {
    const container = document.getElementById('chatMessages');
    return container.scrollHeight - container.scrollTop - container.clientHeight < 60;
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
    document.getElementById('chatInput').value = content;
    document.getElementById('chatInput').placeholder = 'Исправьте сообщение...';
    document.getElementById('chatInput').focus();
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
    document.getElementById('chatInput').placeholder = 'Ответ...';
    document.getElementById('chatInput').value = '';
    document.getElementById('chatInput').focus();
    document.getElementById('sendBtn').innerHTML = SEND_ICON;
}

function cancelReply() {
    replyTo = null;
    editingMessageId = null;
    document.getElementById('replyBar').classList.add('hidden');
    document.getElementById('chatInput').placeholder = 'Написать сообщение...';
    document.getElementById('chatInput').value = '';
    document.getElementById('sendBtn').innerHTML = SEND_ICON;
}

function addMessageToChat(m) {
    const container = document.getElementById('chatMessages');
    const placeholder = container.querySelector('.text-center');
    if (placeholder) placeholder.remove();
    const wasAtBottom = isUserAtBottom();
    container.insertAdjacentHTML('beforeend', renderMessage(m));
    if (wasAtBottom) container.scrollTop = container.scrollHeight;
}

async function loadChatHistory() {
    try {
        const msgs = await (await fetch(`/api/tournament/${lineupId}`, { credentials: 'same-origin' })).json();
        const container = document.getElementById('chatMessages');
        if (msgs.length > 0) {
            lastMessageId = msgs[msgs.length - 1].id || 0;
            window.lastMessageId = lastMessageId;
            container.innerHTML = msgs.map(m => renderMessage(m)).join('');
            container.scrollTop = container.scrollHeight;
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
        cancelReply();
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

        // Загружаем турнир по ID напрямую с сервера
        const res = await fetch(`/api/lineups/${lineupId}`, { credentials: 'same-origin' });
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

        chatSocket = connectChatWebSocket(
            lineupId,
            function(msg) {
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

        setupMentions();
    } catch(e) {
        document.getElementById('tournamentLoading').innerHTML = '<p class="text-red-400">Ошибка загрузки</p>';
    }
}

function setupMentions() {
    const input = document.getElementById('chatInput');
    const dropdown = document.createElement('div');
    dropdown.id = 'mentionDropdown';
    dropdown.style.cssText = 'position:absolute;bottom:48px;left:0;right:0;background:#1c1c1e;border:1px solid rgba(255,255,255,0.08);border-radius:12px;max-height:170px;overflow-y:auto;display:none;z-index:30;box-shadow:0 -4px 20px rgba(0,0,0,0.5);backdrop-filter:blur(20px);';
    input.parentElement.style.position = 'relative';
    input.parentElement.appendChild(dropdown);

    input.addEventListener('input', function() {
        const val = input.value, cursorPos = input.selectionStart;
        const textBefore = val.substring(0, cursorPos);
        const atIndex = textBefore.lastIndexOf('@');
        if (atIndex === -1 || (atIndex > 0 && textBefore[atIndex - 1] !== ' ')) {
            dropdown.style.display = 'none';
            return;
        }
        const query = textBefore.substring(atIndex + 1).trim();
        if (query.length === 0) {
            dropdown.style.display = 'none';
            return;
        }
        fetch('/api/tournament/players/search?q=' + encodeURIComponent(query))
            .then(r => r.json())
            .then(players => {
                if (players.length > 0) {
                    dropdown.innerHTML = players.map((p, i) => `<div class="mention-item" data-idx="${i}" style="padding:10px 14px;cursor:pointer;color:#e4e4e7;font-size:0.85rem;">${escapeHtml(p.playerName)}</div>`).join('');
                    dropdown.style.display = 'block';
                    dropdown.querySelectorAll('.mention-item').forEach(el => {
                        el.addEventListener('click', function() {
                            const before = input.value.substring(0, atIndex);
                            const after = input.value.substring(cursorPos);
                            input.value = before + '@' + players[parseInt(this.dataset.idx)].playerName + ' ' + after;
                            dropdown.style.display = 'none';
                            input.focus();
                        });
                    });
                } else {
                    dropdown.style.display = 'none';
                }
            });
    });
}

window.loadTournamentData = async function(externalId) {
    lineupId = externalId;

    document.getElementById('tournamentLoading').classList.remove('hidden');
    document.getElementById('content').classList.add('hidden');

    if (chatSocket) {
        chatSocket.disconnect();
    }

    lastMessageId = 0;
    window.lastMessageId = 0;
    document.getElementById('chatMessages').innerHTML = '<div class="text-center text-zinc-600 text-xs py-4">Сообщений пока нет</div>';

    await loadData();
};

window.sendMessage = sendMessage;
window.cancelReply = cancelReply;
window.toggleFullscreen = toggleFullscreen;

document.getElementById('chatInput').addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && mentionList.length === 0) sendMessage();
});