window.ProfilePage = (function () {

    function init() {
        document.querySelectorAll('[data-profile-action="orders"]').forEach(el => {
            el.addEventListener('click', (e) => {
                e.preventDefault();
                window.location.hash = '#/orders';
            });
        });
    }

    return { init };
})();