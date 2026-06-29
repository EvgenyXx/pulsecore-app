const lineupId = window.location.pathname.split('/').pop();
let playerName = '', playerId = '';
let replyTo = null;
let editingMessageId = null;
let startX = 0, startY = 0, currentMsg = null, swipedMsg = null;
let stompClient = null;
let lastMessageId = 0;
let pollInterval = null;

let mentionList = [];
let mentionIndex = -1;
let mentionStart = -1;
let mentionJustSelected = false;

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"]/g, c => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;' })[c]);
}

function isUserAtBottom() {
    const container = document.getElementById('chatMessages');
    return container.scrollHeight - container.scrollTop - container.clientHeight < 60;
}

// ==================== CONTEXT MENU ====================

function showContextMenu(e, msgElement) {
    e.preventDefault();
    e.stopPropagation();
    removeContextMenu();

    const messageId = msgElement.dataset.messageId;
    const sender = msgElement.dataset.sender;
    const content = msgElement.dataset.content;

    const menu = document.createElement('div');
    menu.className = 'context-menu';
    menu.style.cssText = `
        position: fixed; z-index: 100; background: #27272a; border: 1px solid rgba(255,255,255,0.1);
        border-radius: 12px; padding: 4px; min-width: 140px; box-shadow: 0 8px 24px rgba(0,0,0,0.5);
        animation: fadeIn 0.15s ease;
    `;

    const isMyMessage = sender === playerName;

    if (isMyMessage) {
        menu.appendChild(createMenuItem('🗑 Удалить', () => deleteMessage(messageId, menu)));
        menu.appendChild(createMenuItem('✏️ Редактировать', () => { startEdit(messageId, content); removeContextMenu(); }));
    }
    menu.appendChild(createMenuItem('↩ Ответить', () => { setReply(messageId, sender, content); removeContextMenu(); }));

    document.body.appendChild(menu);

    const rect = msgElement.getBoundingClientRect();
    menu.style.top = Math.min(rect.top, window.innerHeight - menu.offsetHeight - 10) + 'px';
    menu.style.left = Math.min(rect.right - menu.offsetWidth, window.innerWidth - 10) + 'px';

    setTimeout(() => document.addEventListener('click', removeContextMenu, { once: true }), 0);
}

function createMenuItem(text, onClick) {
    const item = document.createElement('div');
    item.textContent = text;
    item.style.cssText = 'padding:8px 12px; border-radius:8px; cursor:pointer; font-size:0.85rem; color:#e4e4e7; transition:background 0.15s;';
    item.onmouseenter = () => item.style.background = 'rgba(255,255,255,0.08)';
    item.onmouseleave = () => item.style.background = 'transparent';
    item.onclick = (e) => { e.stopPropagation(); onClick(); };
    return item;
}

function removeContextMenu() {
    document.querySelectorAll('.context-menu').forEach(m => m.remove());
}

async function deleteMessage(messageId, menu) {
    removeContextMenu();
    try {
        const res = await fetch(`/api/chat/message/${messageId}`, { method: 'DELETE', credentials: 'same-origin' });
        if (res.ok) {
            const msgEl = document.querySelector(`.chat-message[data-message-id="${messageId}"]`);
            if (msgEl) msgEl.remove();
        }
    } catch(e) {}
}

// ==================== EDIT ====================

function startEdit(messageId, content) {
    editingMessageId = messageId;
    replyTo = null;
    document.getElementById('replyBarSender').textContent = 'Редактирование';
    document.getElementById('replyBarText').textContent = content;
    document.getElementById('replyBar').classList.remove('hidden');
    document.getElementById('chatInput').value = content;
    document.getElementById('chatInput').placeholder = 'Исправьте сообщение...';
    document.getElementById('chatInput').focus();
    document.getElementById('sendBtn').textContent = '✓';
}

