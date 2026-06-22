const lineupId = window.location.pathname.split('/').pop();
let playerName = '', playerId = '';
let replyTo = null;
let startX = 0, startY = 0, currentMsg = null, swipedMsg = null;
let stompClient = null;
let lastMessageId = 0;
let pollInterval = null;

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"]/g, c => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;' })[c]);
}

function isUserAtBottom() {
    const container = document.getElementById('chatMessages');
    return container.scrollHeight - container.scrollTop - container.clientHeight < 60;
}

function connectWebSocket() {
    try {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;
        stompClient.connect({}, function() {
            stompClient.subscribe('/topic/chat/' + lineupId, function(message) {
                const msg = JSON.parse(message.body);
                if (msg.id > lastMessageId) { lastMessageId = msg.id; addMessageToChat(msg); }
            });
            if (pollInterval) { clearInterval(pollInterval); pollInterval = null; }
        }, function() { startPolling(); });
    } catch(e) { startPolling(); }
}

function startPolling() { if (!pollInterval) pollInterval = setInterval(loadNewMessages, 2000); }

async function loadNewMessages() {
    try {
        const msgs = await (await fetch('/api/chat/' + lineupId + '?after=' + lastMessageId, { credentials: 'same-origin' })).json();
        msgs.forEach(function(m) { if (m.id > lastMessageId) { lastMessageId = m.id; addMessageToChat(m); } });
    } catch(e) {}
}

function onTouchStart(e) {
    const msg = e.target.closest('.chat-message'); if (!msg) return;
    if (swipedMsg && swipedMsg !== msg) { swipedMsg.style.transform = ''; swipedMsg = null; }
    startX = e.touches[0].clientX; startY = e.touches[0].clientY; currentMsg = msg;
}

function onTouchMove(e) {
    if (!currentMsg) return;
    const dx = e.touches[0].clientX - startX, dy = e.touches[0].clientY - startY;
    if (Math.abs(dx) > Math.abs(dy) && dx < -5) {
        e.preventDefault(); currentMsg.style.transform = 'translateX(' + Math.max(dx, -80) + 'px)'; currentMsg.style.transition = 'none';
    }
}

function onTouchEnd(e) {
    if (!currentMsg) return;
    const dx = (e.changedTouches[0] ? e.changedTouches[0].clientX : startX) - startX;
    const dy = Math.abs((e.changedTouches[0] ? e.changedTouches[0].clientY : startY) - startY);
    currentMsg.style.transition = 'transform 0.2s ease';
    if (dx < -60 && Math.abs(dx) > dy) {
        currentMsg.style.transform = 'translateX(-70px)'; swipedMsg = currentMsg;
        const mid = currentMsg.dataset.messageId, snd = currentMsg.dataset.sender, cnt = currentMsg.dataset.content;
        if (mid && snd) setReply(mid, snd, cnt);
    } else { currentMsg.style.transform = ''; swipedMsg = null; }
    currentMsg = null; startX = 0; startY = 0;
}

document.addEventListener('click', function(e) {
    if (swipedMsg && !e.target.closest('.chat-message')) { swipedMsg.style.transform = ''; swipedMsg = null; }
});

function setReply(messageId, sender, content) {
    replyTo = { id: messageId, sender: sender, content: content };
    document.getElementById('replyBarSender').textContent = sender;
    document.getElementById('replyBarText').textContent = content;
    document.getElementById('replyBar').classList.remove('hidden');
    document.getElementById('chatInput').placeholder = 'Ответ...'; document.getElementById('chatInput').focus();
}

function cancelReply() {
    replyTo = null;
    document.getElementById('replyBar').classList.add('hidden');
    document.getElementById('chatInput').placeholder = 'Написать сообщение...';
    if (swipedMsg) { swipedMsg.style.transform = ''; swipedMsg = null; }
}

