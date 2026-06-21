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
                if (msg.id > lastMessageId) {
                    lastMessageId = msg.id;
                    addMessageToChat(msg);
                }
            });
            // WebSocket подключился — останавливаем polling
            if (pollInterval) {
                clearInterval(pollInterval);
                pollInterval = null;
            }
        }, function() {
            // WebSocket отвалился — запускаем polling
            startPolling();
        });
    } catch(e) {
        startPolling();
    }
}

function startPolling() {
    if (pollInterval) return;
    pollInterval = setInterval(loadNewMessages, 2000);
}

async function loadNewMessages() {
    try {
        const msgs = await (await fetch(`/api/chat/${lineupId}?after=${lastMessageId}`, { credentials: 'same-origin' })).json();
        if (msgs.length > 0) {
            msgs.forEach(m => {
                if (m.id > lastMessageId) {
                    lastMessageId = m.id;
                    addMessageToChat(m);
                }
            });
        }
    } catch(e) {}
}

function onTouchStart(e) {
    const msg = e.target.closest('.chat-message');
    if (!msg) return;
    if (swipedMsg && swipedMsg !== msg) {
        swipedMsg.style.transform = '';
        swipedMsg = null;
    }
    startX = e.touches[0].clientX;
    startY = e.touches[0].clientY;
    currentMsg = msg;
}

function onTouchMove(e) {
    if (!currentMsg) return;
    const dx = e.touches[0].clientX - startX;
    const dy = e.touches[0].clientY - startY;
    if (Math.abs(dx) > Math.abs(dy) && dx < -5) {
        e.preventDefault();
        const offset = Math.max(dx, -80);
        currentMsg.style.transform = `translateX(${offset}px)`;
        currentMsg.style.transition = 'none';
    }
}

function onTouchEnd(e) {
    if (!currentMsg) return;
    const dx = (e.changedTouches[0]?.clientX || startX) - startX;
    const dy = Math.abs((e.changedTouches[0]?.clientY || startY) - startY);
    currentMsg.style.transition = 'transform 0.2s ease';
    if (dx < -60 && Math.abs(dx) > dy) {
        currentMsg.style.transform = 'translateX(-70px)';
        swipedMsg = currentMsg;
        const msgId = currentMsg.dataset.messageId;
        const sender = currentMsg.dataset.sender;
        const content = currentMsg.dataset.content;
        if (msgId && sender) setReply(msgId, sender, content);
    } else {
        currentMsg.style.transform = '';
        swipedMsg = null;
    }
    currentMsg = null;
    startX = 0;
    startY = 0;
}

document.addEventListener('click', (e) => {
    if (swipedMsg && !e.target.closest('.chat-message')) {
        swipedMsg.style.transform = '';
        swipedMsg = null;
    }
});

function setReply(messageId, sender, content) {
    replyTo = { id: messageId, sender, content };
    document.getElementById('replyBarSender').textContent = sender;
    document.getElementById('replyBarText').textContent = content;
    document.getElementById('replyBar').classList.remove('hidden');
    const input = document.getElementById('chatInput');
    input.placeholder = 'Ответ...';
    input.focus();
}

function cancelReply() {
    replyTo = null;
    document.getElementById('replyBar').classList.add('hidden');
    document.getElementById('chatInput').placeholder = 'Написать сообщение...';
    if (swipedMsg) {
        swipedMsg.style.transform = '';
        swipedMsg = null;
    }
}

function renderMessage(m) {
    const t = m.createdAt ? new Date(m.createdAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '';
    const sender = escapeHtml(m.playerName || '');
    const content = escapeHtml(m.message || '');
    let replyHtml = '';
    if (m.replyToId) {
        replyHtml = `
            <div class="reply-preview-bar">
                <div class="reply-sender">${escapeHtml(m.replyToSenderName || '')}</div>
                <div class="reply-content">${escapeHtml(m.replyToContent || '')}</div>
            </div>`;
    }
    return `
        <div class="chat-message" data-message-id="${m.id || ''}" data-sender="${sender.replace(/"/g, '&quot;')}" data-content="${content.replace(/"/g, '&quot;')}">
            ${replyHtml}
            <div class="flex items-center gap-2 mb-0.5">
                <span class="chat-name">${sender}</span>
                <span class="chat-time">${t}</span>
            </div>
            <div class="chat-text">${content}</div>
        </div>`;
}

function bindSwipes(container) {
    container.querySelectorAll('.chat-message').forEach(el => {
        el.removeEventListener('touchstart', onTouchStart);
        el.removeEventListener('touchmove', onTouchMove);
        el.removeEventListener('touchend', onTouchEnd);
        el.addEventListener('touchstart', onTouchStart, { passive: false });
        el.addEventListener('touchmove', onTouchMove, { passive: false });
        el.addEventListener('touchend', onTouchEnd, { passive: false });
    });
}

function addMessageToChat(m) {
    const container = document.getElementById('chatMessages');
    const placeholder = container.querySelector('.text-center');
    if (placeholder) placeholder.remove();
    const wasAtBottom = isUserAtBottom();
    const html = renderMessage(m);
    container.insertAdjacentHTML('beforeend', html);
    if (wasAtBottom) container.scrollTop = container.scrollHeight;
    const newMsg = container.lastElementChild;
    if (newMsg) {
        newMsg.addEventListener('touchstart', onTouchStart, { passive: false });
        newMsg.addEventListener('touchmove', onTouchMove, { passive: false });
        newMsg.addEventListener('touchend', onTouchEnd, { passive: false });
    }
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
    } catch (e) {}
}

