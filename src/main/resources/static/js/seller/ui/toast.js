window.Toast = (function () {

    function show(el, text, ok) {
        el.textContent = text;
        el.className = 'message ' + (ok ? 'ok' : 'error');
        el.classList.remove('hidden');
    }

    function hide(el) {
        el.classList.add('hidden');
    }

    return { show, hide };
})();