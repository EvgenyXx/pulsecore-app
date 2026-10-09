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
            itemsMap[String(it.variantId)] = {
                id: it.id,
                variantId: it.variantId,
                productId: it.productId,
                name: it.name,
                description: it.description || null,
                brand: it.brand,
                image: it.image,
                price: it.price,
                qty: it.quantity,
                stock: it.stock,
                size: it.size,
                color: it.color
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
                itemsMap[String(it.variantId || it.id)] = { ...it };
            });
            serverCart = null;
            emitChange();
        }
    }

    function buildLocalSnapshot() {
        const items = {};
        Object.values(itemsMap).forEach(it => {
            items[String(it.variantId)] = {
                id: it.id,
                variantId: it.variantId,
                productId: it.productId,
                name: it.name,
                description: it.description || null,
                price: it.price,
                brand: it.brand || null,
                image: it.image,
                qty: it.qty,
                size: it.size,
                color: it.color
            };
        });
        return items;
    }

    function getAll() { return itemsMap; }

    function getCount() {
        return Object.values(itemsMap).reduce((sum, i) => sum + i.qty, 0);
    }

    function getQty(variantId) {
        const it = itemsMap[String(variantId)];
        return it ? it.qty : 0;
    }

    function getTotalPrice() {
        return Object.values(itemsMap).reduce((sum, i) => sum + i.price * i.qty, 0);
    }

    async function add(variantId, productInfo, quantity = 1) {
        if (serverCart) {
            const updated = await window.CartApi.addItem(variantId, quantity);
            fromServer(updated);
            writeLocal({ items: buildLocalSnapshot() });
            return;
        }

        const key = String(variantId);
        if (itemsMap[key]) {
            itemsMap[key].qty += quantity;
        } else {
            const image = productInfo.mainImageUrl
                || (productInfo.images && productInfo.images[0]
                    && (productInfo.images[0].url || productInfo.images[0]))
                || null;
            itemsMap[key] = {
                variantId: variantId,
                productId: productInfo.productId || productInfo.id,
                name: productInfo.name,
                description: productInfo.description || null,
                price: productInfo.price,
                brand: productInfo.brand || null,
                image: image,
                qty: quantity,
                stock: productInfo.stock,
                size: productInfo.size || null,
                color: productInfo.color || null
            };
        }
        writeLocal({ items: buildLocalSnapshot() });
        emitChange();
    }

    async function increment(variantId) {
        const key = String(variantId);
        const it = itemsMap[key];
        if (!it) return;

        if (serverCart && it.id) {
            await window.CartApi.updateItem(it.id, it.qty + 1);
        }

        it.qty += 1;
        writeLocal({ items: buildLocalSnapshot() });
        emitChange();
    }

    async function decrement(variantId) {
        const key = String(variantId);
        const it = itemsMap[key];
        if (!it) return;

        const nextQty = it.qty - 1;

        if (serverCart && it.id) {
            if (nextQty <= 0) {
                await window.CartApi.removeItem(it.id);
            } else {
                await window.CartApi.updateItem(it.id, nextQty);
            }
        }

        if (nextQty <= 0) delete itemsMap[key];
        else it.qty = nextQty;
        writeLocal({ items: buildLocalSnapshot() });
        emitChange();
    }

    async function remove(variantId) {
        const key = String(variantId);
        const it = itemsMap[key];
        if (!it) return;

        if (serverCart && it.id) {
            await window.CartApi.removeItem(it.id);
        }

        delete itemsMap[key];
        writeLocal({ items: buildLocalSnapshot() });
        emitChange();
    }

    async function clear() {
        if (serverCart) {
            await window.CartApi.clear();
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