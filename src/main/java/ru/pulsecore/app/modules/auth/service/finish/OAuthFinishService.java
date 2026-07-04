// 5. OAuthFinishService.java
package ru.pulsecore.app.modules.auth.service.finish;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.auth.api.PlayerLoginService;
import ru.pulsecore.app.modules.auth.api.dto.OAuthFinishRequest;
import ru.pulsecore.app.modules.auth.exception.OAuthEmailNotReceivedException;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.service.TournamentAutoAddService;
import ru.pulsecore.app.modules.tournament.service.TournamentCascadeSyncService;

@Service
@RequiredArgsConstructor
public class OAuthFinishService {

    private static final int RECENT_DAYS = 30;

    private final OAuthSessionExtractor sessionExtractor;
    private final OAuthPlayerBuilder playerBuilder;
    private final TrialActivator trialActivator;
    private final OAuthFinishMailer mailer;
    private final TournamentAutoAddService tournamentAutoAddService;
    private final TournamentCascadeSyncService cascadeSyncService;
    private final PlayerLoginService playerLoginService;

    @Transactional
    public void complete(OAuthFinishRequest request, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        var data = sessionExtractor.extract(session);
        if (data.email() == null) {
            throw new OAuthEmailNotReceivedException();
        }

        String name = (request.getLastName() + " " + request.getFirstName()).toLowerCase().trim();
        String email = data.email();


        Player player = playerBuilder.build(name, email, data);
        trialActivator.activate(player);
        mailer.sendWelcome(player);
        tournamentAutoAddService.addRecentTournamentsForPlayer(player, RECENT_DAYS);
        cascadeSyncService.syncAllHistory(player);
        mailer.notifyAdmin(player, httpRequest);
        sessionExtractor.clear(session);
        playerLoginService.login(player, httpRequest);
    }
}