async function sendMessage() {
    const input = document.getElementById('chatInput');
    const msg = input.value.trim();
    if (!msg) return;
    const btn = document.getElementById('sendBtn');
    btn.disabled = true;
    try {
        const body = { playerId, playerName, message: msg };
        if (replyTo) body.replyToId = replyTo.id;

        const res = await fetch(`/api/chat/${lineupId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify(body)
        });
        const saved = await res.json();
        if (saved.id > lastMessageId) lastMessageId = saved.id;

        input.value = '';
        addMessageToChat(saved);
        cancelReply();
    } catch (e) {
        console.error(e);
    } finally {
        btn.disabled = false;
    }
}

function fitVideoSize() {
    const frame = document.getElementById('streamFrame');
    const wrapper = document.getElementById('videoWrapper');
    const w = wrapper.offsetWidth;
    try {
        const doc = frame.contentDocument || frame.contentWindow.document;
        if (doc) {
            const videoEl = doc.querySelector('video, iframe[src*="youtube"], iframe[src*="vimeo"], iframe[src*="player"], embed, object');
            if (videoEl) {
                const rect = videoEl.getBoundingClientRect();
                if (rect.width > 100 && rect.height > 100) {
                    frame.style.width = rect.width + 'px';
                    frame.style.height = rect.height + 'px';
                    frame.style.margin = '0';
                    frame.style.padding = '0';
                    const scaleX = rect.width / doc.body.scrollWidth;
                    const scaleY = rect.height / doc.body.scrollHeight;
                    frame.style.transform = `scale(${scaleX}, ${scaleY})`;
                    frame.style.transformOrigin = 'top left';
                    frame.style.position = 'relative';
                    return;
                }
            }
        }
    } catch(e) {}
    frame.style.width = w + 'px';
    frame.style.height = (w * 9 / 16) + 'px';
    frame.style.transform = 'none';
}

function toggleFullscreen() {
    const wrapper = document.getElementById('videoWrapper');
    const frame = document.getElementById('streamFrame');
    const btn = document.getElementById('fullscreenBtn');
    if (wrapper.classList.contains('fullscreen-active')) {
        wrapper.classList.remove('fullscreen-active');
        wrapper.style.cssText = 'position:relative;display:flex;justify-content:center;align-items:center;min-height:200px;';
        frame.style.cssText = 'border:0;display:block;background:#000;';
        btn.innerHTML = `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 3 21 3 21 9"/><polyline points="9 21 3 21 3 15"/><line x1="21" y1="3" x2="14" y2="10"/><line x1="3" y1="21" x2="10" y2="14"/></svg> На весь экран`;
        fitVideoSize();
    } else {
        wrapper.classList.add('fullscreen-active');
        wrapper.style.cssText = 'position:fixed;top:0;left:0;width:100vw;height:100vh;z-index:9999;background:#000;border-radius:0;border:none;display:flex;justify-content:center;align-items:center;';
        frame.style.cssText = 'border:0;display:block;max-width:100%;max-height:100%;background:#000;';
        btn.innerHTML = `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="4 4 9 4 9 9"/><polyline points="20 20 15 20 15 15"/><line x1="9" y1="4" x2="4" y2="9"/><line x1="15" y1="20" x2="20" y2="15"/></svg> Свернуть`;
    }
}

async function loadOnline() {
    try {
        const res = await fetch(`/api/chat/${lineupId}/online`, { credentials: 'same-origin' });
        if (res.ok) {
            const count = await res.json();
            const el = document.getElementById('onlineCount');
            if (count > 0) {
                el.textContent = '👁 ' + count;
                el.style.display = 'inline';
            } else {
                el.style.display = 'none';
            }
        }
    } catch(e) {}
}

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

        document.getElementById('playersList').innerHTML = (lineup.players ? lineup.players.split(', ') : [])
            .map(p => `<span class="player-tag">${escapeHtml(p)}</span>`).join('');

        if (lineup.streamUrl) {
            document.getElementById('videoPlaceholder').style.display = 'none';
            const frame = document.getElementById('streamFrame');
            frame.src = lineup.streamUrl;
            frame.style.display = 'block';
            frame.onload = function() {
                fitVideoSize();
                setTimeout(fitVideoSize, 500);
                setTimeout(fitVideoSize, 1500);
                setTimeout(fitVideoSize, 3000);
            };
        }

        await loadChatHistory();
        connectWebSocket();

        loadOnline();
        setInterval(loadOnline, 30000);
    } catch (e) {
        document.getElementById('loading').innerHTML = '<p class="text-red-400">Ошибка загрузки</p>';
    }
}

window.sendMessage = sendMessage;
window.cancelReply = cancelReply;
window.toggleFullscreen = toggleFullscreen;

document.getElementById('chatInput').addEventListener('keydown', (e) => {
    if (e.key === 'Enter') sendMessage();
});

loadData();