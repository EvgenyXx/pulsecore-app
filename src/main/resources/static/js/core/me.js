window.Me = (function () {
    let promise = null;

    function load() {
        if (!promise) {
            promise = fetch('/api/player/me', {credentials: 'same-origin'})
                .then(r => r.ok ? r.json() : null)
                .catch(() => null);
        }
        return promise;
    }

    function invalidate() {
        promise = null;
    }

    function hasRole(me, role) {
        return !!me && Array.isArray(me.roles) && me.roles.includes(role);
    }

    function isAdmin(me) {
        return hasRole(me, 'ROLE_ADMIN');
    }

    function isSeller(me) {
        return hasRole(me, 'ROLE_SELLER');
    }

    function isUser(me) {
        return hasRole(me, 'ROLE_USER');
    }

    return {load, invalidate, hasRole, isAdmin, isSeller, isUser};
})();