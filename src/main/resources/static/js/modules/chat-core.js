export function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"]/g, c => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;' })[c]);
}

export function highlightMentions(text) {
    return text.replace(/@([\p{L}]+\s+[\p{L}]+)/gu, '<span style="color:#818cf8;font-weight:600;">@$1</span>');
}

export const SEND_ICON = `
    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="none">
        <path d="M22 2L11 13"/><polygon points="22 2 15 22 11 13 2 9 22 2" fill="currentColor"/>
    </svg>
`;