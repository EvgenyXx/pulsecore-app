package ru.pulsecore.app.modules.admin.application;

public record BroadcastResult(int totalPlayers, int pushSent, int emailSent) {

    public String toMessage() {
        return String.format(
                "✅ Рассылка завершена. Всего игроков: %d. Push: %d, Email: %d.",
                totalPlayers, pushSent, emailSent
        );
    }
}