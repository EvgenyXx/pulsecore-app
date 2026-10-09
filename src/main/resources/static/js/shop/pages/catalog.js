window.CatalogPage = (function () {
    const productsApi = window.ProductsApi;
    const loader = window.Loader;
    const cart = window.CartStore;

    const PAGE_SIZE = window.innerWidth < 768 ? 10 : 20;
    const SEARCH_DEBOUNCE_MS = 300;

    let allProducts = [];
    let currentPage = 0;
    let hasMore = false;
    let searchQuery = '';
    let searchTimer = null;

    async function init() {
        const grid = document.getElementById('productsGrid');
        loader.show(grid);

        try {
            await loadFirstPage();
            bindSearch();
            bindLoadMore();
            bindCarouselEvents();

            window.addEventListener('cart:change', updateAllSteppers);
        } catch (e) {
            loader.empty(grid, 'Ошибка загрузки: ' + e.message);
        }
    }

    async function loadFirstPage() {
        const response = await productsApi.getAll(0, PAGE_SIZE);
        allProducts = response.content || [];
        currentPage = 0;
        hasMore = !response.last;
        renderProducts();
    }

    async function loadNextPage() {
        const response = await productsApi.getAll(currentPage + 1, PAGE_SIZE);
        const content = response.content || [];
        allProducts = allProducts.concat(content);
        currentPage = currentPage + 1;
        hasMore = !response.last;
        renderProducts();
    }

    async function loadSearchPage(query) {
        const grid = document.getElementById('productsGrid');
        loader.show(grid);

        try {
            const response = await productsApi.search(query, 0, PAGE_SIZE);
            allProducts = response.content || [];
            currentPage = 0;
            hasMore = !response.last;
            renderProducts();
        } catch (e) {
            loader.empty(grid, 'Ошибка поиска: ' + e.message);
        }
    }

    async function loadNextSearchPage(query) {
        const response = await productsApi.search(query, currentPage + 1, PAGE_SIZE);
        const content = response.content || [];
        allProducts = allProducts.concat(content);
        currentPage = currentPage + 1;
        hasMore = !response.last;
        renderProducts();
    }

    function bindSearch() {
        const input = document.getElementById('searchInput');
        const clear = document.getElementById('searchClear');
        if (!input) return;

        input.addEventListener('input', () => {
            const value = input.value.trim();
            clear.classList.toggle('hidden', !value);

            if (searchTimer) clearTimeout(searchTimer);

            searchTimer = setTimeout(() => {
                searchQuery = value;

                if (!searchQuery) {
                    loadFirstPage();
                } else {
                    loadSearchPage(searchQuery);
                }
            }, SEARCH_DEBOUNCE_MS);
        });

        clear.addEventListener('click', () => {
            input.value = '';
            searchQuery = '';
            clear.classList.add('hidden');

            if (searchTimer) clearTimeout(searchTimer);
            loadFirstPage();
            input.focus();
        });
    }

    function renderProducts() {
        const grid = document.getElementById('productsGrid');

        const list = allProducts;

        if (list.length === 0) {
            loader.empty(grid, searchQuery ? 'Ничего не найдено' : 'Товаров нет');
            return;
        }

        const loadMoreHtml = hasMore
            ? `<button type="button" id="catalogLoadMore" class="load-more-btn">Показать ещё</button>`
            : '';

        grid.innerHTML = list.map(renderCard).join('') + loadMoreHtml;

        grid.querySelectorAll('.product-card').forEach(card => {
            card.addEventListener('click', (e) => {
                if (e.target.closest('.carousel-arrow')
                    || e.target.closest('.carousel-dot')
                    || e.target.closest('.product-card-cart')) return;
                sessionStorage.setItem('productBackTo', '#/');
                window.location.hash = '#/product/' + card.dataset.id;
            });
        });

        bindCartButtons(grid);
        bindLoadMore();
        bindCarouselEvents();
    }

    function bindLoadMore() {
        const btn = document.getElementById('catalogLoadMore');
        if (!btn) return;

        btn.addEventListener('click', async () => {
            btn.disabled = true;
            btn.textContent = 'Загрузка...';
            try {
                if (searchQuery) {
                    await loadNextSearchPage(searchQuery);
                } else {
                    await loadNextPage();
                }
            } catch (e) {
                btn.disabled = false;
                btn.textContent = 'Ошибка, попробуйте ещё раз';
            }
        });
    }

    // ==================== RENDER CARD ====================

    function renderCard(p) {
        const images = p.images && p.images.length > 0
                ? p.images.map(i => typeof i === 'string' ? i : i.url)
                : (p.mainImageUrl ? [p.mainImageUrl] : []);

        let imageBlock;

        if (images.length === 0) {
            imageBlock = `<div class="product-card-image-placeholder">📷</div>`;
        } else if (images.length === 1) {
            imageBlock = `<img src="${images[0]}" alt="${p.name}" loading="lazy" decoding="async">`;
        } else {
            const slides = images.map((url, i) => `
                <img class="carousel-slide ${i === 0 ? 'active' : ''}"
                     src="${url}" alt="${p.name}" data-index="${i}"
                     draggable="false" loading="lazy" decoding="async">
            `).join('');

            const dots = images.map((_, i) => `
                <span class="carousel-dot ${i === 0 ? 'active' : ''}" data-index="${i}"></span>
            `).join('');

            imageBlock = `
                <div class="carousel" data-current="0" data-total="${images.length}">
                    <div class="carousel-track">${slides}</div>
                    <button class="carousel-arrow prev" type="button">‹</button>
                    <button class="carousel-arrow next" type="button">›</button>
                    <div class="carousel-dots">${dots}</div>
                </div>
            `;
        }

        const inStock = p.inStock === true;
        const badge = inStock
            ? `<span class="product-badge in">В наличии</span>`
            : `<span class="product-badge out">Нет в наличии</span>`;

        const brand = p.brand
                ? `<span class="product-card-brand">${capitalize(p.brand)}</span>`
                : '';

        const cartBlock = renderCartBlock(p);

        return `
            <div class="product-card ${inStock ? '' : 'out-of-stock'}" data-id="${p.id}">
                <div class="product-card-image">
                    ${imageBlock}
                    ${badge}
                </div>
                <div class="product-card-body">
                    <span class="product-card-price">${formatPrice(p.price)} ₽</span>
                    ${brand}
                    <span class="product-card-name">${p.name}</span>
                </div>
                <div class="product-card-cart">${cartBlock}</div>
            </div>
        `;
    }

    function renderCartBlock(p) {
        if (p.inStock !== true) {
            return `<button class="cart-btn out-of-stock" disabled>Нет в наличии</button>`;
        }

        const variants = p.variants || [];

        if (variants.length === 0) {
            const qty = cart.getQty(p.id);
            if (qty > 0) {
                return `
                    <div class="cart-stepper" data-id="${p.id}">
                        <button class="cart-step-btn" type="button" data-action="minus">−</button>
                        <span class="cart-step-qty">${qty}</span>
                        <button class="cart-step-btn" type="button" data-action="plus">+</button>
                    </div>
                `;
            }
            return `<button class="cart-btn" type="button" data-action="add" data-fallback="1">В корзину</button>`;
        }

        if (variants.length === 1) {
            const v = variants[0];
            const qty = cart.getQty(v.id);
            if (qty > 0) {
                return `
                    <div class="cart-stepper" data-id="${v.id}">
                        <button class="cart-step-btn" type="button" data-action="minus">−</button>
                        <span class="cart-step-qty">${qty}</span>
                        <button class="cart-step-btn" type="button" data-action="plus">+</button>
                    </div>
                `;
            }
            return `<button class="cart-btn" type="button" data-action="add" data-variant-id="${v.id}">В корзину</button>`;
        }

        return `<button class="cart-btn" type="button" data-action="choose">Выбрать вариант</button>`;
    }

    function bindCartButtons(grid) {
        grid.querySelectorAll('.product-card').forEach(card => {
            const id = card.dataset.id;
            const wrap = card.querySelector('.product-card-cart');
            if (!wrap) return;
            const product = allProducts.find(p => String(p.id) === String(id));
            if (!product) return;

            wrap.addEventListener('click', (e) => {
                e.stopPropagation();
                const btn = e.target.closest('[data-action]');
                if (!btn) return;
                const action = btn.dataset.action;

                if (action === 'choose') {
                    sessionStorage.setItem('productBackTo', '#/');
                    window.location.hash = '#/product/' + product.id;
                    return;
                }

                const variantId = btn.dataset.variantId
                    ? Number(btn.dataset.variantId)
                    : null;

                if (action === 'add') {
                    if (variantId) {
                        const variant = (product.variants || []).find(v => Number(v.id) === variantId);
                        cart.add(variantId, {
                            productId: product.id,
                            name: product.name,
                            description: product.description,
                            price: variant
                                ? Number(product.price) + Number(variant.priceDelta || 0)
                                : product.price,
                            brand: product.brand,
                            mainImageUrl: product.mainImageUrl,
                            images: product.images,
                            stock: variant ? variant.stock : product.stock,
                            size: variant ? variant.size : null,
                            color: variant ? variant.color : null
                        });
                    } else {
                        cart.add(product.id, {
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
                } else if (action === 'plus') {
                    cart.increment(variantId || product.id);
                } else if (action === 'minus') {
                    cart.decrement(variantId || product.id);
                }
            });
        });
    }

    function updateAllSteppers() {
        document.querySelectorAll('.product-card').forEach(card => {
            const id = card.dataset.id;
            const wrap = card.querySelector('.product-card-cart');
            if (!wrap) return;
            const product = allProducts.find(p => String(p.id) === String(id));
            if (!product) return;
            wrap.innerHTML = renderCartBlock(product);
        });
        updateCartBadge();
    }

    function updateCartBadge() {
        const badge = document.getElementById('cartBadge');
        if (!badge) return;
        const count = cart.getCount();
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.classList.remove('hidden');
        } else {
            badge.classList.add('hidden');
        }
    }

    function bindCarouselEvents() {
        document.querySelectorAll('#productsGrid .carousel').forEach(carousel => {
            bindCarousel(carousel);
        });
    }

    function bindCarousel(carousel) {
        if (carousel.dataset.bound === '1') return;
        carousel.dataset.bound = '1';

        const track = carousel.querySelector('.carousel-track');
        const slides = carousel.querySelectorAll('.carousel-slide');
        const dots = carousel.querySelectorAll('.carousel-dot');
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

    function formatPrice(v) {
        return Number(v).toLocaleString('ru-RU');
    }

    function capitalize(s) {
        if (!s) return '';
        return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
    }

    return { init, updateCartBadge };
})();