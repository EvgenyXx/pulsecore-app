// modules/admin/service/CoreEndpointLabels.java
package ru.pulsecore.app.modules.admin.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoreEndpointLabels implements EndpointLabelProvider {

    @Override
    public List<EndpointLabel> labels() {
        return List.of(
                new EndpointLabel("/api/auth/me", "🔑 Проверка авторизации"),
                new EndpointLabel("/api/auth/login", "🔑 Вход"),
                new EndpointLabel("/api/auth/register", "📝 Регистрация"),
                new EndpointLabel("/api/auth/logout", "🚪 Выход"),
                new EndpointLabel("/api/player/{id}/dashboard", "📊 Дашборд игрока"),
                new EndpointLabel("/api/player/{id}/monthly-income", "📅 Доход за месяц"),
                new EndpointLabel("/api/player/{id}/daily-income", "📅 Доход за день"),
                new EndpointLabel("/api/player/top/WEEK", "🏆 Топ недели (общий)"),
                new EndpointLabel("/api/player/top/WEEK/A", "🏆 Топ лиги A (неделя)"),
                new EndpointLabel("/api/player/top/WEEK/B", "🏆 Топ лиги B (неделя)"),
                new EndpointLabel("/api/player/top/WEEK/C", "🏆 Топ лиги C (неделя)"),
                new EndpointLabel("/api/player/top/WEEK/D", "🏆 Топ лиги D (неделя)"),
                new EndpointLabel("/api/player/top/MONTH", "🏆 Топ месяца (общий)"),
                new EndpointLabel("/api/player/top/MONTH/A", "🏆 Топ лиги A (месяц)"),
                new EndpointLabel("/api/player/top/MONTH/B", "🏆 Топ лиги B (месяц)"),
                new EndpointLabel("/api/player/top/MONTH/C", "🏆 Топ лиги C (месяц)"),
                new EndpointLabel("/api/player/top/MONTH/D", "🏆 Топ лиги D (месяц)"),
                new EndpointLabel("/api/player/top/YEAR", "🏆 Топ года (общий)"),
                new EndpointLabel("/api/player/top/YEAR/A", "🏆 Топ лиги A (год)"),
                new EndpointLabel("/api/player/top/YEAR/B", "🏆 Топ лиги B (год)"),
                new EndpointLabel("/api/player/top/YEAR/C", "🏆 Топ лиги C (год)"),
                new EndpointLabel("/api/player/sum", "💰 Общая сумма"),
                new EndpointLabel("/api/player/halls", "🏛 Список залов"),
                new EndpointLabel("/api/player/subscription", "💳 Проверка подписки"),
                new EndpointLabel("/api/player/analytics", "📈 Аналитика"),
                new EndpointLabel("/api/player/search", "🔍 Поиск игрока"),
                new EndpointLabel("/api/player/best-time", "⏱ Лучшее время"),
                new EndpointLabel("/api/player/notifications/status", "🔔 Статус уведомлений"),
                new EndpointLabel("/api/lineups/all", "📋 Все турниры"),
                new EndpointLabel("/api/lineups/my", "📋 Мои турниры"),
                new EndpointLabel("/api/prices", "💵 Цены"),
                new EndpointLabel("/api/push/push-status", "🔔 Push-статус"),
                new EndpointLabel("/api/player/pay", "💳 Оплата"),
                new EndpointLabel("/api/admin/broadcast", "📢 Админ: рассылка"),
                new EndpointLabel("/api/admin/stats/page-views", "📊 Админ: статистика"),
                new EndpointLabel("/api/admin/players/{id}/subscribe", "👤 Админ: выдача подписки"),
                new EndpointLabel("/api/admin/players/{id}/unsubscribe", "👤 Админ: отключение подписки"),
                new EndpointLabel("/api/admin/players/{id}/roles/grant", "👤 Админ: выдача роли"),
                new EndpointLabel("/api/admin/players/{id}/roles/revoke", "👤 Админ: отзыв роли"),
                new EndpointLabel("/api/admin/players/{id}", "👤 Админ: удаление игрока"),
                new EndpointLabel("/api/admin/players/{id}/tournaments", "👤 Админ: удаление турниров"),
                new EndpointLabel("/api/admin/players/{id}/tournaments/resync", "👤 Админ: перезагрузка истории"),
                new EndpointLabel("/api/admin/prices", "💵 Админ: обновление цен"),
                new EndpointLabel("/api/admin/tournaments/calculate", "🧮 Админ: расчёт"),
                new EndpointLabel("/api/chat/{lineupId}", "💬 Чат трансляции"),
                new EndpointLabel("/api/chat/{lineupId}/online", "👁 Онлайн трансляции"),
                new EndpointLabel("/api/chat/players/search", "🔍 Поиск игроков чата"),
                new EndpointLabel("/api/tournament/live", "📡 Live-трансляции"),
                new EndpointLabel("/api/lineups/live-halls", "🏛 Залы трансляций")
        );
    }
}