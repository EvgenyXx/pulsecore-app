package ru.pulsecore.app.shared.api.player;

/**
 * Административное управление игроками.
 * Используется админ-панелью для операций, недоступных обычному пользователю.
 */
public interface PlayerAdminManagementClient {

    /**
     * Создаёт игрока вручную из админ-панели.
     * Email и пароль генерируются на бэке, аккаунт сразу подтверждён,
     * активируется пробная подписка. Операция атомарна.
     *
     * @param request данные для создания (имя)
     * @throws ru.pulsecore.app.player.infrastructure.exception.PlayerNameAlreadyExistsException если имя не проходит валидацию
     * @throws ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException если почта уже есть в системе
     */
    void createPlayer(CreatePlayerRequest request);
}