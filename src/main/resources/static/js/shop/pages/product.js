window.ProductPage = (function () {
    const productsApi = window.ProductsApi;
    const loader = window.Loader;
    const cart = window.CartStore;
    const toast = window.Toast;

    let currentId = null;
    let currentProduct = null;
    let selectedSize = null;
    let selectedColor = null;
    let galleryIndex = 0;

    async function init(productId) {
        const container = document.getElementById('productContent');
        if (currentId === productId) return;
        currentId = productId;
        selectedSize = null;
        selectedColor = null;
        galleryIndex = 0;

        loader.show(container);

        try {
            const p = await productsApi.getById(productId);
            currentProduct = p;

            autoSelectFirstAvailable(p);

            container.innerHTML = renderProduct(p);
            bindBackBtn(container);
            bindGallery(container, p);
            bindColorPicker(container, p);
            bindSizePicker(container, p);
            bindActions(container, p);

            window.addEventListener('cart:change', onCartChange);
        } catch (e) {
            loader.empty(container, 'Ошибка загрузки: ' + e.message);
        }
    }

    function autoSelectFirstAvailable(p) {
        const variants = p.variants || [];
        if (variants.length === 0) return;

        const first = variants.find(v => v.stock > 0) || variants[0];
        selectedSize  = first.size  || null;
        selectedColor = first.color || null;
    }

    function onCartChange() {
        if (!currentProduct) return;
        const container = document.getElementById('productContent');
        const actionsWrap = container.querySelector('.product-detail-actions');
        if (!actionsWrap) return;
        actionsWrap.innerHTML = renderCartBlock(currentProduct);
        bindActions(container, currentProduct);
    }

    function getSelectedVariant() {
        if (!currentProduct) return null;
        const variants = currentProduct.variants || [];
        if (variants.length === 0) return null;

        if (selectedSize == null && selectedColor == null && variants.length === 1) {
            return variants[0];
        }

        return variants.find(v =>
            (v.size  || null) === selectedSize &&
            (v.color || null) === selectedColor
        ) || null;
    }

    function getImagesForColor(p, color) {
        const colors = p.colors || [];
        if (color) {
            const c = colors.find(x => x.color === color);
            if (c && c.images) return c.images.map(i => i.url);
        }
        const images = [];
        colors.forEach(c => (c.images || []).forEach(img => images.push(img.url)));
        return images;
    }

    function renderProduct(p) {
        const brand = p.brand
            ? `<span class="product-detail-brand">${capitalize(p.brand)}</span>` : '';

        const category = p.categoryName
            ? `<span class="product-detail-category">${p.categoryName}</span>` : '';

        const description = p.description
            ? `<div class="product-detail-description">
                    <h2 class="product-detail-section">Описание</h2>
                    <p>${escapeHtml(p.description)}</p>
               </div>` : '';

        return `
            <div class="product-detail-gallery" id="productGallery">
                <button type="button" class="product-detail-back" id="productBackBtn" aria-label="Назад">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="15 18 9 12 15 6"/>
                    </svg>
                </button>
                ${renderGalleryBlock(p)}
            </div>
            <div class="product-detail-body">
                <div class="product-detail-price-block">
                    <span class="product-detail-price" id="productPrice"></span>
                    <span class="product-detail-stock" id="productStock"></span>
                </div>
                ${renderColorStrip(p)}
                ${renderSizeGrid(p)}
                <h1 class="product-detail-name">${escapeHtml(p.name)}</h1>
                <div class="product-detail-meta">${brand}${category}</div>
                ${description}
                <div class="product-detail-actions">
                    ${renderCartBlock(p)}
                </div>
            </div>
        `;
    }

    function bindBackBtn(container) {
        const btn = container.querySelector('#productBackBtn');
        if (!btn) return;

        btn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();

            const backTo = sessionStorage.getItem('productBackTo') || '#/';
            window.location.hash = backTo;
        });
    }

    function renderColorStrip(p) {
        const colors = (p.colors || []).filter(c => c.color);
        if (colors.length === 0) return '';

        const items = colors.map(c => {
            const img = (c.images || [])[0];
            const url = img ? img.url : '';
            const isActive = c.color === selectedColor;
            return `
                <button type="button"
                        class="color-thumb ${isActive ? 'active' : ''}"
                        data-color="${escapeHtml(c.color)}">
                    ${url
                        ? `<img src="${url}" alt="${escapeHtml(c.color)}" loading="lazy">`
                        : `<span class="color-thumb-empty">📷</span>`}
                </button>
            `;
        }).join('');

        const label = selectedColor ? escapeHtml(selectedColor) : '';

        return `
            <div class="color-strip-wrap">
                <div class="color-strip">${items}</div>
                ${label ? `<div class="color-strip-label">${label}</div>` : ''}
            </div>
        `;
    }

    function bindColorPicker(container, product) {
        const strip = container.querySelector('.color-strip');
        if (!strip) return;

        strip.onclick = (e) => {
            const btn = e.target.closest('.color-thumb');
            if (!btn) return;

            selectedColor = btn.dataset.color;
            galleryIndex = 0;

            refreshColors(container, product);
            refreshGallery(container, product);
            refreshSizeGrid(container, product);
            refreshPriceStock(container, product);
            refreshCartBtn(container, product);
        };
    }

    function refreshColors(container, product) {
        const wrap = container.querySelector('.color-strip-wrap');
        if (!wrap) return;
        wrap.outerHTML = renderColorStrip(product);

        const newWrap = container.querySelector('.color-strip-wrap');
        if (newWrap) {
            const strip = newWrap.querySelector('.color-strip');
            if (strip) {
                strip.onclick = (e) => {
                    const btn = e.target.closest('.color-thumb');
                    if (!btn) return;
                    selectedColor = btn.dataset.color;
                    galleryIndex = 0;
                    refreshColors(container, product);
                    refreshGallery(container, product);
                    refreshSizeGrid(container, product);
                    refreshPriceStock(container, product);
                    refreshCartBtn(container, product);
                };
            }
        }
    }

    function renderSizeGrid(p) {
        const variants = p.variants || [];
        const sizes = [...new Set(variants.map(v => v.size).filter(Boolean))];
        if (sizes.length === 0) return '';

        const items = sizes.map(s => {
            const available = isSizeAvailable(s);
            const isActive = s === selectedSize;
            return `
                <button type="button"
                        class="size-tile ${isActive ? 'active' : ''} ${!available ? 'disabled' : ''}"
                        data-size="${escapeHtml(s)}"
                        ${!available ? 'disabled' : ''}>
                    ${escapeHtml(s)}
                </button>
            `;
        }).join('');

        return `
            <div class="size-grid-wrap">
                <div class="size-grid-label">Размер</div>
                <div class="size-grid">${items}</div>
            </div>
        `;
    }

    function bindSizePicker(container, product) {
        const grid = container.querySelector('.size-grid');
        if (!grid) return;

        grid.onclick = (e) => {
            const btn = e.target.closest('.size-tile');
            if (!btn || btn.disabled) return;

            const value = btn.dataset.size;
            selectedSize = (value === selectedSize) ? null : value;

            refreshSizeGrid(container, product);
            refreshPriceStock(container, product);
            refreshCartBtn(container, product);
        };
    }

    function refreshSizeGrid(container, product) {
        const wrap = container.querySelector('.size-grid-wrap');
        if (!wrap) return;
        wrap.outerHTML = renderSizeGrid(product);

        const newGrid = container.querySelector('.size-grid');
        if (newGrid) {
            newGrid.onclick = (e) => {
                const btn = e.target.closest('.size-tile');
                if (!btn || btn.disabled) return;
                const value = btn.dataset.size;
                selectedSize = (value === selectedSize) ? null : value;
                refreshSizeGrid(container, product);
                refreshPriceStock(container, product);
                refreshCartBtn(container, product);
            };
        }
    }

    function isSizeAvailable(size) {
        const variants = currentProduct?.variants || [];
        return variants.some(v =>
            (v.size || null) === size
            && (selectedColor == null || (v.color || null) === selectedColor)
            && v.stock > 0);
    }

    function renderGalleryBlock(p) {
        const images = getImagesForColor(p, selectedColor);
        if (images.length === 0) return `<div class="product-detail-gallery-empty">📷</div>`;
        return renderGallery(images, p.name);
    }

    function refreshGallery(container, product) {
        const wrap = container.querySelector('.product-detail-gallery');
        if (!wrap) return;

        // Сохраняем кнопку «Назад», перерисовываем только галерею
        const backBtn = wrap.querySelector('#productBackBtn');
        wrap.innerHTML = renderGalleryBlock(product);
        if (backBtn) wrap.prepend(backBtn);

        bindGallery(container, product);
    }

    function renderGallery(images, name) {
        if (images.length === 1) {
            return `<div class="product-detail-image-single"><img src="${images[0]}" alt="${escapeHtml(name)}"></div>`;
        }

        const slides = images.map((url, i) => `
            <img class="product-detail-slide ${i === 0 ? 'active' : ''}"
                 src="${url}" alt="${escapeHtml(name)}" data-index="${i}" draggable="false">
        `).join('');

        const dots = images.map((_, i) => `
            <span class="product-detail-dot ${i === 0 ? 'active' : ''}" data-index="${i}"></span>
        `).join('');

        return `
            <div class="product-detail-carousel" data-current="0" data-total="${images.length}">
                <div class="product-detail-track">${slides}</div>
                <button class="product-detail-arrow prev" type="button">‹</button>
                <button class="product-detail-arrow next" type="button">›</button>
                <div class="product-detail-dots">${dots}</div>
            </div>
        `;
    }

    function bindGallery(container, product) {
        const carousel = container.querySelector('.product-detail-carousel');
        if (!carousel) return;

        const track = carousel.querySelector('.product-detail-track');
        const slides = carousel.querySelectorAll('.product-detail-slide');
        const dots = carousel.querySelectorAll('.product-detail-dot');
        const total = slides.length;
        let current = 0;

        function go(index) {
            if (index < 0) index = total - 1;
            if (index >= total) index = 0;
            current = index;
            galleryIndex = index;
            track.style.transition = 'transform 0.4s cubic-bezier(0.25, 0.1, 0.25, 1)';
            track.style.transform = `translateX(-${index * 100}%)`;
            slides.forEach((s, i) => s.classList.toggle('active', i === index));
            dots.forEach((d, i) => d.classList.toggle('active', i === index));
            carousel.dataset.current = index;
        }

        const prevBtn = carousel.querySelector('.prev');
        const nextBtn = carousel.querySelector('.next');
        if (prevBtn) prevBtn.addEventListener('click', (e) => { e.stopPropagation(); go(current - 1); });
        if (nextBtn) nextBtn.addEventListener('click', (e) => { e.stopPropagation(); go(current + 1); });
        dots.forEach(dot => dot.addEventListener('click', (e) => { e.stopPropagation(); go(Number(dot.dataset.index)); }));

        let startX = 0, startY = 0, deltaX = 0, dragging = false, isHorizontal = null;

        carousel.addEventListener('touchstart', (e) => {
            if (e.touches.length !== 1) return;
            startX = e.touches[0].clientX;
            startY = e.touches[0].clientY;
            deltaX = 0; dragging = true; isHorizontal = null;
            track.style.transition = 'none';
        }, { passive: true });

        carousel.addEventListener('touchmove', (e) => {
            if (!dragging || e.touches.length !== 1) return;
            const x = e.touches[0].clientX;
            const y = e.touches[0].clientY;
            const dx = x - startX;
            const dy = y - startY;

            if (isHorizontal === null) {
                if (Math.abs(dx) > 8 || Math.abs(dy) > 8) isHorizontal = Math.abs(dx) > Math.abs(dy);
                else return;
            }
            if (!isHorizontal) return;

            deltaX = dx;
            track.style.transform = `translateX(${-current * carousel.offsetWidth + deltaX}px)`;
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

        carousel.addEventListener('touchcancel', () => { dragging = false; go(current); });
    }

    function refreshPriceStock(container, product) {
        const variant = getSelectedVariant();
        const priceEl = container.querySelector('#productPrice');
        const stockEl = container.querySelector('#productStock');
        if (!priceEl || !stockEl) return;

        if (variant) {
            const price = Number(product.price) + Number(variant.priceDelta || 0);
            priceEl.textContent = formatPrice(price) + ' ₽';
            if (variant.stock > 0) {
                stockEl.textContent = 'Осталось ' + variant.stock + ' шт';
                stockEl.className = 'product-detail-stock';
            } else {
                stockEl.textContent = 'Нет в наличии';
                stockEl.className = 'product-detail-stock out';
            }
        } else {
            priceEl.textContent = formatPrice(product.price) + ' ₽';
            stockEl.textContent = product.inStock === true ? 'В наличии' : 'Нет в наличии';
            stockEl.className = 'product-detail-stock' + (product.inStock === true ? '' : ' out');
        }
    }

    function renderCartBlock(p) {
        const variants = p.variants || [];
        const variant = getSelectedVariant();

        const hasVariants = variants.length > 0;
        const available = hasVariants
            ? (variant && variant.stock > 0)
            : (p.inStock === true);

        if (!available) {
            return `<button class="product-detail-cart out-of-stock" disabled>Нет в наличии</button>`;
        }

        const key = hasVariants ? variant.id : p.id;
        const qty = cart.getQty(key);

        if (qty > 0) {
            return `
                <button class="product-detail-cart in-cart" type="button" data-action="goto-cart">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                    В корзине · ${qty} шт
                </button>
            `;
        }

        return `
            <button class="product-detail-cart" type="button" data-action="add" data-variant="${hasVariants ? 1 : 0}">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                В корзину
            </button>
        `;
    }

    function refreshCartBtn(container, product) {
        const wrap = container.querySelector('.product-detail-actions');
        if (!wrap) return;
        wrap.innerHTML = renderCartBlock(product);
        bindActions(container, product);
    }

    function bindActions(container, product) {
        const wrap = container.querySelector('.product-detail-actions');
        if (!wrap) return;

        wrap.onclick = async (e) => {
            const btn = e.target.closest('[data-action]');
            if (!btn || btn.disabled) return;
            const action = btn.dataset.action;
            const hasVariant = btn.dataset.variant === '1';

            try {
                if (action === 'goto-cart') {
                    window.location.hash = '#/cart';
                    return;
                }
                if (action === 'add') {
                    if (hasVariant) {
                        const variant = getSelectedVariant();
                        if (!variant) return;
                        const price = Number(product.price) + Number(variant.priceDelta || 0);
                        const colorObj = (product.colors || []).find(c => c.color === variant.color);
                        const mainImg = colorObj && colorObj.images && colorObj.images.length > 0
                            ? colorObj.images[0].url : null;
                        await cart.add(variant.id, {
                            productId: product.id,
                            name: product.name,
                            description: product.description,
                            price: price,
                            brand: product.brand,
                            mainImageUrl: mainImg,
                            images: colorObj ? colorObj.images : [],
                            stock: variant.stock,
                            size: variant.size,
                            color: variant.color
                        });
                    } else {
                        await cart.add(product.id, {
                            productId: product.id,
                            name: product.name,
                            description: product.description,
                            price: product.price,
                            brand: product.brand,
                            mainImageUrl: product.mainImageUrl,
                            images: product.images,
                            stock: product.stock
                        });
                    }
                }
            } catch (err) {
                toast.showPopup(err.message || 'Ошибка', false);
            }
        };
    }

    function formatPrice(v) { return Number(v).toLocaleString('ru-RU'); }
    function capitalize(s) { return s ? s.charAt(0).toUpperCase() + s.slice(1).toLowerCase() : ''; }
    function escapeHtml(s) {
        return String(s).replace(/[&<>"']/g, ch => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[ch]));
    }

    return { init };
})();