async function registerServiceWorker() {
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
        console.log('Push не поддерживается');
        return false;
    }
    try {
        const reg = await navigator.serviceWorker.register('/sw.js');
        console.log('SW зарегистрирован');
        return reg;
    } catch(e) {
        console.error('Ошибка регистрации SW:', e);
        return false;
    }
}

function urlB64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
}

async function subscribeToPush(reg) {
    try {
        const vapidPublicKey = await fetch('/api/push/vapid-public-key').then(r => r.text());
        const subscription = await reg.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: urlB64ToUint8Array(vapidPublicKey)
        });

        await fetch('/api/push/subscribe', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                endpoint: subscription.endpoint,
                p256dh: btoa(String.fromCharCode(...new Uint8Array(subscription.getKey('p256dh')))),
                auth: btoa(String.fromCharCode(...new Uint8Array(subscription.getKey('auth'))))
            })
        });
        return true;
    } catch(e) {
        console.error('Ошибка подписки:', e);
        return false;
    }
}

async function initPushNotifications() {
    const permission = await Notification.requestPermission();
    if (permission !== 'granted') {
        console.log('Уведомления не разрешены');
        return false;
    }
    const reg = await registerServiceWorker();
    if (!reg) return false;
    return await subscribeToPush(reg);
}