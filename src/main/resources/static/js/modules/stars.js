// js/modules/stars.js
export function initStars() {
    const container = document.getElementById('starsContainer');
    if (!container) return;

    const theme = document.documentElement.getAttribute('data-theme') || 'dark';
    let html = '';

    if (theme === 'mars') {
        // === МАРСИАНСКАЯ ПУСТЫНЯ ===

        // Солнце с лучами
        html += `
            <div style="position:absolute; bottom:28%; left:50%; transform:translateX(-50%); width:80px; height:80px; pointer-events:none;">
                <div style="width:80px;height:80px;border-radius:50%;background:radial-gradient(circle,rgba(255,220,150,1) 0%,rgba(255,180,80,0.8) 20%,rgba(255,140,40,0.3) 50%,transparent 70%);box-shadow:0 0 80px 40px rgba(255,180,80,0.5),0 0 160px 80px rgba(255,140,40,0.3),0 0 240px 120px rgba(255,120,30,0.15);"></div>
            </div>
        `;

        // Силуэты скал (слева)
        html += `
            <div style="position:absolute; bottom:20%; left:0; width:40%; height:35%; pointer-events:none;">
                <svg viewBox="0 0 400 300" preserveAspectRatio="none" style="width:100%;height:100%;">
                    <polygon points="0,300 0,120 60,100 120,140 180,80 240,130 300,200 350,180 400,220 400,300" fill="rgba(30,15,5,0.7)"/>
                    <polygon points="0,300 0,140 80,130 160,160 220,100 300,150 400,190 400,300" fill="rgba(40,20,8,0.5)"/>
                </svg>
            </div>
        `;

        // Силуэты скал (справа)
        html += `
            <div style="position:absolute; bottom:22%; right:0; width:35%; height:30%; pointer-events:none;">
                <svg viewBox="0 0 350 250" preserveAspectRatio="none" style="width:100%;height:100%;">
                    <polygon points="0,250 50,100 100,130 160,70 220,120 280,160 350,100 350,250" fill="rgba(35,18,8,0.65)"/>
                    <polygon points="0,250 80,140 140,160 200,90 260,140 350,110 350,250" fill="rgba(45,22,10,0.45)"/>
                </svg>
            </div>
        `;

        // Дюны на горизонте
        html += `
            <div style="position:absolute; bottom:18%; left:0; width:100%; height:12%; pointer-events:none;">
                <svg viewBox="0 0 1000 100" preserveAspectRatio="none" style="width:100%;height:100%;">
                    <path d="M0,100 L0,50 Q100,20 200,45 Q350,10 500,40 Q650,15 800,35 Q900,25 1000,50 L1000,100 Z" fill="rgba(80,40,15,0.5)"/>
                    <path d="M0,100 L0,60 Q150,35 300,55 Q500,25 700,50 Q850,30 1000,60 L1000,100 Z" fill="rgba(100,50,20,0.35)"/>
                    <path d="M0,100 L0,70 Q200,50 400,65 Q600,40 800,60 Q900,50 1000,70 L1000,100 Z" fill="rgba(120,60,25,0.25)"/>
                </svg>
            </div>
        `;

        // Песок на переднем плане
        html += `
            <div style="position:absolute; bottom:0; left:0; width:100%; height:22%; pointer-events:none; background:linear-gradient(180deg,rgba(180,100,40,0.15) 0%,rgba(140,70,25,0.4) 40%,rgba(100,45,15,0.6) 100%);"></div>
        `;

        // Летящий песок (ближний — крупный, 20 шт)
        for (let i = 0; i < 20; i++) {
            const size = Math.random() * 3 + 1.5;
            const x = Math.random() * 100;
            const y = Math.random() * 60 + 20;
            const speed = Math.random() * 6 + 5;
            const opacity = Math.random() * 0.5 + 0.3;
            html += `<div style="
                position:absolute; left:${x}%; top:${y}%;
                width:${size}px; height:${size * 0.6}px;
                background:rgba(220,160,80,${opacity});
                border-radius:1px;
                animation:sandDrift var(--speed) linear infinite;
                animation-delay:${Math.random() * speed}s;
                --speed:${speed}s;
                --drift-x:${Math.random() * 80 - 40}px;
                --drift-y:${Math.random() * 30 - 15}px;
                pointer-events:none;
            "></div>`;
        }

        // Мелкая пыль (50 шт)
        for (let i = 0; i < 50; i++) {
            const size = Math.random() * 1.5 + 0.3;
            const x = Math.random() * 100;
            const y = Math.random() * 80 + 10;
            const opacity = Math.random() * 0.35 + 0.1;
            html += `<div style="
                position:absolute; left:${x}%; top:${y}%;
                width:${size}px; height:${size * 0.5}px;
                background:rgba(200,140,60,${opacity});
                border-radius:1px;
                animation:sandDrift ${Math.random() * 8 + 6}s linear infinite;
                animation-delay:${Math.random() * 6}s;
                pointer-events:none;
            "></div>`;
        }

        // Тёплое зарево на горизонте
        html += `
            <div style="position:absolute; bottom:25%; left:0; width:100%; height:30%; pointer-events:none; background:radial-gradient(ellipse at center,rgba(255,180,80,0.12) 0%,transparent 70%);"></div>
        `;

    } else {
        // === КОСМОС ===

        // Ближний слой (6 шт)
        for (let i = 0; i < 6; i++) {
            const size = Math.random() * 6 + 4;
            const x = Math.random() * 100;
            const y = Math.random() * 100;
            const rotate = Math.random() * 360;
            const speed = Math.random() * 8 + 10;
            html += `<div class="star-layer-1" style="
                position:absolute; left:${x}%; top:${y}%;
                width:${size}px; height:${size}px;
                --speed:${speed}s;
                --drift:${Math.random() * 50 + 40}px;
                animation: driftHorizontal var(--speed) linear infinite;
                animation-delay: ${Math.random() * speed}s;
                filter: drop-shadow(0 0 ${size * 0.4}px rgba(200,220,255,0.8))
                        drop-shadow(0 0 ${size}px rgba(160,200,255,0.3));
                transform: rotate(${rotate}deg);
                pointer-events:none;
            "></div>`;
        }

        // Средний слой (25 шт)
        for (let i = 0; i < 25; i++) {
            const size = Math.random() * 3 + 1.5;
            const x = Math.random() * 100;
            const y = Math.random() * 100;
            const rotate = Math.random() * 360;
            const speed = Math.random() * 12 + 15;
            html += `<div class="star-layer-2" style="
                position:absolute; left:${x}%; top:${y}%;
                width:${size}px; height:${size}px;
                --speed:${speed}s;
                --drift:${Math.random() * 40 + 30}px;
                animation: driftHorizontal var(--speed) linear infinite;
                animation-delay: ${Math.random() * speed}s;
                filter: drop-shadow(0 0 ${size * 0.5}px rgba(200,220,255,0.6));
                transform: rotate(${rotate}deg);
                pointer-events:none;
            "></div>`;
        }

        // Дальний слой (40 шт)
        for (let i = 0; i < 40; i++) {
            const size = Math.random() * 2 + 0.8;
            const x = Math.random() * 100;
            const y = Math.random() * 100;
            const speed = Math.random() * 20 + 20;
            html += `<div class="star-layer-3" style="
                position:absolute; left:${x}%; top:${y}%;
                width:${size}px; height:${size}px;
                --speed:${speed}s;
                --drift:${Math.random() * 30 + 20}px;
                animation: driftHorizontal var(--speed) linear infinite;
                animation-delay: ${Math.random() * speed}s;
                filter: drop-shadow(0 0 ${size * 0.3}px rgba(180,200,255,0.4));
                transform: rotate(${Math.random() * 360}deg);
                pointer-events:none;
            "></div>`;
        }

        // Неподвижная пыль (60 шт)
        for (let i = 0; i < 60; i++) {
            const size = Math.random() * 0.8 + 0.2;
            const alpha = Math.random() * 0.4 + 0.15;
            html += `<div style="
                position:absolute; left:${Math.random() * 100}%; top:${Math.random() * 100}%;
                width:${size}px; height:${size}px;
                background:rgba(255,255,255,${alpha});
                border-radius:50%;
                pointer-events:none;
            "></div>`;
        }

        // Туманности (3 шт)
        for (let i = 0; i < 3; i++) {
            const size = Math.random() * 250 + 150;
            html += `<div style="
                position:absolute; left:${Math.random() * 80}%; top:${Math.random() * 80}%;
                width:${size}px; height:${size}px;
                background:radial-gradient(circle, rgba(80,100,180,0.05) 0%, transparent 70%);
                border-radius:50%;
                animation: nebulaDrift ${Math.random() * 18 + 15}s ease-in-out infinite;
                animation-delay: ${Math.random() * 10}s;
                pointer-events:none;
            "></div>`;
        }
    }

    container.innerHTML = html;
}