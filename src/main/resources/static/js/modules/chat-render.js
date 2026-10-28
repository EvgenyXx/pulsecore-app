import { escapeHtml, highlightMentions } from './chat-core.js';

export const SEND_ICON = `
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <line x1="22" y1="2" x2="11" y2="13"></line>
        <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
    </svg>
`;

export function renderMessage(m, currentPlayerName = '') {
    const t = m.createdAt ? new Date(m.createdAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '';
    const sender = escapeHtml(m.playerName || '');
    const content = highlightMentions(escapeHtml(m.message || ''));
    const editedBadge = m.edited ? ' <span style="color:rgba(255,255,255,0.4);font-size:10px;">(изм.)</span>' : '';

    const isMine = currentPlayerName && sender.toLowerCase() === currentPlayerName.toLowerCase();
    const mineClass = isMine ? 'is-mine' : '';

    let replyHtml = '';
    if (m.replyToId) {
        replyHtml = `
            <div class="chat-reply">
                <div class="chat-reply-sender">${escapeHtml(m.replyToSenderName || '')}</div>
                <div class="chat-reply-text">${escapeHtml(m.replyToContent || '')}</div>
            </div>
        `;
    }

    // Убрали span.chat-dots, теперь клик вешаем на сам пузырь
    return `
        <div class="chat-message ${mineClass}" 
             data-message-id="${m.id || ''}" 
             data-sender="${sender.replace(/"/g, '&quot;')}" 
             data-content="${escapeHtml(m.message || '').replace(/"/g, '&quot;')}">
            ${!isMine ? `<div class="chat-sender">${sender}${editedBadge}</div>` : ''}
            ${replyHtml}
            <div class="chat-text">${content}</div>
            <div class="chat-time">${t}</div>
        </div>
    `;
}