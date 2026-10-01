window.ProductPage = (function () {
    const productsApi = window.ProductsApi;
    const loader = window.Loader;
    const cart = window.CartStore;

    let currentId = null;
    let currentProduct = null;

    async function init(productId) {
        const container = document.getElementById('productContent');
        if (currentId === productId) return;
        currentId = productId;

        loader.show(container);

        try {
            const p = await productsApi.getById(productId);
            currentProduct = p;
            container.innerHTML = renderProduct(p);
            bindGallery(container);
            bindActions(container, p);

            window.addEventListener('cart:change', onCartChange);
        } catch (e) {
            loader.empty(container, 'Ошибка загрузки: ' + e.message);
        }
    }

    function onCartChange() {
        if (!currentProduct) return;
        const container = document.getElementById('productContent');
        const actionsWrap = container.querySelector('.product-detail-actions');
        if (!actionsWrap) return;
        actionsWrap.innerHTML = renderCartBlock(currentProduct);
        bindActions(container, currentProduct);
    }

    function renderProduct(p) {
        const images = p.images && p.images.length > 0
                ? p.images.map(i => i.url)
                : [];

        const gallery = images.length > 0
            ? renderGallery(images, p.name)
            : `<div class="product-detail-gallery-empty">📷</div>`;

        const stock = p.stock > 0
            ? `<span class="product-detail-stock">В наличии · ${p.stock} шт.</span>`
            : `<span class="product-detail-stock out">Нет в наличии</span>`;

        const brand = p.brand
            ? `<span class="product-detail-brand">${capitalize(p.brand)}</span>`
            : '';

        const category = p.categoryName
            ? `<span class="product-detail-category">${p.categoryName}</span>`
            : '';

        const description = p.description
            ? `<div class="product-detail-description">
                    <h2 class="product-detail-section">Описание</h2>
                    <p>${escapeHtml(p.description)}</p>
               </div>`
            : '';

        return `
            <div class="product-detail-gallery">${gallery}</div>

            <div class="product-detail-body">
                <div class="product-detail-meta">
                    ${brand}
                    ${category}
                </div>
                <h1 class="product-detail-name">${escapeHtml(p.name)}</h1>
                <div class="product-detail-price-block">
                    <span class="product-detail-price">${formatPrice(p.price)} ₽</span>
                    ${stock}
                </div>
                ${description}
                <div class="product-detail-actions">
                    ${renderCartBlock(p)}
                </div>
            </div>
        `;
    }

    function renderCartBlock(p) {
        if (p.stock <= 0) {
            return `<button class="product-detail-cart out-of-stock" disabled>Нет в наличии</button>`;
        }

        const qty = cart.getQty(p.id);

        if (qty > 0) {
            return `
                <div class="product-detail-stepper" data-id="${p.id}">
                    <button class="product-detail-step-btn" type="button" data-action="minus">−</button>
                    <span class="product-detail-step-qty">${qty}</span>
                    <button class="product-detail-step-btn" type="button" data-action="plus">+</button>
                </div>
            `;
        }

        return `
            <button class="product-detail-cart" type="button" data-action="add">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                В корзину
            </button>
        `;
    }

    function renderGallery(images, name) {
        if (images.length === 1) {
            return `<div class="product-detail-image-single">
                        <img src="${images[0]}" alt="${escapeHtml(name)}">
                    </div>`;
        }

        const slides = images.map((url, i) => `
            <img class="product-detail-slide ${i === 0 ? 'active' : ''}"
                 src="${url}" alt="${escapeHtml(name)}" data-index="${i}" draggable="false">
        `).join('');

        const dots = images.map((_, i) => `
            <span class="product-detail-dot ${i === 0 ? 'active' : ''}" data-index="${i}"></span>
        `).join('');

        const thumbs = images.map((url, i) => `
            <button class="product-detail-thumb ${i === 0 ? 'active' : ''}" data-index="${i}" type="button">
                <img src="${url}" alt="">
            </button>
        `).join('');

        return `
            <div class="product-detail-carousel" data-current="0" data-total="${images.length}">
                <div class="product-detail-track">${slides}</div>
                <button class="product-detail-arrow prev" type="button">‹</button>
                <button class="product-detail-arrow next" type="button">›</button>
                <div class="product-detail-dots">${dots}</div>
            </div>
            <div class="product-detail-thumbs">${thumbs}</div>
        `;
    }

    function bindGallery(container) {
        const carousel = container.querySelector('.product-detail-carousel');
        if (!carousel) return;

        const track = carousel.querySelector('.product-detail-track');
        const slides = carousel.querySelectorAll('.product-detail-slide');
        const dots = carousel.querySelectorAll('.product-detail-dot');
        const thumbs = container.querySelectorAll('.product-detail-thumb');
        const total = slides.length;
        let current = 0;

        function go(index) {
            if (index < 0) index = total - 1;
            if (index >= total) index = 0;

            current = index;
            track.style.transition = 'transform 0.4s cubic-bezier(0.25, 0.1, 0.25, 1)';
            track.style.transform = `translateX(-${index * 100}%)`;
            slides.forEach((s, i) => s.classList.toggle('active', i === index));
            dots.forEach((d, i) => d.classList.toggle('active', i === index));
            thumbs.forEach((t, i) => t.classList.toggle('active', i === index));
            carousel.dataset.current = index;
        }

        carousel.querySelector('.prev').addEventListener('click', (e) => {
            e.stopPropagation();
            go(current - 1);
        });

        carousel.querySelector('.next').addEventListener('click', (e) => {
            e.stopPropagation();
            go(current + 1);
        });

        dots.forEach(dot => {
            dot.addEventListener('click', (e) => {
                e.stopPropagation();
                go(Number(dot.dataset.index));
            });
        });

        thumbs.forEach(thumb => {
            thumb.addEventListener('click', () => {
                go(Number(thumb.dataset.index));
            });
        });

        let startX = 0, startY = 0, deltaX = 0, dragging = false, isHorizontal = null;

        carousel.addEventListener('touchstart', (e) => {
            if (e.touches.length !== 1) return;
            startX = e.touches[0].clientX;
            startY = e.touches[0].clientY;
            deltaX = 0;
            dragging = true;
            isHorizontal = null;
            track.style.transition = 'none';
        }, { passive: true });

        carousel.addEventListener('touchmove', (e) => {
            if (!dragging || e.touches.length !== 1) return;
            const x = e.touches[0].clientX;
            const y = e.touches[0].clientY;
            const dx = x - startX;
            const dy = y - startY;

            if (isHorizontal === null) {
                if (Math.abs(dx) > 8 || Math.abs(dy) > 8) {
                    isHorizontal = Math.abs(dx) > Math.abs(dy);
                } else return;
            }
            if (!isHorizontal) return;

            deltaX = dx;
            const offset = -current * carousel.offsetWidth + deltaX;
            track.style.transform = `translateX(${offset}px)`;
        }, { passive: true });

        carousel.addEventListener('touchend', () => {
            if (!dragging) return;
            dragging = false;
            if (!isHorizontal) {
                track.style.transition = 'transform 0.4s cubic-bezier(0.25, 0.1, 0.25, 1)';
                track.style.transform = `translateX(-${current * 100}%)`;
                return;
            }
            const threshold = carousel.offsetWidth * 0.2;
            if (deltaX < -threshold) go(current + 1);
            else if (deltaX > threshold) go(current - 1);
            else go(current);
            deltaX = 0;
        });

        carousel.addEventListener('touchcancel', () => {
            dragging = false;
            go(current);
        });
    }

    function bindActions(container, product) {
        const wrap = container.querySelector('.product-detail-actions');
        if (!wrap) return;

        wrap.onclick = (e) => {
            const action = e.target.closest('[data-action]')?.dataset.action;
            if (!action) return;

            if (action === 'add') cart.add(product);
            else if (action === 'plus') cart.increment(product.id);
            else if (action === 'minus') cart.decrement(product.id);
        };
    }

    function formatPrice(v) {
        return Number(v).toLocaleString('ru-RU');
    }

    function capitalize(s) {
        if (!s) return '';
        return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
    }

    function escapeHtml(s) {
        return String(s).replace(/[&<>"']/g, ch => ({
            '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
        }[ch]));
    }

    return { init };
})();