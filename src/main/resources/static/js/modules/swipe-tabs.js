export function initSwipeTabs(options) {
    const { contexts = [] } = options;

    if (!contexts.length) {
        console.warn('[swipe-tabs] нет контекстов');
        return null;
    }

    let startX = 0;
    let startY = 0;
    let tracking = false;
    let verticalDetected = false;

    function getActiveContext(e) {
        const enabled = contexts
            .filter(c => {
                if (typeof c.isEnabled === 'function' && !c.isEnabled()) return false;
                if (typeof c.isInZone === 'function') {
                    const touch = e?.touches?.[0] || e?.changedTouches?.[0];
                    if (!touch) return true;
                    const el = document.elementFromPoint(touch.clientX, touch.clientY);
                    return c.isInZone(el);
                }
                return true;
            })
            .sort((a, b) => (b.priority || 0) - (a.priority || 0));
        return enabled[0] || null;
    }

    function getTabs(ctx) {
        const root = typeof ctx.container === 'string'
            ? document.querySelector(ctx.container)
            : ctx.container || document.body;
        return root.querySelectorAll(ctx.tabSelector);
    }

    function getActiveIndex(tabs, activeClass) {
        let index = -1;
        tabs.forEach((t, i) => {
            if (t.classList.contains(activeClass || 'active')) index = i;
        });
        return index;
    }

    function switchTab(ctx, direction) {
        const tabs = getTabs(ctx);
        if (!tabs.length) return;

        const current = getActiveIndex(tabs, ctx.activeClass);
        if (current === -1) return;

        const next = current + direction;
        if (next < 0 || next >= tabs.length) return;

        tabs[next].click();

        if (typeof ctx.onChange === 'function') {
            ctx.onChange(tabs[next], next);
        }
    }

    function onTouchStart(e) {
        const ctx = getActiveContext(e);
        if (!ctx) {
            tracking = false;
            return;
        }

        const t = e.target;
        const tag = t.tagName;
        const ignoreTags = ctx.ignoreTags || ['INPUT', 'BUTTON', 'A', 'SELECT', 'TEXTAREA'];

        if (ignoreTags.includes(tag) && !t.matches(ctx.tabSelector)) {
            tracking = false;
            return;
        }

        startX = e.touches[0].clientX;
        startY = e.touches[0].clientY;
        tracking = true;
        verticalDetected = false;
    }

    function onTouchMove(e) {
        if (!tracking) return;

        const dx = Math.abs(e.touches[0].clientX - startX);
        const dy = Math.abs(e.touches[0].clientY - startY);

        if (dy > dx && dy > 10) {
            verticalDetected = true;
            tracking = false;
        }
    }

    function onTouchEnd(e) {
        if (!tracking || verticalDetected) {
            tracking = false;
            return;
        }
        tracking = false;

        const ctx = getActiveContext(e);
        if (!ctx) return;

        const endX = e.changedTouches[0].clientX;
        const endY = e.changedTouches[0].clientY;

        const deltaX = endX - startX;
        const deltaY = endY - startY;

        const maxDeltaY = ctx.maxDeltaY || 80;
        const thresholdX = ctx.thresholdX || 60;

        if (Math.abs(deltaY) > maxDeltaY) return;
        if (Math.abs(deltaX) < thresholdX) return;

        if (e.cancelable) e.preventDefault();

        switchTab(ctx, deltaX < 0 ? 1 : -1);
    }

    document.body.addEventListener('touchstart', onTouchStart, { passive: true });
    document.body.addEventListener('touchmove', onTouchMove, { passive: true });
    document.body.addEventListener('touchend', onTouchEnd, { passive: false });

    return {
        getActiveContext,
        destroy() {
            document.body.removeEventListener('touchstart', onTouchStart);
            document.body.removeEventListener('touchmove', onTouchMove);
            document.body.removeEventListener('touchend', onTouchEnd);
        }
    };
}