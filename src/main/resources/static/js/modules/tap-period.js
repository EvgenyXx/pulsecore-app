/**
 * Тап по экрану → переключение периода зала славы.
 * 1 тап  → вперёд (week → month → year)
 * 2 тапа → назад  (year → month → week)
 *
 * Не конфликтует со свайпом по .league-pill — тапы внутри .league-pill игнорируются.
 */

const PERIODS = ['week', 'month', 'year'];
const DOUBLE_TAP_MS = 300;

export function initTapPeriod(options = {}) {
    const {
        container = document.body,
        excludeSelectors = ['.league-pill', '.period-pill'],
        isEnabled = () => true,
        getCurrent = () => 'week',
        setPeriod = () => {},
        doubleTapMs = DOUBLE_TAP_MS
    } = options;

    const root = typeof container === 'string'
        ? document.querySelector(container)
        : container;

    if (!root) {
        console.warn('[tap-period] container не найден');
        return null;
    }

    let lastTapAt = 0;
    let singleTapTimer = null;
    let startX = 0;
    let startY = 0;

    function isExcluded(target) {
        return excludeSelectors.some(sel => target.closest(sel));
    }

    function stepPeriod(current, direction) {
        const idx = PERIODS.indexOf(current);
        if (idx === -1) return null;

        const next = idx + direction;
        if (next < 0 || next >= PERIODS.length) return null;

        return PERIODS[next];
    }

    function applyPeriod(direction) {
        const current = getCurrent();
        const next = stepPeriod(current, direction);
        if (!next) return;

        setPeriod(next);
    }

    function onTouchStart(e) {
        if (!isEnabled()) return;
        if (isExcluded(e.target)) return;

        const t = e.touches[0];
        startX = t.clientX;
        startY = t.clientY;
    }

    function onTouchEnd(e) {
        if (!isEnabled()) return;
        if (isExcluded(e.target)) return;

        const t = e.changedTouches[0];
        const dx = Math.abs(t.clientX - startX);
        const dy = Math.abs(t.clientY - startY);

        // Это был свайп, не тап
        if (dx > 10 || dy > 10) return;

        const now = Date.now();
        const isDouble = now - lastTapAt < doubleTapMs;

        if (isDouble) {
            // Двойной тап — отменяем одиночный, идём назад
            clearTimeout(singleTapTimer);
            singleTapTimer = null;
            lastTapAt = 0;

            applyPeriod(-1);
        } else {
            // Одинарный тап — ждём, вдруг будет второй
            lastTapAt = now;
            clearTimeout(singleTapTimer);
            singleTapTimer = setTimeout(() => {
                singleTapTimer = null;
                lastTapAt = 0;
                applyPeriod(1);
            }, doubleTapMs);
        }
    }

    root.addEventListener('touchstart', onTouchStart, { passive: true });
    root.addEventListener('touchend', onTouchEnd, { passive: true });

    return {
        destroy() {
            root.removeEventListener('touchstart', onTouchStart);
            root.removeEventListener('touchend', onTouchEnd);
            if (singleTapTimer) clearTimeout(singleTapTimer);
        }
    };
}