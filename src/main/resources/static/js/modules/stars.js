// js/modules/stars.js
export function initStars() {
    const container = document.getElementById('starsContainer');
    if (!container) return;

    let currentTheme = document.documentElement.getAttribute('data-theme') || 'dark';
    let animationId = null;
    let canvas = null;
    let ctx = null;
    let w = 0;
    let h = 0;
    const dpr = window.devicePixelRatio || 1;
    let stars = [];
    let bgRgb = '12,12,24'; // fallback

    const STAR_COUNT = 400;
    const MAX_DEPTH = 1000;
    const SPEED = 1.5;
    const FOV = 300;

    const cosmicThemes = ['dark', 'ocean', 'aurora', 'neon'];

    // Фоны тем (совпадают с --bg в app.css)
    const themeBg = {
        dark:  '12,12,24',    // #0c0c18
        ocean: '6,13,26',     // #060d1a
        aurora:'12,12,24',
        neon:  '12,12,24',
    };

    // ===== МАРС (DOM, как было) =====
    function renderMars() {
        let html = '';

        html += `<div style="position:absolute; bottom:28%; left:50%; transform:translateX(-50%); width:80px; height:80px; pointer-events:none;">
            <div style="width:80px;height:80px;border-radius:50%;background:radial-gradient(circle,rgba(255,220,150,1) 0%,rgba(255,180,80,0.8) 20%,rgba(255,140,40,0.3) 50%,transparent 70%);box-shadow:0 0 80px 40px rgba(255,180,80,0.5),0 0 160px 80px rgba(255,140,40,0.3),0 0 240px 120px rgba(255,120,30,0.15);"></div>
        </div>`;

        html += `<div style="position:absolute; bottom:20%; left:0; width:40%; height:35%; pointer-events:none;">
            <svg viewBox="0 0 400 300" preserveAspectRatio="none" style="width:100%;height:100%;">
                <polygon points="0,300 0,120 60,100 120,140 180,80 240,130 300,200 350,180 400,220 400,300" fill="rgba(30,15,5,0.7)"/>
                <polygon points="0,300 0,140 80,130 160,160 220,100 300,150 400,190 400,300" fill="rgba(40,20,8,0.5)"/>
            </svg>
        </div>`;

        html += `<div style="position:absolute; bottom:22%; right:0; width:35%; height:30%; pointer-events:none;">
            <svg viewBox="0 0 350 250" preserveAspectRatio="none" style="width:100%;height:100%;">
                <polygon points="0,250 50,100 100,130 160,70 220,120 280,160 350,100 350,250" fill="rgba(35,18,8,0.65)"/>
                <polygon points="0,250 80,140 140,160 200,90 260,140 350,110 350,250" fill="rgba(45,22,10,0.45)"/>
            </svg>
        </div>`;

        html += `<div style="position:absolute; bottom:18%; left:0; width:100%; height:12%; pointer-events:none;">
            <svg viewBox="0 0 1000 100" preserveAspectRatio="none" style="width:100%;height:100%;">
                <path d="M0,100 L0,50 Q100,20 200,45 Q350,10 500,40 Q650,15 800,35 Q900,25 1000,50 L1000,100 Z" fill="rgba(80,40,15,0.5)"/>
                <path d="M0,100 L0,60 Q150,35 300,55 Q500,25 700,50 Q850,30 1000,60 L1000,100 Z" fill="rgba(100,50,20,0.35)"/>
                <path d="M0,100 L0,70 Q200,50 400,65 Q600,40 800,60 Q900,50 1000,70 L1000,100 Z" fill="rgba(120,60,25,0.25)"/>
            </svg>
        </div>`;

        html += `<div style="position:absolute; bottom:0; left:0; width:100%; height:22%; pointer-events:none; background:linear-gradient(180deg,rgba(180,100,40,0.15) 0%,rgba(140,70,25,0.4) 40%,rgba(100,45,15,0.6) 100%);"></div>`;

        for (let i = 0; i < 20; i++) {
            const size = Math.random() * 3 + 1.5;
            const x = Math.random() * 100;
            const y = Math.random() * 60 + 20;
            const speed = Math.random() * 6 + 5;
            const opacity = Math.random() * 0.5 + 0.3;
            html += `<div style="position:absolute; left:${x}%; top:${y}%;width:${size}px; height:${size * 0.6}px;background:rgba(220,160,80,${opacity});border-radius:1px;animation:sandDrift ${speed}s linear infinite;animation-delay:${Math.random() * speed}s;--drift-x:${Math.random() * 80 - 40}px;--drift-y:${Math.random() * 30 - 15}px;pointer-events:none;"></div>`;
        }

        for (let i = 0; i < 50; i++) {
            const size = Math.random() * 1.5 + 0.3;
            const x = Math.random() * 100;
            const y = Math.random() * 80 + 10;
            const opacity = Math.random() * 0.35 + 0.1;
            html += `<div style="position:absolute; left:${x}%; top:${y}%;width:${size}px; height:${size * 0.5}px;background:rgba(200,140,60,${opacity});border-radius:1px;animation:sandDrift ${Math.random() * 8 + 6}s linear infinite;animation-delay:${Math.random() * 6}s;pointer-events:none;"></div>`;
        }

        html += `<div style="position:absolute; bottom:25%; left:0; width:100%; height:30%; pointer-events:none; background:radial-gradient(ellipse at center,rgba(255,180,80,0.12) 0%,transparent 70%);"></div>`;

        container.innerHTML = html;
    }

    // ===== КОСМОС (canvas) =====
    function randomColor() {
        const r = Math.random();
        if (r < 0.1) return '200,220,255';
        if (r > 0.92) return '255,232,200';
        return '255,255,255';
    }

    function makeStar(spawnAtFar) {
        return {
            x: (Math.random() - 0.5) * w * 2,
            y: (Math.random() - 0.5) * h * 2,
            z: spawnAtFar ? MAX_DEPTH * Math.random() : MAX_DEPTH,
            color: randomColor(),
        };
    }

    function resizeCanvas() {
        w = container.clientWidth;
        h = container.clientHeight;
        canvas.width = w * dpr;
        canvas.height = h * dpr;
        ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
        // Первый кадр — сразу фон темы, без чёрного
        ctx.fillStyle = `rgb(${bgRgb})`;
        ctx.fillRect(0, 0, w, h);
    }

    function resetStars() {
        stars = [];
        for (let i = 0; i < STAR_COUNT; i++) stars.push(makeStar(true));
    }

    function drawFrame() {
        // Хвост — затухание в цвет фона темы, а не в чёрный
        ctx.fillStyle = `rgba(${bgRgb},0.35)`;
        ctx.fillRect(0, 0, w, h);

        const cx = w / 2;
        const cy = h / 2;

        for (let i = 0; i < stars.length; i++) {
            const s = stars[i];
            s.z -= SPEED;

            if (s.z <= 0) {
                stars[i] = makeStar(false);
                continue;
            }

            const k = FOV / s.z;
            const px = s.x * k + cx;
            const py = s.y * k + cy;

            if (px < 0 || px > w || py < 0 || py > h) continue;

            const depthRatio = 1 - s.z / MAX_DEPTH;
            const size = Math.max(0.4, depthRatio * 2.4);
            const alpha = Math.max(0.1, depthRatio);

            ctx.beginPath();
            ctx.fillStyle = `rgba(${s.color},${alpha})`;
            ctx.arc(px, py, size, 0, Math.PI * 2);
            ctx.fill();
        }

        animationId = requestAnimationFrame(drawFrame);
    }

    function startCosmos() {
        stopAll();
        container.innerHTML = '<canvas id="starsCanvas" style="width:100%;height:100%;display:block;"></canvas>';
        canvas = document.getElementById('starsCanvas');
        ctx = canvas.getContext('2d');
        resizeCanvas();
        resetStars();
        drawFrame();
    }

    function stopAll() {
        if (animationId) {
            cancelAnimationFrame(animationId);
            animationId = null;
        }
        canvas = null;
        ctx = null;
    }

    function render() {
        const theme = document.documentElement.getAttribute('data-theme') || 'dark';
        if (cosmicThemes.includes(theme)) {
            bgRgb = themeBg[theme] || themeBg.dark;
            startCosmos();
        } else if (theme === 'mars') {
            stopAll();
            renderMars();
        } else {
            stopAll();
            container.innerHTML = '';
        }
    }

    window.addEventListener('resize', () => {
        if (canvas && ctx) {
            resizeCanvas();
            resetStars();
        }
    });

    const observer = new MutationObserver(() => {
        const t = document.documentElement.getAttribute('data-theme') || 'dark';
        if (t !== currentTheme) {
            currentTheme = t;
            render();
        }
    });
    observer.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });

    render();
}