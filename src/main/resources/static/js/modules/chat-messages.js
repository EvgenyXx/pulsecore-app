import { escapeHtml, highlightMentions } from './chat-core.js';

export const SEND_ICON = `
    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="none">
        <path d="M22 2L11 13"/><polygon points="22 2 15 22 11 13 2 9 22 2" fill="currentColor"/>
    </svg>
`;

export function renderMessage(m) {
    const t = m.createdAt ? new Date(m.createdAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '';
    const sender = escapeHtml(m.playerName || '');
    const content = highlightMentions(escapeHtml(m.message || ''));
    const editedBadge = m.edited ? ' <span style="color:#71717a;font-size:0.65rem;">(изм.)</span>' : '';
    let replyHtml = '';
    if (m.replyToId) {
        replyHtml = `<div class="reply-preview-bar"><div class="reply-sender">${escapeHtml(m.replyToSenderName || '')}</div><div class="reply-content">${escapeHtml(m.replyToContent || '')}</div></div>`;
    }
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