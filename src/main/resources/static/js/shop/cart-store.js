window.CartStore = (function () {
    const KEY = 'pulsecore_cart';

    let serverCart = null;
    let itemsMap = {};

    function readLocal() {
        try {
            const raw = localStorage.getItem(KEY);
            return raw ? JSON.parse(raw) : { items: {} };
        } catch (e) {
            return { items: {} };
        }
    }

    function writeLocal(data) {
        localStorage.setItem(KEY, JSON.stringify(data));
    }

    function clearLocal() {
        localStorage.removeItem(KEY);
    }

    function emitChange() {
        window.dispatchEvent(new CustomEvent('cart:change', { detail: getCount() }));
    }

    function fromServer(server) {
        serverCart = server;
        itemsMap = {};
        (server.items || []).forEach(it => {
            itemsMap[String(it.productId)] = {
                id: it.id,
                productId: it.productId,
                name: it.name,
                description: it.description || null,
                brand: it.brand,
                image: it.image,
                price: it.price,
                qty: it.quantity,
                stock: it.stock
            };
        });
        emitChange();
    }

    async function init() {
        try {
            const server = await window.CartApi.getCart();
            fromServer(server);
            writeLocal({ items: buildLocalSnapshot() });
        } catch (e) {
            const local = readLocal();
            itemsMap = {};
            Object.values(local.items || {}).forEach(it => {
                itemsMap[String(it.id)] = { ...it, productId: it.id };
            });
            serverCart = null;
            emitChange();
        }
    }

    function buildLocalSnapshot() {
        const items = {};
        Object.values(itemsMap).forEach(it => {
            items[String(it.productId)] = {
                id: it.productId,
                name: it.name,
                description: it.description || null,
                price: it.price,
                brand: it.brand || null,
                image: it.image,
                qty: it.qty
            };
        });
        return items;
    }

    function getAll() {
        return itemsMap;
    }

    function getCount() {
        return Object.values(itemsMap).reduce((sum, i) => sum + i.qty, 0);
    }

    function getQty(productId) {
        const it = itemsMap[String(productId)];
        return it ? it.qty : 0;
    }

    function getTotalPrice() {
        return Object.values(itemsMap)
            .reduce((sum, i) => sum + i.price * i.qty, 0);
    }

    async function add(product) {
        if (serverCart) {
            try {
                const updated = await window.CartApi.addItem(product.id, 1);
                fromServer(updated);
                writeLocal({ items: buildLocalSnapshot() });
                return;
            } catch (e) { console.warn('cart add failed', e); }
        }
        const key = String(product.id);
        if (itemsMap[key]) {
            itemsMap[key].qty += 1;
        } else {
            const image = product.mainImageUrl
                || (product.images && product.images[0] && (product.images[0].url || product.images[0]))
                || null;
            itemsMap[key] = {
                productId: product.id,
                name: product.name,
                description: product.description || null,
                price: product.price,
                brand: product.brand || null,
                image: image,
                qty: 1,
                stock: product.stock
            };
        }
        writeLocal({ items: buildLocalSnapshot() });
        emitChange();
    }

    async function increment(productId) {
        const key = String(productId);
        const it = itemsMap[key];
        if (!it) return;

        if (serverCart && it.id) {
            try {
                await window.CartApi.updateItem(it.id, it.qty + 1);
            } catch (e) { console.warn('cart increment failed', e); return; }
        }

        it.qty += 1;
        writeLocal({ items: buildLocalSnapshot() });
        emitChange();
    }

    async function decrement(productId) {
        const key = String(productId);
        const it = itemsMap[key];
        if (!it) return;

        const nextQty = it.qty - 1;

        if (serverCart && it.id) {
            try {
                if (nextQty <= 0) {
                    await window.CartApi.removeItem(it.id);
                } else {
                    await window.CartApi.updateItem(it.id, nextQty);
                }
            } catch (e) { console.warn('cart decrement failed', e); return; }
        }

        if (nextQty <= 0) delete itemsMap[key];
        else it.qty = nextQty;
        writeLocal({ items: buildLocalSnapshot() });
        emitChange();
    }

    async function remove(productId) {
        const key = String(productId);
        const it = itemsMap[key];
        if (!it) return;

        if (serverCart && it.id) {
            try {
                await window.CartApi.removeItem(it.id);
            } catch (e) { console.warn('cart remove failed', e); return; }
        }

        delete itemsMap[key];
        writeLocal({ items: buildLocalSnapshot() });
        emitChange();
    }

    async function clear() {
        if (serverCart) {
            try {
                await window.CartApi.clear();
            } catch (e) { console.warn('cart clear failed', e); return; }
        }
        itemsMap = {};
        clearLocal();
        emitChange();
    }

    return {
        init,
        getAll, getCount, getQty, getTotalPrice,
        add, increment, decrement, remove, clear
    };
})();