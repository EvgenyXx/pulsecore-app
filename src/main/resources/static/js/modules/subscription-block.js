export const subBlockHtml = () => `
    <div class="flex items-center justify-between mb-4">
        <div></div>
        <button onclick="toggleMobileMenu()" class="md:hidden w-9 h-9 flex items-center justify-center rounded-lg bg-white/5 hover:bg-white/10 active:scale-90 text-white">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
        </button>
    </div>
    <div class="sub-block" style="animation: fadeIn 0.25s cubic-bezier(0.25, 0.1, 0.25, 1)">
        <div class="sub-block-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#0a84ff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
        </div>
        <h3 class="sub-block-title">Требуется подписка</h3>
        <p class="sub-block-text">Оформите подписку чтобы открыть все функции</p>
        <a href="/subscribe" class="sub-block-btn">Оформить подписку</a>
    </div>
`;

export async function checkSubscription() {
    try {
        const res = await fetch('/api/player/subscription', { credentials: 'same-origin' });
        if (res.status === 402) return false;
        if (!res.ok) return false;
        const data = await res.json();
        return data && data.active === true;
    } catch (e) {
        return false;
    }
}

window.subBlockHtml = subBlockHtml;
window.checkSubscription = checkSubscription;