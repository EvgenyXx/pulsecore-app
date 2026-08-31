export function connectChatWebSocket(lineupId, onMessage, onOnlineCount, onDisconnect) {
    let stompClient = null;
    let pollInterval = null;

    function startPolling() {
        if (!pollInterval) pollInterval = setInterval(loadNewMessages, 2000);
    }

    async function loadNewMessages() {
        try {
            const msgs = await (await fetch(`/api/tournament/${lineupId}?after=${window.lastMessageId || 0}`, { credentials: 'same-origin' })).json();
            msgs.forEach(m => {
                if (m.id > (window.lastMessageId || 0)) {
                    window.lastMessageId = m.id;
                    onMessage(m);
                }
            });
        } catch(e) {}
    }

    try {
        const socket = new SockJS('/ws');
        stompClient = new StompJs.Client({
            webSocketFactory: () => socket,
            debug: function() {},
            onConnect: function() {
                stompClient.subscribe('/topic/chat/' + lineupId, function(message) {
                    const msg = JSON.parse(message.body);
                    onMessage(msg);
                });
                stompClient.subscribe('/topic/chat/' + lineupId + '/online', function(message) {
                    const count = JSON.parse(message.body);
                    onOnlineCount(count);
                });
                if (pollInterval) { clearInterval(pollInterval); pollInterval = null; }
            },
            onDisconnect: function() { startPolling(); onDisconnect(); },
            onStompError: function() { startPolling(); onDisconnect(); }
        });
        stompClient.activate();
    } catch(e) {
        startPolling();
    }

    return {
        sendMessage: function(body) {
            if (stompClient && stompClient.active) {
                stompClient.publish({ destination: '/app/chat/' + lineupId, body: JSON.stringify(body) });
                return true;
            }
            return false;
        },
        disconnect: function() {
            if (stompClient) {
                try { stompClient.deactivate(); } catch(e) {}
            }
            if (pollInterval) { clearInterval(pollInterval); pollInterval = null; }
        }
    };
}