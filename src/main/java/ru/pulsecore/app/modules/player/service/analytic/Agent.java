package ru.pulsecore.app.modules.player.service.analytic;

import ru.pulsecore.app.modules.player.domain.Player;

public interface Agent {
    boolean canHandle(String question);
    String answer(Player player, String question);
}