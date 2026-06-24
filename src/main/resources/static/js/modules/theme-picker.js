// js/modules/theme-picker.js
export function initThemePicker() {
    if (document.getElementById('themeSheetOverlay')) return;

    const overlay = document.createElement('div');
    overlay.id = 'themeSheetOverlay';
    overlay.className = 'theme-sheet-overlay';
    overlay.onclick = () => toggleThemeSheet();

    const sheet = document.createElement('div');
    sheet.className = 'theme-sheet';
    sheet.onclick = (e) => e.stopPropagation();

    sheet.innerHTML = `
        <div class="theme-sheet-handle"></div>
        <h3 class="theme-sheet-title">Выбор темы</h3>
        <button class="theme-sheet-btn" data-theme="dark" onclick="window.setTheme('dark');window.toggleThemeSheet()">
            <span style="width:40px;height:24px;border-radius:6px;background:#6366f1;flex-shrink:0;"></span>
            <div style="flex:1;">
                <div class="theme-name">Deep Space</div>
                <div class="theme-desc">Тёмно-синие звёзды, фиолетовые туманности</div>
            </div>
            <span class="theme-check" style="color:#818cf8;font-size:1.2rem;display:none;">✓</span>
        </button>
        <button class="theme-sheet-btn" data-theme="ocean" onclick="window.setTheme('ocean');window.toggleThemeSheet()">
            <span style="width:40px;height:24px;border-radius:6px;background:#06b6d4;flex-shrink:0;"></span>
            <div style="flex:1;">
                <div class="theme-name">Северное сияние</div>
                <div class="theme-desc">Бирюзовые переливы, ледяное свечение</div>
            </div>
            <span class="theme-check" style="color:#22d3ee;font-size:1.2rem;display:none;">✓</span>
        </button>
        <button class="theme-sheet-btn" data-theme="mars" onclick="window.setTheme('mars');window.toggleThemeSheet()">
            <span style="width:40px;height:24px;border-radius:6px;background:#f59e0b;flex-shrink:0;"></span>
            <div style="flex:1;">
                <div class="theme-name">Марсианская пустыня</div>
                <div class="theme-desc">Тёплый песок, оранжевые дюны</div>
            </div>
            <span class="theme-check" style="color:#fbbf24;font-size:1.2rem;display:none;">✓</span>
        </button>
    `;

    overlay.appendChild(sheet);
    document.body.appendChild(overlay);

    updateActiveInSheet();
    renderThemeListInline();
}

export function toggleThemeSheet() {
    const overlay = document.getElementById('themeSheetOverlay');
    if (overlay) {
        overlay.classList.toggle('open');
        updateActiveInSheet();
    }
}

export function setTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
    updateActiveInSheet();
    renderThemeListInline();

    fetch('/api/auth/me/theme', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin',
        body: JSON.stringify({ theme: theme })
    }).catch(() => {});
}

function updateActiveInSheet() {
    const current = document.documentElement.getAttribute('data-theme');
    document.querySelectorAll('.theme-sheet-btn').forEach(btn => {
        const check = btn.querySelector('.theme-check');
        if (check) {
            check.style.display = btn.dataset.theme === current ? 'inline' : 'none';
        }
    });
}

function renderThemeListInline() {
    const container = document.getElementById('themeListInline');
    if (!container) return;

    const themes = [
        { id: 'dark', name: 'Deep Space', desc: 'Тёмно-синие звёзды, фиолетовые туманности', color: '#6366f1', checkColor: '#818cf8' },
        { id: 'ocean', name: 'Северное сияние', desc: 'Бирюзовые переливы, ледяное свечение', color: '#06b6d4', checkColor: '#22d3ee' },
        { id: 'mars', name: 'Марсианская пустыня', desc: 'Тёплый песок, оранжевые дюны', color: '#f59e0b', checkColor: '#fbbf24' }
    ];

    const current = document.documentElement.getAttribute('data-theme');

    container.innerHTML = themes.map(t => `
        <button class="theme-sheet-btn" style="width:100%;" onclick="window.setTheme('${t.id}')">
            <span style="width:40px;height:24px;border-radius:6px;background:${t.color};flex-shrink:0;"></span>
            <div style="flex:1;">
                <div class="theme-name">${t.name}</div>
                <div class="theme-desc">${t.desc}</div>
            </div>
            <span class="theme-check" style="color:${t.checkColor};font-size:1.2rem;display:${t.id === current ? 'inline' : 'none'};">✓</span>
        </button>
    `).join('');
}