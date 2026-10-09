window.ProductNewPage = (function () {
    const api = window.ProductsApi;
    const categoriesApi = window.CategoriesApi;
    const toast = window.Toast;
    const upload = window.Upload;

    let initialized = false;
    const sizes = [];
    const colors = [];

    // groups: { [colorValue]: [ {id, size, stock, priceDelta} ] }
    // colorValue === '' — «без цвета»
    let groups = {};
    let activeKey = '';
    let rowSeq = 0;

    function init() {
        if (initialized) return;
        initialized = true;

        reloadCategories();

        initTags('sizesTags', 'sizesInput', sizes, onTagsChange);
        initTags('colorsTags', 'colorsInput', colors, onTagsChange);

        document.querySelectorAll('input[name="variantMode"]').forEach(r => {
            r.addEventListener('change', onModeChange);
        });

        const genBtn = document.getElementById('generateVariantsBtn');
        if (genBtn) genBtn.addEventListener('click', generateFromTags);

        const addBtn = document.getElementById('addVariantRowBtn');
        if (addBtn) addBtn.addEventListener('click', addRowToActive);

        const simpleCell = document.getElementById('simplePhotoCell');
        if (simpleCell) upload.attachCell('__default__', simpleCell);

        onModeChange();
    }

    /* ======================= MODE ======================= */

    function getMode() {
        const checked = document.querySelector('input[name="variantMode"]:checked');
        return checked ? checked.value : 'simple';
    }

    function onModeChange() {
        const mode = getMode();
        const simple = document.getElementById('simpleBlock');
        const variants = document.getElementById('variantsBlock');
        if (simple) simple.classList.toggle('hidden', mode !== 'simple');
        if (variants) variants.classList.toggle('hidden', mode !== 'variants');
        renderTabs();
        renderGallery();
        renderVariantsTable();
    }

    function onTagsChange() {
        colors.forEach(c => {
            if (!(c in groups)) groups[c] = [];
        });
        renderTabs();
        renderGallery();
        renderVariantsTable();
    }

    /* ======================= TAGS ======================= */

    function initTags(wrapId, inputId, arr, onChange) {
        const wrap = document.getElementById(wrapId);
        const input = document.getElementById(inputId);
        if (!wrap || !input || !arr) return;

        input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ',') {
                e.preventDefault();
                const value = input.value.trim().replace(/,$/, '');
                if (value && !arr.includes(value)) {
                    arr.push(value);
                    renderTags(wrap, input, arr, onChange);
                }
                input.value = '';
                onChange();
            } else if (e.key === 'Backspace' && input.value === '' && arr.length > 0) {
                arr.pop();
                renderTags(wrap, input, arr, onChange);
                onChange();
            }
        });

        input.addEventListener('blur', () => {
            const value = input.value.trim().replace(/,$/, '');
            if (value && !arr.includes(value)) {
                arr.push(value);
                renderTags(wrap, input, arr, onChange);
                input.value = '';
                onChange();
            }
        });

        renderTags(wrap, input, arr, onChange);
    }

    function renderTags(wrap, input, arr, onChange) {
        wrap.querySelectorAll('.tag').forEach(t => t.remove());
        arr.forEach((value, i) => {
            const tag = document.createElement('span');
            tag.className = 'tag';
            tag.innerHTML = `
                <span>${escapeHtml(value)}</span>
                <button type="button" class="tag-remove">×</button>
            `;
            tag.querySelector('.tag-remove').addEventListener('click', () => {
                arr.splice(i, 1);
                renderTags(wrap, input, arr, onChange);
                onChange();
            });
            wrap.insertBefore(tag, input);
        });
    }

    /* ======================= CATEGORIES ======================= */

    async function reloadCategories() {
        const select = document.getElementById('categoryId');
        if (!select) return;
        try {
            const categories = await categoriesApi.getAll();
            select.innerHTML = '<option value="">— Выберите —</option>';
            categories.forEach(c => {
                const opt = document.createElement('option');
                opt.value = c.id;
                opt.textContent = c.name;
                select.appendChild(opt);
            });
        } catch (e) {
            select.innerHTML = '<option value="">Ошибка загрузки</option>';
        }
    }

    /* ======================= TABS ======================= */

    function visibleColors() {
        return colors.length > 0 ? colors : [''];
    }

    function ensureGroups() {
        const visible = visibleColors();
        visible.forEach(c => {
            if (!(c in groups)) groups[c] = [];
        });
        if (!visible.includes(activeKey)) {
            activeKey = visible[0] || '';
        }
    }

    function renderTabs() {
        ensureGroups();
        const wrap = document.getElementById('vtTabs');
        if (!wrap) return;

        const visible = visibleColors();

        wrap.innerHTML = visible.map(c => {
            const isActive = c === activeKey;
            const rows = groups[c] || [];
            const totalStock = rows.reduce((s, r) => s + (Number(r.stock) || 0), 0);
            const label = c || 'Без цвета';
            const meta = rows.length === 0
                ? 'пусто'
                : `${rows.length} разм. · ${totalStock} шт`;
            return `
                <button type="button" class="vt-tab ${isActive ? 'active' : ''}" data-color="${escapeAttr(c)}">
                    <span class="vt-tab-title">${escapeHtml(label)}</span>
                    <span class="vt-tab-meta">${meta}</span>
                    ${c ? `<span class="vt-tab-remove" data-remove-color="${escapeAttr(c)}">×</span>` : ''}
                </button>
            `;
        }).join('');

        wrap.querySelectorAll('.vt-tab').forEach(btn => {
            btn.addEventListener('click', (e) => {
                if (e.target.closest('.vt-tab-remove')) return;
                activeKey = btn.dataset.color;
                renderTabs();
                renderGallery();
                renderVariantsTable();
            });
        });

        wrap.querySelectorAll('.vt-tab-remove').forEach(x => {
            x.addEventListener('click', (e) => {
                e.stopPropagation();
                confirmRemoveColor(x.dataset.removeColor);
            });
        });
    }

    /* ======================= GALLERY ======================= */

    function renderGallery() {
        const cell = document.getElementById('vtColorGallery');
        if (!cell) return;
        upload.attachCell(activeKey || '', cell);
    }

    function confirmRemoveColor(color) {
        const rows = groups[color] || [];
        const message = rows.length > 0
            ? `Удалить цвет «${color}» и ${rows.length} вариант(ов)?`
            : `Убрать цвет «${color}» из списка?`;

        if (!window.confirm(message)) return;

        const idx = colors.indexOf(color);
        if (idx !== -1) colors.splice(idx, 1);
        renderTags(
            document.getElementById('colorsTags'),
            document.getElementById('colorsInput'),
            colors,
            onTagsChange
        );

        upload.detachCell(color);
        const all = upload.getAll();
        if (all[color]) delete all[color];

        delete groups[color];

        if (activeKey === color) {
            const visible = visibleColors();
            activeKey = visible[0] || '';
        }
        renderTabs();
        renderGallery();
        renderVariantsTable();
    }

    /* ======================= VARIANTS TABLE ======================= */

    function addRow(size = null, colorValue = null, stock = 0, priceDelta = 0) {
        const gk = colorValue || '';
        if (!(gk in groups)) groups[gk] = [];

        if (size && groups[gk].some(v => v.size === size)) return null;

        const row = {
            id: ++rowSeq,
            size: size || null,
            stock: Number(stock) || 0,
            priceDelta: Number(priceDelta) || 0
        };
        groups[gk].push(row);
        return row;
    }

    function addRowToActive() {
        const colorValue = activeKey || null;
        const row = addRow(null, colorValue, 0, 0);
        if (!row) {
            toast.show(document.getElementById('message'), 'Такая строка уже есть', false);
            return;
        }
        renderTabs();
        renderVariantsTable();
    }

    function removeRow(id) {
        const arr = groups[activeKey] || [];
        const idx = arr.findIndex(v => v.id === id);
        if (idx === -1) return;
        arr.splice(idx, 1);

        renderTabs();
        renderVariantsTable();
    }

    function generateFromTags() {
        const msg = document.getElementById('message');

        if (sizes.length === 0 && colors.length === 0) {
            toast.show(msg, 'Добавь хотя бы один размер или цвет', false);
            return;
        }

        const useSizes  = sizes.length  > 0 ? sizes  : [null];
        const useColors = colors.length > 0 ? colors : [''];

        let added = 0;
        useColors.forEach(colorKey => {
            const gk = colorKey || '';
            if (!(gk in groups)) groups[gk] = [];
            const existing = new Set(groups[gk].map(v => v.size));

            useSizes.forEach(size => {
                if (existing.has(size)) return;
                groups[gk].push({
                    id: ++rowSeq,
                    size: size || null,
                    stock: 0,
                    priceDelta: 0
                });
                added++;
            });
        });

        if (!activeKey && Object.keys(groups).length) {
            activeKey = Object.keys(groups)[0];
        }

        renderTabs();
        renderGallery();
        renderVariantsTable();

        if (added === 0) {
            toast.show(msg, 'Все комбинации уже есть', false);
        }
    }

    function renderVariantsTable() {
        ensureGroups();

        const tbody = document.getElementById('variantsTbody');
        const empty = document.getElementById('variantsEmpty');
        if (!tbody) return;

        const rows = groups[activeKey] || [];

        if (rows.length === 0) {
            tbody.innerHTML = '';
            if (empty) empty.classList.remove('hidden');
            updateTableColumns();
            return;
        }
        if (empty) empty.classList.add('hidden');

        const sizeCount = {};
        rows.forEach(v => { if (v.size) sizeCount[v.size] = (sizeCount[v.size] || 0) + 1; });

        tbody.innerHTML = rows.map((v, i) => {
            const dup = v.size && sizeCount[v.size] > 1;
            return `
                <tr data-row-id="${v.id}" class="${dup ? 'dup' : ''}">
                    <td class="vt-num">${i + 1}</td>
                    <td class="vt-cell-size">
                        <input class="vt-input" type="text" data-field="size"
                               value="${escapeAttr(v.size || '')}" placeholder="—">
                    </td>
                    <td class="vt-cell-stock">
                        <input class="vt-input vt-input-num" type="number" min="0"
                               data-field="stock" value="${v.stock}">
                    </td>
                    <td class="vt-cell-delta">
                        <input class="vt-input vt-input-num" type="number" step="0.01"
                               data-field="priceDelta" value="${v.priceDelta}">
                    </td>
                    <td class="vt-remove">
                        <button type="button" class="vt-remove-btn" data-action="remove" aria-label="Удалить">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                 stroke="currentColor" stroke-width="2"
                                 stroke-linecap="round" stroke-linejoin="round">
                                <line x1="18" y1="6" x2="6" y2="18"/>
                                <line x1="6" y1="6" x2="18" y2="18"/>
                            </svg>
                        </button>
                    </td>
                </tr>
            `;
        }).join('');

        tbody.querySelectorAll('tr').forEach(tr => {
            const id = Number(tr.dataset.rowId);

            tr.querySelectorAll('input[data-field]').forEach(inp => {
                inp.addEventListener('input', () => {
                    const row = (groups[activeKey] || []).find(v => v.id === id);
                    if (!row) return;
                    const field = inp.dataset.field;
                    if (field === 'stock' || field === 'priceDelta') {
                        row[field] = inp.value === '' ? 0 : Number(inp.value);
                    } else {
                        row[field] = inp.value.trim() || null;
                    }
                });
            });

            tr.querySelector('[data-action="remove"]').addEventListener('click', () => {
                removeRow(id);
            });
        });

        updateTableColumns();
    }

    function updateTableColumns() {
        const table = document.getElementById('variantsTable');
        if (!table) return;

        const allRows = Object.values(groups).flat();
        const hasSize = sizes.length > 0 || allRows.some(v => v.size);

        table.classList.toggle('vt-hide-size', !hasSize);
    }

    /* ======================= CREATE ======================= */

    function parseIntOrZero(v) {
        return v === '' || v == null ? 0 : parseInt(v);
    }

    async function createProduct() {
        const msg = document.getElementById('message');
        const btn = document.getElementById('submitBtn');

        toast.hide(msg);

        const mode = getMode();

        btn.disabled = true;
        btn.textContent = 'Загрузка фото...';

        try {
            // 1. загружаем фото
            const filesByColor = upload.getAll();
            for (const key of Object.keys(filesByColor)) {
                for (const item of filesByColor[key]) {
                    if (item.uploadedUrl) continue;
                    const data = await api.upload(item.file);
                    item.uploadedUrl = data.url;
                }
            }

            btn.textContent = 'Создание...';

            let colorsPayload = null;
            let sizesPayload = null;
            let variantsPayload = [];

            if (mode === 'simple') {
                // фото для «без вариантов» — на ключе '__default__'
                const vFiles = filesByColor['__default__'] || [];
                const stock = parseIntOrZero(document.getElementById('stock').value);

                colorsPayload = [{
                    color: null,
                    sortOrder: 0,
                    images: vFiles.map((f, i) => ({
                        url: f.uploadedUrl, sortOrder: i, main: i === 0
                    }))
                }];

                sizesPayload = null;

                variantsPayload = [{
                    color: null,
                    size: null,
                    stock: stock,
                    priceDelta: 0
                }];
            } else {
                const visible = visibleColors();

                // цвета — из вкладок, фото с upload по ключу цвета
                colorsPayload = visible.map((c, idx) => {
                    const vFiles = filesByColor[c] || [];
                    return {
                        color: c || null,
                        sortOrder: idx,
                        images: vFiles.map((f, i) => ({
                            url: f.uploadedUrl, sortOrder: i, main: i === 0
                        }))
                    };
                });

                // размеры — из чипов
                sizesPayload = sizes.length > 0
                    ? sizes.map((s, idx) => ({ size: s, sortOrder: idx }))
                    : null;

                // варианты — плоский список из всех групп
                Object.keys(groups).forEach(colorValue => {
                    (groups[colorValue] || []).forEach(row => {
                        variantsPayload.push({
                            color: colorValue || null,
                            size: row.size,
                            stock: row.stock,
                            priceDelta: row.priceDelta || 0
                        });
                    });
                });
            }

            const body = {
                name: document.getElementById('name').value.trim(),
                brand: document.getElementById('brand').value.trim() || null,
                description: document.getElementById('description').value.trim() || null,
                price: parseFloat(document.getElementById('price').value),
                categoryId: parseInt(document.getElementById('categoryId').value),
                colors: colorsPayload,
                sizes: sizesPayload,
                variants: variantsPayload
            };

            await api.create(body);
            toast.show(msg, 'Продукт создан', true);
            resetForm();
            setTimeout(() => { window.location.hash = '#/'; }, 600);

        } catch (e) {
            toast.show(msg, e.message, false);
        } finally {
            btn.disabled = false;
            btn.textContent = 'Создать продукт';
        }
    }

    function resetForm() {
        const form = document.getElementById('productForm');
        if (form) form.reset();
        upload.reset();
        sizes.length = 0;
        colors.length = 0;
        groups = {};
        activeKey = '';
        rowSeq = 0;
        document.querySelectorAll('#sizesTags .tag, #colorsTags .tag').forEach(t => t.remove());
        renderTabs();
        renderGallery();
        renderVariantsTable();
        onModeChange();
    }

    /* ======================= UTILS ======================= */

    function escapeHtml(s) {
        return String(s ?? '').replace(/[&<>"']/g, c => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
        })[c]);
    }

    function escapeAttr(s) {
        return escapeHtml(s);
    }

    return { init, createProduct, reloadCategories };
})();