function renderMessage(m) {
    const t = m.createdAt ? new Date(m.createdAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '';
    const sender = escapeHtml(m.playerName || ''), content = escapeHtml(m.message || '');
    let replyHtml = '';
    if (m.replyToId) replyHtml = '<div class="reply-preview-bar"><div class="reply-sender">' + escapeHtml(m.replyToSenderName || '') + '</div><div class="reply-content">' + escapeHtml(m.replyToContent || '') + '</div></div>';
    return '<div class="chat-message" data-message-id="' + (m.id || '') + '" data-sender="' + sender.replace(/"/g, '&quot;') + '" data-content="' + content.replace(/"/g, '&quot;') + '">' + replyHtml + '<div class="flex items-center gap-2 mb-0.5"><span class="chat-name">' + sender + '</span><span class="chat-time">' + t + '</span></div><div class="chat-text">' + content + '</div></div>';
}

function bindSwipes(container) {
    container.querySelectorAll('.chat-message').forEach(function(el) {
        el.removeEventListener('touchstart', onTouchStart); el.removeEventListener('touchmove', onTouchMove); el.removeEventListener('touchend', onTouchEnd);
        el.addEventListener('touchstart', onTouchStart, { passive: false }); el.addEventListener('touchmove', onTouchMove, { passive: false }); el.addEventListener('touchend', onTouchEnd, { passive: false });
    });
}

function addMessageToChat(m) {
    const container = document.getElementById('chatMessages');
    const placeholder = container.querySelector('.text-center'); if (placeholder) placeholder.remove();
    const wasAtBottom = isUserAtBottom();
    container.insertAdjacentHTML('beforeend', renderMessage(m));
    if (wasAtBottom) container.scrollTop = container.scrollHeight;
    const newMsg = container.lastElementChild;
    if (newMsg) {
        newMsg.addEventListener('touchstart', onTouchStart, { passive: false }); newMsg.addEventListener('touchmove', onTouchMove, { passive: false }); newMsg.addEventListener('touchend', onTouchEnd, { passive: false });
    }
}

async function loadChatHistory() {
    try {
        const msgs = await (await fetch('/api/chat/' + lineupId, { credentials: 'same-origin' })).json();
        const container = document.getElementById('chatMessages');
        if (msgs.length > 0) {
            lastMessageId = msgs[msgs.length - 1].id || 0;
            container.innerHTML = msgs.map(function(m) { return renderMessage(m); }).join('');
            container.scrollTop = container.scrollHeight; bindSwipes(container);
        }
    } catch(e) {}
}

async function sendMessage() {
    const input = document.getElementById('chatInput'), msg = input.value.trim(); if (!msg) return;
    const btn = document.getElementById('sendBtn'); btn.disabled = true;
    try {
        const body = { playerId: playerId, playerName: playerName, message: msg };
        if (replyTo) body.replyToId = replyTo.id;
        const res = await fetch('/api/chat/' + lineupId, { method: 'POST', headers: { 'Content-Type': 'application/json' }, credentials: 'same-origin', body: JSON.stringify(body) });
        const saved = await res.json(); if (saved.id > lastMessageId) lastMessageId = saved.id;
        input.value = ''; addMessageToChat(saved); cancelReply();
    } catch(e) {} finally { btn.disabled = false; }
}

async function loadOnline() {
    try {
        const res = await fetch('/api/chat/' + lineupId + '/online', { credentials: 'same-origin' });
        if (res.ok) {
            const count = await res.json();
            const el = document.getElementById('onlineCount');
            if (count > 0) { el.textContent = '👁 ' + count; el.style.display = 'inline'; }
            else { el.style.display = 'none'; }
        }
    } catch(e) {}
}

async function loadData() {
    try {
        const me = await (await fetch('/api/auth/me', { credentials: 'same-origin' })).json().catch(function() { return {}; });
        playerName = me.name || 'Аноним';
        playerId = me.id || '00000000-0000-0000-0000-000000000000';

        const lineup = await (await fetch('/api/lineups/' + lineupId, { credentials: 'same-origin' })).json();

        document.getElementById('loading').classList.add('hidden');
        document.getElementById('content').classList.remove('hidden');
        document.getElementById('leagueTitle').textContent = lineup.league || 'Турнир';
        document.getElementById('tournamentInfo').textContent = (lineup.hall || '') + ' • ' + (lineup.time || '');
        document.getElementById('playersList').innerHTML = (lineup.players ? lineup.players.split(', ') : []).map(function(p) { return '<span class="player-tag">' + escapeHtml(p) + '</span>'; }).join('');

        if (lineup.streamUrl) {
            const placeholder = document.getElementById('videoPlaceholder');
            const frame = document.getElementById('streamFrame');
            placeholder.style.display = 'none';
            frame.src = lineup.streamUrl;
            frame.style.display = 'block';
        }

        await loadChatHistory();
        connectWebSocket();
        loadOnline();
        setInterval(loadOnline, 30000);
    } catch(e) { document.getElementById('loading').innerHTML = '<p class="text-red-400">Ошибка загрузки</p>'; }
}

window.sendMessage = sendMessage;
window.cancelReply = cancelReply;
document.getElementById('chatInput').addEventListener('keydown', function(e) { if (e.key === 'Enter') sendMessage(); });
loadData();