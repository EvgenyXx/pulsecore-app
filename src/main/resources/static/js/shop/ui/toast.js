window.Toast = (function () {
    let containerEl = null;

    function ensureContainer() {
        if (containerEl) return containerEl;
        containerEl = document.createElement('div');
        containerEl.id = 'toastContainer';
        containerEl.className = 'toast-container';
        document.body.appendChild(containerEl);
        return containerEl;
    }

    function iconSvg(ok) {
        if (ok) {
            return `<svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2.2"
                         stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 6L9 17l-5-5"/>
                    </svg>`;
        }
        return `<svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2.2"
                     stroke-linecap="round" stroke-linejoin="round">
                    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                    <line x1="12" y1="9" x2="12" y2="13"/>
                    <line x1="12" y1="17" x2="12.01" y2="17"/>
                </svg>`;
    }

    function show(el, text, ok) {
        if (el) {
            el.textContent = text;
            el.className = 'message ' + (ok ? 'ok' : 'error');
            el.classList.remove('hidden');
            return;
        }
        showPopup(text, ok);
    }

    function showPopup(text, ok, duration) {
        const wrap = ensureContainer();

        const toast = document.createElement('div');
        toast.className = 'toast ' + (ok ? 'toast-ok' : 'toast-error');
        toast.innerHTML = `
            <span class="toast-icon">${iconSvg(ok)}</span>
            <span class="toast-text">${escapeHtml(text)}</span>
        `;

        wrap.appendChild(toast);

        requestAnimationFrame(() => toast.classList.add('toast-visible'));

        const ttl = duration || (ok ? 2500 : 5000);

        let removed = false;
        const remove = () => {
            if (removed) return;
            removed = true;
            toast.classList.remove('toast-visible');
            setTimeout(() => toast.remove(), 250);
        };

        toast.addEventListener('click', remove);
        setTimeout(remove, ttl);
    }

    function showPopupProblems(title, problems) {
        if (!problems || problems.length === 0) {
            return showPopup(title, false);
        }

        const wrap = ensureContainer();

        const toast = document.createElement('div');
        toast.className = 'toast toast-error';
        toast.innerHTML = `
            <span class="toast-icon">${iconSvg(false)}</span>
            <span class="toast-body">
                <span class="toast-title">${escapeHtml(title)}</span>
                <ul class="toast-problems">
                    ${problems.map(p => `<li>${formatProblem(p)}</li>`).join('')}
                </ul>
            </span>
        `;

        wrap.appendChild(toast);
        requestAnimationFrame(() => toast.classList.add('toast-visible'));

        let removed = false;
        const remove = () => {
            if (removed) return;
            removed = true;
            toast.classList.remove('toast-visible');
            setTimeout(() => toast.remove(), 250);
        };

        toast.addEventListener('click', remove);
        setTimeout(remove, 7000);
    }

    function formatProblem(p) {
        let line = '«' + escapeHtml(p.productName) + '»';
        if (p.variantLabel) line += ' · ' + escapeHtml(p.variantLabel);

        if (p.type === 'OUT_OF_STOCK') {
            line += ' — нет в наличии';
        } else if (p.type === 'STOCK_SHORTAGE') {
            line += ' — в наличии ' + p.available + ' шт';
        } else if (p.type === 'INACTIVE') {
            line += ' — больше не продаётся';
        }
        return line;
    }

    function hide(el) {
        if (el) el.classList.add('hidden');
    }

    function escapeHtml(s) {
        return String(s ?? '').replace(/[&<>"']/g, ch => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
        })[ch]);
    }

    return { show, hide, showPopup, showPopupProblems };
})();