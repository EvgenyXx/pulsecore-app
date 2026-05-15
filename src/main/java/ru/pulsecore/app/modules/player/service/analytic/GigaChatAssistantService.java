package ru.pulsecore.app.modules.player.service.analytic;


import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.modules.player.domain.Player;



import javax.net.ssl.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GigaChatAssistantService {
    private final AgentRouter router;


    public String answer(Player player, String question) {
        return router.route(player, question);
    }
}