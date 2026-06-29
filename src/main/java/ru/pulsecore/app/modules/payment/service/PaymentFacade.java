package ru.pulsecore.app.modules.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.modules.player.api.dto.payment.PaymentResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PlayerService playerService;
    private final YookassaService yookassaService;

    public PaymentResponse createPayment(UUID playerId, int months) {
        Player player = playerService.getById(playerId);
        var payment = yookassaService.createPayment(player.getId(), months);
        return new PaymentResponse(payment.confirmationUrl());
    }
}