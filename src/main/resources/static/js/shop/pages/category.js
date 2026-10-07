window.CategoryPage = (function () {
    const productsApi = window.ProductsApi;
    const categoriesApi = window.CategoriesApi;
    const loader = window.Loader;
    const cart = window.CartStore;

    const PAGE_SIZE = window.innerWidth < 768 ? 10 : 20;

    let currentId = null;
    let currentPage = 0;
    let hasMore = false;
    let allProducts = [];

    async function init(categoryId) {
        const title = document.getElementById('categoryTitle');
        const grid = document.getElementById('categoryProductsGrid');

        const isNew = currentId !== categoryId;

        if (isNew) {
            currentId = categoryId;
            currentPage = 0;
            hasMore = false;
            allProducts = [];
            title.textContent = 'Загрузка...';
            loader.show(grid);
        }

        try {
            const [categories, response] = await Promise.all([
                categoriesApi.getAll(),
                productsApi.getByCategory(categoryId, 0, PAGE_SIZE)
            ]);

            const cat = categories.find(c => String(c.id) === String(categoryId));
            title.textContent = cat ? cat.name : 'Категория';

            allProducts = response.content || [];
            currentPage = 0;
            hasMore = !response.last;

            render(grid);
        } catch (e) {
            loader.empty(grid, 'Ошибка загрузки: ' + e.message);
        }
    }

    async function loadNextPage() {
        const response = await productsApi.getByCategory(currentId, currentPage + 1, PAGE_SIZE);
        const content = response.content || [];
        allProducts = allProducts.concat(content);
        currentPage = currentPage + 1;
        hasMore = !response.last;
        render(document.getElementById('categoryProductsGrid'));
    }

    function render(grid) {
        if (allProducts.length === 0) {
            loader.empty(grid, 'В этой категории пока нет товаров');
            return;
        }

        const loadMoreHtml = hasMore
            ? `<button type="button" id="categoryLoadMore" class="load-more-btn">Показать ещё</button>`
            : '';

        grid.innerHTML = allProducts.map(renderCard).join('') + loadMoreHtml;

        grid.querySelectorAll('.product-card').forEach(card => {
            card.addEventListener('click', (e) => {
                if (e.target.closest('.carousel-arrow')
                    || e.target.closest('.carousel-dot')
                    || e.target.closest('.product-card-cart')) return;
                window.location.hash = '#/product/' + card.dataset.id;
            });
        });

        bindCartButtons(grid);
        bindLoadMore();
        bindCarouselEvents();
    }

    function bindLoadMore() {
        const btn = document.getElementById('categoryLoadMore');
        if (!btn) return;
        btn.addEventListener('click', async () => {
            btn.disabled = true;
            btn.textContent = 'Загрузка...';
            try {
                await loadNextPage();
            } catch (e) {
                btn.disabled = false;
                btn.textContent = 'Ошибка, попробуйте ещё раз';
            }
        });
    }

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
        const stock = inStock
                ? `<span class="product-card-stock">В наличии · ${p.stock} шт.</span>`
                : `<span class="product-card-stock out">Нет в наличии</span>`;

        const brand = p.brand
                ? `<span class="product-card-brand">${capitalize(p.brand)}</span>`
                : '';

        const cartBlock = renderCartBlock(p);

        return `
            <div class="product-card ${inStock ? '' : 'out-of-stock'}" data-id="${p.id}">
                <div class="product-card-image">${imageBlock}</div>
                <div class="product-card-body">
                    <span class="product-card-price">${formatPrice(p.price)} ₽</span>
                    ${brand}
                    <span class="product-card-name">${p.name}</span>
                    ${stock}
                </div>
                <div class="product-card-cart">${cartBlock}</div>
            </div>
        `;
    }

    function renderCartBlock(p) {
        if (p.inStock !== true) {
            return `<button class="cart-btn out-of-stock" disabled>Нет в наличии</button>`;
        }

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

        return `<button class="cart-btn" type="button" data-action="add">В корзину</button>`;
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
                const action = e.target.dataset.action;
                if (action === 'add') {
                    cart.add(product).then(() => updateCardCart(wrap, product));
                } else if (action === 'plus') {
                    cart.increment(id).then(() => updateCardCart(wrap, product));
                } else if (action === 'minus') {
                    cart.decrement(id).then(() => updateCardCart(wrap, product));
                }
            });
        });
    }

    function updateCardCart(wrap, product) {
        wrap.innerHTML = renderCartBlock(product);
    }

    function bindCarouselEvents() {
        document.querySelectorAll('#categoryProductsGrid .carousel').forEach(carousel => {
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

    return { init };
})();