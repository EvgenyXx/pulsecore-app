window.Loader = (function () {

    function show(container, text = 'Загрузка...') {
        container.innerHTML = `<p class="muted">${text}</p>`;
    }

    function empty(container, text = 'Ничего не найдено') {
        container.innerHTML = `<div class="empty-state">${text}</div>`;
    }

    return { show, empty };
})();