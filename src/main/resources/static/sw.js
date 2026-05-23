self.addEventListener('push', event => {
    let data = {};
    try {
        data = event.data.json();
    } catch (e) {
        data = { title: 'PulseCore', body: 'Новое уведомление' };
    }

    const options = {
        body: data.body || '',
        icon: '/img.png',
        badge: '/img.png',
        vibrate: [200, 100, 200, 100, 200],
        tag: data.tag || 'tournament',
        renotify: true,
        requireInteraction: true,
        silent: false,
        data: {
            url: data.url || '/dashboard'
        }
    };

    event.waitUntil(self.registration.showNotification(data.title, options));
});

self.addEventListener('notificationclick', event => {
    event.notification.close();
    event.waitUntil(clients.openWindow(event.notification.data.url));
});