async function sendEditMessage() {
    const input = document.getElementById('chatInput'), newText = input.value.trim();
    if (!newText || !editingMessageId) return;
    const btn = document.getElementById('sendBtn'); btn.disabled = true;
    try {
        const res = await fetch(`/api/chat/message/${editingMessageId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify({ message: newText })
        });
        if (res.ok) {
            const msgEl = document.querySelector(`.chat-message[data-message-id="${editingMessageId}"]`);
            if (msgEl) {
                msgEl.querySelector('.chat-text').innerHTML = highlightMentions(escapeHtml(newText));
                msgEl.dataset.content = newText;
                const nameEl = msgEl.querySelector('.chat-name');
                if (nameEl && !nameEl.textContent.includes('(изм.)')) {
                    nameEl.innerHTML += ' <span style="color:#71717a;font-size:0.65rem;">(изм.)</span>';
                }
            }
        }
    } catch(e) {} finally {
        btn.disabled = false;
        cancelReply();
    }
}

// ==================== WEBSOCKET ====================

function connectWebSocket() {
    try {
        const socket = new SockJS('/ws');
        stompClient = new StompJs.Client({
            webSocketFactory: () => socket,
            debug: function() {},
            onConnect: function() {
                stompClient.subscribe('/topic/chat/' + lineupId, function(message) {
                    const msg = JSON.parse(message.body);
                    if (msg.type === 'DELETE') {
                        const el = document.querySelector(`.chat-message[data-message-id="${msg.messageId}"]`);
                        if (el) el.remove();
                        return;
                    }
                    if (msg.type === 'EDIT') {
                        const el = document.querySelector(`.chat-message[data-message-id="${msg.messageId}"]`);
                        if (el) {
                            el.querySelector('.chat-text').innerHTML = highlightMentions(escapeHtml(msg.message));
                            el.dataset.content = msg.message;
                            const nameEl = el.querySelector('.chat-name');
                            if (nameEl && !nameEl.textContent.includes('(изм.)')) {
                                nameEl.innerHTML += ' <span style="color:#71717a;font-size:0.65rem;">(изм.)</span>';
                            }
                        }
                        return;
                    }
                    if (msg.id > lastMessageId) { lastMessageId = msg.id; addMessageToChat(msg); }
                });
                stompClient.subscribe('/topic/chat/' + lineupId + '/online', function(message) {
                    const count = JSON.parse(message.body);
                    const el = document.getElementById('onlineCount');
                    if (count > 0) {
                        el.textContent = '👁 ' + count;
                        el.style.display = 'inline';
                    } else {
                        el.style.display = 'none';
                    }
                });
                if (pollInterval) { clearInterval(pollInterval); pollInterval = null; }
            },
            onDisconnect: function() { startPolling(); },
            onStompError: function() { startPolling(); }
        });
        stompClient.activate();
    } catch(e) { startPolling(); }
}

function startPolling() { if (!pollInterval) pollInterval = setInterval(loadNewMessages, 2000); }

async function loadNewMessages() {
    try {
        const msgs = await (await fetch(`/api/chat/${lineupId}?after=${lastMessageId}`, { credentials: 'same-origin' })).json();
        msgs.forEach(m => { if (m.id > lastMessageId) { lastMessageId = m.id; addMessageToChat(m); } });
    } catch(e) {}
}

// ==================== SWIPE ====================

function onTouchStart(e) {
    const msg = e.target.closest('.chat-message'); if (!msg) return;
    if (swipedMsg && swipedMsg !== msg) { swipedMsg.style.transform = ''; swipedMsg = null; }
    startX = e.touches[0].clientX; startY = e.touches[0].clientY; currentMsg = msg;
}

function onTouchMove(e) {
    if (!currentMsg) return;
    const dx = e.touches[0].clientX - startX, dy = e.touches[0].clientY - startY;
    if (Math.abs(dx) > Math.abs(dy) && dx < -5) {
        e.preventDefault(); currentMsg.style.transform = `translateX(${Math.max(dx, -80)}px)`; currentMsg.style.transition = 'none';
    }
}

function onTouchEnd(e) {
    if (!currentMsg) return;
    const dx = (e.changedTouches[0]?.clientX || startX) - startX;
    const dy = Math.abs((e.changedTouches[0]?.clientY || startY) - startY);
    currentMsg.style.transition = 'transform 0.2s ease';
    if (dx < -60 && Math.abs(dx) > dy) {
        currentMsg.style.transform = 'translateX(-70px)'; swipedMsg = currentMsg;
        const msgId = currentMsg.dataset.messageId, sender = currentMsg.dataset.sender, content = currentMsg.dataset.content;
        if (msgId && sender) setReply(msgId, sender, content);
    } else { currentMsg.style.transform = ''; swipedMsg = null; }
    currentMsg = null; startX = 0; startY = 0;
}

document.addEventListener('click', (e) => {
    if (swipedMsg && !e.target.closest('.chat-message')) { swipedMsg.style.transform = ''; swipedMsg = null; }
    if (!e.target.closest('#mentionDropdown') && !e.target.closest('#chatInput')) {
        const dd = document.getElementById('mentionDropdown');
        if (dd) dd.style.display = 'none';
    }
});

function setReply(messageId, sender, content) {
    editingMessageId = null;
    replyTo = { id: messageId, sender, content };
    document.getElementById('replyBarSender').textContent = sender;
    document.getElementById('replyBarText').textContent = content;
    document.getElementById('replyBar').classList.remove('hidden');
    document.getElementById('chatInput').placeholder = 'Ответ...';
    document.getElementById('chatInput').value = '';
    document.getElementById('chatInput').focus();
    document.getElementById('sendBtn').textContent = '➤';
}

function cancelReply() {
    replyTo = null;
    editingMessageId = null;
    document.getElementById('replyBar').classList.add('hidden');
    document.getElementById('chatInput').placeholder = 'Написать сообщение...';
    document.getElementById('chatInput').value = '';
    document.getElementById('sendBtn').textContent = '➤';
    if (swipedMsg) { swipedMsg.style.transform = ''; swipedMsg = null; }
}

// ==================== RENDER ====================

function renderMessage(m) {
    const t = m.createdAt ? new Date(m.createdAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '';
    const sender = escapeHtml(m.playerName || ''), content = highlightMentions(escapeHtml(m.message || ''));
    const editedBadge = m.edited ? ' <span style="color:#71717a;font-size:0.65rem;">(изм.)</span>' : '';
    let replyHtml = '';
    if (m.replyToId) replyHtml = `<div class="reply-preview-bar"><div class="reply-sender">${escapeHtml(m.replyToSenderName || '')}</div><div class="reply-content">${escapeHtml(m.replyToContent || '')}</div></div>`;
    return `<div class="chat-message" data-message-id="${m.id || ''}" data-sender="${sender.replace(/"/g, '&quot;')}" data-content="${escapeHtml(m.message || '').replace(/"/g, '&quot;')}">
        <div class="flex items-center justify-between mb-0.5">
            <div class="flex items-center gap-2">
                <span class="chat-name">${sender}${editedBadge}</span>
                <span class="chat-time">${t}</span>
            </div>
            <span class="chat-dots" onclick="event.stopPropagation(); showContextMenu(event, this.closest('.chat-message'))">⋮</span>
        </div>
        ${replyHtml}
        <div class="chat-text">${content}</div>
    </div>`;
}

function highlightMentions(text) {
    return text.replace(/@([\p{L}]+\s+[\p{L}]+)/gu, '<span style="color:#818cf8;font-weight:600;">@$1</span>');
}

function bindEvents(el) {
    el.addEventListener('contextmenu', (e) => showContextMenu(e, el));
    el.addEventListener('touchstart', onTouchStart, { passive: false });
    el.addEventListener('touchmove', onTouchMove, { passive: false });
    el.addEventListener('touchend', onTouchEnd, { passive: false });
}

function bindSwipes(container) {
    container.querySelectorAll('.chat-message').forEach(el => {
        el.removeEventListener('touchstart', onTouchStart);
        el.removeEventListener('touchmove', onTouchMove);
        el.removeEventListener('touchend', onTouchEnd);
        bindEvents(el);
    });
}

function addMessageToChat(m) {
    const container = document.getElementById('chatMessages');
    const placeholder = container.querySelector('.text-center'); if (placeholder) placeholder.remove();
    const wasAtBottom = isUserAtBottom();
    container.insertAdjacentHTML('beforeend', renderMessage(m));
    if (wasAtBottom) container.scrollTop = container.scrollHeight;
    const newMsg = container.lastElementChild;
    if (newMsg) bindEvents(newMsg);
}

async function loadChatHistory() {
    try {
        const msgs = await (await fetch(`/api/chat/${lineupId}`, { credentials: 'same-origin' })).json();
        const container = document.getElementById('chatMessages');
        if (msgs.length > 0) {
            lastMessageId = msgs[msgs.length - 1].id || 0;
            container.innerHTML = msgs.map(m => renderMessage(m)).join('');
            container.scrollTop = container.scrollHeight;
            bindSwipes(container);
        }
    } catch(e) {}
}

async function sendMessage() {
    if (editingMessageId) { sendEditMessage(); return; }

    const input = document.getElementById('chatInput'), msg = input.value.trim(); if (!msg) return;
    const btn = document.getElementById('sendBtn'); btn.disabled = true;
    try {
        const body = { playerId, playerName, message: msg };
        if (replyTo) body.replyToId = replyTo.id;

        if (stompClient && stompClient.active) {
            stompClient.publish({ destination: '/app/chat/' + lineupId, body: JSON.stringify(body) });
        } else {
            const res = await fetch(`/api/chat/${lineupId}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, credentials: 'same-origin', body: JSON.stringify(body) });
            const saved = await res.json(); if (saved.id > lastMessageId) lastMessageId = saved.id;
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

// ==================== MENTIONS ====================

function buildMentionDropdown() {
    if (document.getElementById('mentionDropdown')) return;
    const input = document.getElementById('chatInput');
    const container = document.createElement('div');
    container.id = 'mentionDropdown';
    container.style.cssText = 'position:absolute;bottom:48px;left:0;right:0;background:#18181b;border:1px solid rgba(255,255,255,0.08);border-radius:10px;max-height:170px;overflow-y:auto;display:none;z-index:30;box-shadow:0 -4px 20px rgba(0,0,0,0.5);';
    input.parentElement.style.position = 'relative';
    input.parentElement.appendChild(container);
}

function setupMentions() {
    buildMentionDropdown();
    const input = document.getElementById('chatInput');

    const lineupPlayers = [];
    const playersEl = document.getElementById('playersList');
    if (playersEl) {
        playersEl.querySelectorAll('.player-tag').forEach(el => lineupPlayers.push({ id: '', name: el.textContent.trim() }));
    }

    function hasCompleteMention(text) {
        return /@([\p{L}]+\s+[\p{L}]+)/gu.test(text);
    }

    function fetchAndShow(query, atIndex) {
        fetch('/api/chat/players/search?q=' + encodeURIComponent(query))
            .then(r => r.json())
            .then(players => {
                if (players.length > 0) showMentions(players, atIndex);
                else {
                    const local = lineupPlayers.filter(p => p.name.toLowerCase().includes(query.toLowerCase()));
                    showMentions(local.length > 0 ? local : [], atIndex);
                }
            })
            .catch(() => {
                const local = lineupPlayers.filter(p => p.name.toLowerCase().includes(query.toLowerCase()));
                showMentions(local.length > 0 ? local : [], atIndex);
            });
    }

    input.addEventListener('input', function() {
        if (mentionJustSelected) { mentionJustSelected = false; return; }
        const val = input.value, cursorPos = input.selectionStart;
        const textBefore = val.substring(0, cursorPos);
        const atIndex = textBefore.lastIndexOf('@');
        if (atIndex === -1 || (atIndex > 0 && textBefore[atIndex - 1] !== ' ') || hasCompleteMention(textBefore.substring(atIndex))) {
            hideMentions(); return;
        }
        fetchAndShow(textBefore.substring(atIndex + 1).trim(), atIndex);
    });

    input.addEventListener('keydown', function(e) {
        if (mentionList.length === 0) return;
        if (e.key === 'ArrowDown') { e.preventDefault(); mentionIndex = Math.min(mentionIndex + 1, mentionList.length - 1); highlightMentionItem(); }
        else if (e.key === 'ArrowUp') { e.preventDefault(); mentionIndex = Math.max(mentionIndex - 1, 0); highlightMentionItem(); }
        else if (e.key === 'Enter' && mentionIndex >= 0) { e.preventDefault(); selectMention(mentionIndex); }
        else if (e.key === 'Escape') { hideMentions(); }
        else if (e.key === 'Tab' && mentionList.length > 0) { e.preventDefault(); selectMention(Math.max(0, mentionIndex)); }
    });
}

function showMentions(players, atIndex) {
    const container = document.getElementById('mentionDropdown');
    if (!container) return;
    mentionList = players; mentionIndex = -1; mentionStart = atIndex;
    if (players.length > 0) {
        container.innerHTML = players.map((p, i) => `<div class="mention-item" data-idx="${i}" style="padding:9px 14px;cursor:pointer;color:#e4e4e7;font-size:0.85rem;border-bottom:1px solid rgba(255,255,255,0.04);transition:background 0.15s;">${escapeHtml(p.name)}</div>`).join('');
        container.style.display = 'block';
        container.scrollTop = 0;
        container.querySelectorAll('.mention-item').forEach(el => {
            el.addEventListener('click', function() { selectMention(parseInt(this.dataset.idx)); });
            el.addEventListener('mouseenter', function() { mentionIndex = parseInt(this.dataset.idx); highlightMentionItem(); });
        });
    } else {
        container.innerHTML = '<div style="padding:9px 14px;color:#71717a;font-size:0.85rem;">Ничего не найдено</div>';
        container.style.display = 'block';
    }
}

function hideMentions() {
    const container = document.getElementById('mentionDropdown');
    if (container) container.style.display = 'none';
    mentionList = []; mentionIndex = -1; mentionStart = -1;
}

function highlightMentionItem() {
    const container = document.getElementById('mentionDropdown');
    if (!container) return;
    container.querySelectorAll('.mention-item').forEach((el, i) => {
        el.style.background = i === mentionIndex ? 'rgba(99,102,241,0.25)' : 'transparent';
        if (i === mentionIndex) el.scrollIntoView({ block: 'nearest' });
    });
}

function selectMention(idx) {
    if (idx < 0 || idx >= mentionList.length) return;
    const input = document.getElementById('chatInput');
    const before = input.value.substring(0, mentionStart);
    const after = input.value.substring(input.selectionStart);
    input.value = before + '@' + mentionList[idx].name + ' ' + after;
    mentionJustSelected = true;
    hideMentions();
    input.focus();
}

// ==================== INIT ====================

async function loadData() {
    try {
        const me = await (await fetch('/api/auth/me', { credentials: 'same-origin' })).json().catch(() => ({}));
        playerName = me.name || 'Аноним';
        playerId = me.id || '00000000-0000-0000-0000-000000000000';

        const lineup = await (await fetch(`/api/lineups/${lineupId}`, { credentials: 'same-origin' })).json();

        document.getElementById('loading').classList.add('hidden');
        document.getElementById('content').classList.remove('hidden');
        document.getElementById('leagueTitle').textContent = lineup.league || 'Турнир';
        document.getElementById('tournamentInfo').textContent = `${lineup.hall || ''} • ${lineup.time || ''}`;
        document.getElementById('playersList').innerHTML = (lineup.players ? lineup.players.split(', ') : []).map(p => `<span class="player-tag">${escapeHtml(p)}</span>`).join('');

        const streamUrl = lineup.streamUrl || lineup.stream_url;
        if (streamUrl) {
            document.getElementById('videoPlaceholder').style.display = 'none';
            const frame = document.getElementById('streamFrame');
            frame.src = streamUrl;
            frame.style.display = 'block';
        } else {
            document.getElementById('videoPlaceholder').innerHTML = `<div style="display:flex;align-items:center;justify-content:center;gap:10px;color:#a1a1aa;font-size:0.95rem;padding:20px;">📡 Трансляция недоступна</div>`;
        }

        await loadChatHistory();
        connectWebSocket();
        setupMentions();
    } catch(e) { document.getElementById('loading').innerHTML = '<p class="text-red-400">Ошибка загрузки</p>'; }
}

window.sendMessage = sendMessage;
window.cancelReply = cancelReply;
window.toggleFullscreen = toggleFullscreen;
document.getElementById('chatInput').addEventListener('keydown', (e) => { if (e.key === 'Enter' && mentionList.length === 0) sendMessage(); });
loadData();