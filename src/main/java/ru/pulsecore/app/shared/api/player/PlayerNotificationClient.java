package ru.pulsecore.app.shared.api.player;

import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Определяет контракт взаимодействия с модулем игроков
 * для управления настройками push-уведомлений и получения данных игроков.
 */
public interface PlayerNotificationClient {

    /**
     * Переключает состояние push-уведомлений игрока.
     *
     * @param playerId идентификатор игрока
     * @return новое состояние push-уведомлений: {@code true} — включены,
     *         {@code false} — выключены
     */
    boolean togglePushEnabled(UUID playerId);

    /**
     * Проверяет текущее состояние push-уведомлений игрока.
     *
     * @param playerId идентификатор игрока
     * @return {@code true}, если push-уведомления включены, иначе {@code false}
     */
    boolean isPushEnabled(UUID playerId);

    /**
     * Получает данные игрока по его идентификатору.
     *
     * @param playerId идентификатор игрока
     * @return данные игрока
     */
    PlayerData getPlayer(UUID playerId);

    /**
     * Получает данные игроков по набору идентификаторов.
     *
     * @param playerIds набор идентификаторов игроков
     * @return список данных найденных игроков
     */
    List<PlayerData> getPlayers(Set<UUID> playerIds);
}