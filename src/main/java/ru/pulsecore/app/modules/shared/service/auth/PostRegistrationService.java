package ru.pulsecore.app.modules.shared.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.domain.Subscription;
import ru.pulsecore.app.modules.player.repository.SubscriptionRepository;
import ru.pulsecore.app.modules.tournament.service.TournamentAutoAddService;
import ru.pulsecore.app.modules.tournament.service.TournamentCascadeSyncService;

@Component
@RequiredArgsConstructor
public class PostRegistrationService {

    private static final int TRIAL_DAYS = 7;
    private static final int RECENT_DAYS = 30;

    private final SubscriptionRepository subscriptionRepository;
    private final TournamentAutoAddService tournamentAutoAddService;
    private final TournamentCascadeSyncService cascadeSyncService;
    private final RegistrationMailService mailService;

    @Transactional
    public void execute(Player player, HttpServletRequest request) {
        createTrial(player);
        addTournaments(player);
        mailService.sendWelcome(player);
        mailService.notifyAdminNewUser(player, request);
    }

    private void createTrial(Player player) {
        Subscription trial = Subscription.builder().player(player).build();
        trial.activate(TRIAL_DAYS);
        subscriptionRepository.save(trial);
    }

    private void addTournaments(Player player) {
        tournamentAutoAddService.addRecentTournamentsForPlayer(player, RECENT_DAYS);
        cascadeSyncService.syncAllHistory(player);
    }
}