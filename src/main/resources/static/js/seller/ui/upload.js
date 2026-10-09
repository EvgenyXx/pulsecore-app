window.Upload = (function () {
    // filesByVariant: { [key]: [ { file, previewUrl, uploadedUrl, imageId } ] }
    let filesByVariant = {};
    // cells: { [key]: { cellEl, inputEl } }
    const cells = {};

    /* ================================================================
       attachCell(key, cellEl)
       Регистрирует ячейку таблицы как «контейнер фото» для варианта key.
       Внутри ячейки должен быть:
         <div class="vt-photo-thumbs"></div>
         <button class="vt-photo-add" type="button">＋</button>
         <input class="vt-photo-input" type="file" multiple accept="image/*" hidden>
       Если разметка отсутствует — создаём её сами.
       ================================================================ */
    function attachCell(key, cellEl) {
        if (!key || !cellEl) return;

        cellEl.innerHTML = `
            <div class="vt-photo-thumbs"></div>
            <button class="vt-photo-add" type="button" aria-label="Добавить фото">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="2"
                     stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
            </button>
            <input class="vt-photo-input" type="file" accept="image/*" multiple hidden>
        `;

        const inputEl = cellEl.querySelector('.vt-photo-input');
        const addBtn  = cellEl.querySelector('.vt-photo-add');

        addBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            inputEl.click();
        });

        inputEl.addEventListener('change', (e) => {
            const files = Array.from(e.target.files || []);
            if (files.length) addFiles(key, files);
            e.target.value = '';
        });

        if (!filesByVariant[key]) filesByVariant[key] = [];

        cells[key] = { cellEl, inputEl };
        renderCell(key);
    }

    /* Отвязать ячейку (например при удалении строки) */
    function detachCell(key) {
        delete cells[key];
    }

    /* Полный сброс DOM-ячейки */
    function clearCell(key) {
        const cell = cells[key];
        if (!cell) return;
        const thumbs = cell.cellEl.querySelector('.vt-photo-thumbs');
        if (thumbs) thumbs.innerHTML = '';
    }

    /* ================================================================
       Files API
       ================================================================ */

    function getFiles(key) {
        return filesByVariant[key] || [];
    }

    function getAll() {
        return filesByVariant;
    }

    function addFiles(key, files) {
        if (!filesByVariant[key]) filesByVariant[key] = [];
        files.forEach(f => {
            filesByVariant[key].push({
                file: f,
                previewUrl: URL.createObjectURL(f),
                uploadedUrl: null,
                imageId: null
            });
        });
        renderCell(key);
    }

    async function removeFile(key, index) {
        const arr = filesByVariant[key] || [];
        const item = arr[index];
        if (!item) return;

        arr.splice(index, 1);
        renderCell(key);

        if (item.imageId) {
            try {
                await window.ProductsApi.deleteImage(item.imageId);
            } catch (e) {
                console.warn('Не удалось удалить фото:', e);
                // возвращаем обратно
                arr.splice(index, 0, item);
                renderCell(key);
                return;
            }
        }

        if (item.previewUrl && item.previewUrl.startsWith('blob:')) {
            URL.revokeObjectURL(item.previewUrl);
        }
    }

    function setFiles(key, list) {
        filesByVariant[key] = (list || []).map(item => ({
            file: null,
            previewUrl: item.url,
            uploadedUrl: item.url,
            imageId: item.id
        }));
        renderCell(key);
    }

    function setAll(map) {
        filesByVariant = map || {};
        // перерисовываем все уже привязанные ячейки
        Object.keys(cells).forEach(key => renderCell(key));
    }

    function reset() {
        Object.values(filesByVariant).forEach(arr => {
            arr.forEach(f => {
                if (f.previewUrl && f.previewUrl.startsWith('blob:')) {
                    URL.revokeObjectURL(f.previewUrl);
                }
            });
        });
        filesByVariant = {};
        // чистим DOM у всех зарегистрированных ячеек
        Object.keys(cells).forEach(key => clearCell(key));
    }

    /* ================================================================
       Render одной ячейки
       ================================================================ */
    function renderCell(key) {
        const cell = cells[key];
        if (!cell) return;

        const thumbs = cell.cellEl.querySelector('.vt-photo-thumbs');
        if (!thumbs) return;

        const arr = filesByVariant[key] || [];

        thumbs.innerHTML = arr.map((item, i) => `
            <span class="vt-photo-thumb" title="Удалить">
                <img src="${item.previewUrl}" alt="">
                <button type="button" class="vt-photo-remove" data-index="${i}" aria-label="Удалить">×</button>
            </span>
        `).join('');

        thumbs.querySelectorAll('.vt-photo-remove').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                removeFile(key, Number(btn.dataset.index));
            });
        });
    }

    return {
        attachCell,
        detachCell,
        getFiles,
        getAll,
        addFiles,
        removeFile,
        setFiles,
        setAll,
        reset
    };
})();