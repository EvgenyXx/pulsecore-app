// js/modules/chart-tooltip.js
export function externalTooltipHandler(context) {
    const { chart, tooltip } = context;
    let el = document.getElementById('chartjs-tooltip');

    if (!el) {
        el = document.createElement('div');
        el.id = 'chartjs-tooltip';
        document.body.appendChild(el);
    }

    if (tooltip.opacity === 0) {
        el.style.opacity = 0;
        return;
    }

    if (tooltip.body) {
        const title = tooltip.title?.[0] || '';
        const value = tooltip.body[0]?.lines?.[0] || '';
        el.innerHTML = `
            <div class="ct-title">${title}</div>
            <div class="ct-value">${value}</div>
        `;
    }

    const canvasRect = chart.canvas.getBoundingClientRect();

    el.style.opacity = 1;
    el.style.left = (canvasRect.left + window.pageXOffset + tooltip.caretX) + 'px';
    el.style.top = (canvasRect.top + window.pageYOffset + tooltip.caretY - 12) + 'px';
}