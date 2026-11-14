import { escapeHtml, highlightMentions } from './chat-core.js';

export const SEND_ICON = `
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <line x1="22" y1="2" x2="11" y2="13"></line>
        <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
    </svg>
`;

const NAME_COLORS = ['#e17076', '#7bc862', '#e5ca77', '#65aadd', '#a695e7', '#ee7aae', '#6ec9cb', '#faa774'];
const AVATAR_COLORS = ['rgba(225,112,118,0.2)', 'rgba(123,200,98,0.2)', 'rgba(229,202,119,0.2)', 'rgba(101,170,221,0.2)', 'rgba(166,149,231,0.2)', 'rgba(238,122,174,0.2)', 'rgba(110,201,203,0.2)', 'rgba(250,167,116,0.2)'];

function getHash(name) {
    let hash = 0;
    if (!name) return 0;
    for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    return Math.abs(hash);
}

export function renderMessage(m, currentPlayerName = '', prevMessage = null) {
    const date = m.createdAt ? new Date(m.createdAt) : new Date();
    const t = date.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' });

    const sender = escapeHtml(m.playerName || 'Аноним');
    const content = highlightMentions(escapeHtml(m.message || ''));
    const editedBadge = m.edited ? ' <span class="chat-edited">(изм.)</span>' : '';

    const isMine = currentPlayerName && sender.toLowerCase() === currentPlayerName.toLowerCase();
    const mineClass = isMine ? 'is-mine' : '';

    const hash = getHash(sender);
    const nameColor = isMine ? 'inherit' : NAME_COLORS[hash % NAME_COLORS.length];
    const avatarBg = AVATAR_COLORS[hash % AVATAR_COLORS.length];

    let showAvatarAndName = true;
    if (prevMessage && prevMessage.playerName === m.playerName) {
        const prevDate = new Date(prevMessage.createdAt);
        if ((date - prevDate) < 300000) {
            showAvatarAndName = false;
        }
    }

    // ОТРИСОВКА ПЛАШКИ (ЕДИНАЯ ДЛЯ ОТВЕТА И УПОМИНАНИЯ)
    let replyHtml = '';

    let replyName = m.replyToSenderName;
    let replyContent = m.replyToContent;

    if (!replyName && m.mentionedPlayerId) {
        // Если это упоминание, берем имя из кэша (заполняется во фронте)
        replyName = window.mentionNamesCache ? window.mentionNamesCache[m.mentionedPlayerId] : "Упоминание";
        replyContent = ""; // Текст внутри плашки не нужен
    }

    if (replyName) {
        const replyColor = NAME_COLORS[getHash(replyName) % NAME_COLORS.length];
        replyHtml = `
            <div class="tg-reply" style="border-left-color: ${replyColor};">
                <div class="tg-reply-name" style="color: ${replyColor};">${escapeHtml(replyName)}</div>
                ${replyContent ? `<div class="tg-reply-text">${escapeHtml(replyContent)}</div>` : ''}
            </div>
        `;
    }

    const checks = isMine ? `<span class="tg-checks"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg></span>` : '';

    const compactClass = !showAvatarAndName ? 'compact' : '';

    return `
        <div class="tg-message-wrapper ${compactClass}">
            ${!isMine ? `
                <div class="tg-avatar-slot">
                    ${showAvatarAndName ? `<div class="tg-avatar" style="background: ${avatarBg}; color: ${nameColor};">${sender.charAt(0).toUpperCase()}</div>` : ''}
                </div>
            ` : ''}
            
            <div class="tg-message ${mineClass}" data-message-id="${m.id || ''}" data-sender="${sender.replace(/"/g, '&quot;')}" data-content="${escapeHtml(m.message || '').replace(/"/g, '&quot;')}">
                ${!isMine && showAvatarAndName ? `<div class="tg-sender" style="color: ${nameColor};">${sender}</div>` : ''}
                ${replyHtml}
                <div class="tg-text">${content}${editedBadge}</div>
                <div class="tg-meta">
                    <span class="tg-time">${t}</span>
                    ${checks}
                </div>
            </div>
        </div>
    `;
}