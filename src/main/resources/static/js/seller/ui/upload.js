window.Upload = (function () {
    let files = [];
    let gridRef = null;

    function init(inputId, gridId) {
        const input = document.getElementById(inputId);
        const grid = document.getElementById(gridId);
        if (!input || !grid) return;

        gridRef = grid;

        if (!input.dataset.uploadBound) {
            input.dataset.uploadBound = '1';
            input.addEventListener('change', (e) => {
                Array.from(e.target.files).forEach(f => {
                    files.push({
                        file: f,
                        previewUrl: URL.createObjectURL(f),
                        uploadedUrl: null,
                        imageId: null
                    });
                });
                e.target.value = '';
                render(gridRef);
            });
        }

        files = [];
        render(gridRef);
    }

    function render(grid) {
        if (!grid) return;
        grid.innerHTML = '';
        files.forEach((item, index) => {
            const div = document.createElement('div');
            div.className = 'preview-item';
            div.innerHTML = `
                <img src="${item.previewUrl}" alt="">
                <button type="button" class="preview-remove">×</button>
            `;
            div.querySelector('.preview-remove').addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                remove(index);
            });
            grid.appendChild(div);
        });
    }

    async function remove(index) {
        const item = files[index];
        if (!item) return;

        // Оптимистично убираем из UI
        files.splice(index, 1);
        render(gridRef);

        // Удаляем с бэка
        if (item.imageId) {
            try {
                await window.ProductsApi.deleteImage(item.imageId);
            } catch (e) {
                console.warn('Не удалось удалить фото:', e);
                files.splice(index, 0, item);
                render(gridRef);
            }
        }

        if (item.previewUrl && item.previewUrl.startsWith('blob:')) {
            URL.revokeObjectURL(item.previewUrl);
        }
    }

    function getFiles() {
        return files;
    }

    function setFiles(list) {
        files = list.map(item => ({
            file: null,
            previewUrl: item.url,
            uploadedUrl: item.url,
            imageId: item.id
        }));
        render(gridRef);
    }

    function reset(grid) {
        files.forEach(f => {
            if (f.previewUrl && f.previewUrl.startsWith('blob:')) {
                URL.revokeObjectURL(f.previewUrl);
            }
        });
        files = [];
        if (grid) {
            gridRef = grid;
            render(gridRef);
        }
    }

    return { init, getFiles, setFiles, reset };
})();