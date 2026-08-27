// js/compare/tooltip.js

export function initTooltips() {
    // Удаляем старые тултипы
    document.querySelectorAll('.tooltip-box').forEach(el => el.remove());

    document.querySelectorAll('[data-tooltip]').forEach(element => {
        let tooltipEl = null;

        function showTooltip() {
            // Закрываем все другие тултипы
            document.querySelectorAll('.tooltip-box').forEach(el => el.remove());
            tooltipEl = null;

            const text = element.getAttribute('data-tooltip');

            tooltipEl = document.createElement('div');
            tooltipEl.className = 'tooltip-box';
            tooltipEl.innerHTML = text;

            document.body.appendChild(tooltipEl);

            const rect = element.getBoundingClientRect();
            const tooltipRect = tooltipEl.getBoundingClientRect();

            let left = rect.left + rect.width / 2 - tooltipRect.width / 2;
            let top = rect.bottom + 8;

            if (left < 8) left = 8;
            if (left + tooltipRect.width > window.innerWidth - 8) {
                left = window.innerWidth - tooltipRect.width - 8;
            }

            if (top + tooltipRect.height > window.innerHeight - 8) {
                top = rect.top - tooltipRect.height - 8;
            }

            tooltipEl.style.left = left + 'px';
            tooltipEl.style.top = top + 'px';
            tooltipEl.style.pointerEvents = 'auto';
        }

        function hideTooltip() {
            if (tooltipEl) {
                tooltipEl.remove();
                tooltipEl = null;
            }
        }

        // ПК: наведение
        element.addEventListener('mouseenter', showTooltip);

        element.addEventListener('mouseleave', () => {
            setTimeout(hideTooltip, 300);
        });

        // Тап
        element.addEventListener('click', function(e) {
            e.stopPropagation();
            e.preventDefault();

            if (tooltipEl) {
                hideTooltip();
            } else {
                showTooltip();
            }
        });

        // Закрыть при клике вне — но НЕ сразу после открытия
        document.addEventListener('click', function(e) {
            if (tooltipEl && !element.contains(e.target) && !tooltipEl.contains(e.target)) {
                setTimeout(hideTooltip, 200);
            }
        });

        // Закрыть при скролле
        document.addEventListener('scroll', function() {
            if (tooltipEl) hideTooltip();
        }, true);
    });
}