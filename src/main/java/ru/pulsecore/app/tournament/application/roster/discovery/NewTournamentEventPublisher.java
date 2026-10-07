package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.NewTournamentContext;
import ru.pulsecore.app.shared.dispetcher.MailDispatcher;
import ru.pulsecore.app.shared.dispetcher.PushDispatcher;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.shared.event.PushContent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import ru.pulsecore.app.tournament.domain.enums.LineupType;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NewTournamentEventPublisher {

    private final PushDispatcher pushDispatcher;
    private final MailDispatcher mailDispatcher;


    public void publish(Map<PlayerData, List<TournamentDto>> map) {
        if (map == null || map.isEmpty()) return;

        pushDispatcher.send(buildPushContent(map));
        mailDispatcher.send(buildMailContent(map));
    }

    // ==================== PUSH ====================

    private Map<UUID, PushContent> buildPushContent(Map<PlayerData, List<TournamentDto>> map) {
        Map<UUID, PushContent> result = new HashMap<>();

        for (Map.Entry<PlayerData, List<TournamentDto>> entry : map.entrySet()) {
            PlayerData player = entry.getKey();
            List<TournamentDto> tournaments = entry.getValue();
            if (tournaments == null || tournaments.isEmpty()) continue;

            boolean single = tournaments.size() == 1;
            String body = PushMessageBuilder.buildNewTournamentBody(player.name(), tournaments);
            String url = single ? tournaments.get(0).getLink() : "/dashboard#/tournaments";

            result.put(player.id(), new PushContent(
                    single ? "Новый турнир" : "Новые турниры",
                    body,
                    url
            ));
        }

        return result;
    }

    // ==================== MAIL ====================

    private Map<UUID, MailContent> buildMailContent(Map<PlayerData, List<TournamentDto>> map) {
        Map<UUID, MailContent> result = new HashMap<>();

        for (Map.Entry<PlayerData, List<TournamentDto>> entry : map.entrySet()) {
            PlayerData player = entry.getKey();
            List<TournamentDto> tournaments = entry.getValue();
            if (tournaments == null || tournaments.isEmpty()) continue;

            // 1 письмо на игрока — берём первый турнир
            TournamentDto tournament = tournaments.get(0);

            MailContext ctx = buildNewTournamentContext(player, tournament);
            result.put(player.id(), new MailContent(MailTypes.NEW_TOURNAMENT, ctx));
        }

        return result;
    }

    private MailContext buildNewTournamentContext(PlayerData player, TournamentDto tournament) {
        String firstName = StringUtils.extractFirstName(player.name());
        String rawDate = tournament.getDate() != null ? tournament.getDate().getDate() : null;
        String type = LineupType.fromApiType(tournament.getType()).displayName();

        return new NewTournamentContext(
                player.email(),
                firstName,
                DateTimeUtils.formatDate(rawDate),
                DateTimeUtils.formatTime(rawDate),
                tournament.getHall() != null ? tournament.getHall() : "—",
                tournament.getLeague() != null ? tournament.getLeague() : "—",
                tournament.getPlayers() != null && !tournament.getPlayers().isEmpty()
                        ? String.join("\n", tournament.getPlayers()) : "—",
                tournament.getLink() != null ? tournament.getLink() : "",
                type
        );
    }
}