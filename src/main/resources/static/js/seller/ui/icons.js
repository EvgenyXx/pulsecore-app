/**
 * SVG иконки селлер-панели (Feather, stroke=currentColor).
 * Использование: window.SellerIcons.navProducts
 */
window.SellerIcons = (function () {
    const svg = (paths, size = 16, stroke = 2) =>
        `<svg width="${size}" height="${size}" viewBox="0 0 24 24" ` +
        `fill="none" stroke="currentColor" stroke-width="${stroke}" ` +
        `stroke-linecap="round" stroke-linejoin="round">${paths}</svg>`;

    const svgFilled = (inner, size = 24) =>
        `<svg width="${size}" height="${size}" viewBox="0 0 24 24" ` +
        `fill="currentColor" xmlns="http://www.w3.org/2000/svg">${inner}</svg>`;

    return {
        // ===== Карточка заказа =====
        user: svg(`<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>`),

        phone: svg(`<path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>`),

        pin: svg(`<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>`),

        card: svg(`<rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/>`),

        chat: svg(`<path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>`),

        camera: svg(`<path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/>`, 24),

        box: svg(`<path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/>`, 32),

        // ===== Нижняя панель =====

        // PC монограмма — PulseCore
        navApp: svgFilled(`
            <circle cx="12" cy="12" r="11"/>
            <text x="12" y="16.5"
                  text-anchor="middle"
                  font-family="Inter, -apple-system, BlinkMacSystemFont, sans-serif"
                  font-size="10"
                  font-weight="800"
                  letter-spacing="0.3"
                  fill="#0a0b0d">PC</text>
        `, 24),

        // Товары — коробка/пакет
        navProducts: svg(`
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
            <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
            <line x1="12" y1="22.08" x2="12" y2="12"/>
        `, 24),

        // Заказы — сумка
        navOrders: svg(`
            <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
            <line x1="3" y1="6" x2="21" y2="6"/>
            <path d="M16 10a4 4 0 0 1-8 0"/>
        `, 24),

        // Категории — 3×3 точки
        navCategories: svg(`
            <circle cx="5" cy="5" r="1.6"/>
            <circle cx="12" cy="5" r="1.6"/>
            <circle cx="19" cy="5" r="1.6"/>
            <circle cx="5" cy="12" r="1.6"/>
            <circle cx="12" cy="12" r="1.6"/>
            <circle cx="19" cy="12" r="1.6"/>
            <circle cx="5" cy="19" r="1.6"/>
            <circle cx="12" cy="19" r="1.6"/>
            <circle cx="19" cy="19" r="1.6"/>
        `, 24)
    };
})();