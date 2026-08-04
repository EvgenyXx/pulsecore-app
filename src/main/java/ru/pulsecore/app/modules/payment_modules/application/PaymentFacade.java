package ru.pulsecore.app.modules.payment_modules.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.modules.player_modeles.api.dto.response.PaymentResponse;
import ru.pulsecore.app.modules.player_modeles.entity.Player;
import ru.pulsecore.app.modules.player_modeles.application.player.PlayerService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    //todo клиента добавить
    private final PlayerService playerService;
    private final YookassaService yookassaService;

    public PaymentResponse createPayment(UUID playerId, int months) {
        Player player = playerService.getById(playerId);
        var payment = yookassaService.createPayment(player.getId(), months);
        return new PaymentResponse(payment.confirmationUrl());
    }
}