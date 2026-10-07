package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dispetcher.PushDispatcher;
import ru.pulsecore.app.shared.event.PushContent;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentResultNotifier {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final PushDispatcher pushDispatcher;


    public void publishLastResultEvent(List<TournamentResultEntity> results) {
        if (results == null) return;
        Map<UUID, PushContent> contentByPlayer = results.stream()
                .collect(Collectors.toMap(
                        TournamentResultEntity::getPlayerId,
                        r ->
                                new PushContent("Результат турнира", buildBody(r), buildUrl(r))));
        pushDispatcher.send(contentByPlayer);
    }

    private String buildBody(TournamentResultEntity r) {
        TournamentEntity t = r.getTournament();
        String date = formatDate(t.getDate());
        String time = t.getTime() == null ? "" : t.getTime();

        String when = time.isEmpty()
                ? "Турнир " + date
                : "Турнир " + date + " в " + time;

        return when + " завершён\nЗаработано: " + formatAmount(r.getAmount()) + " ₽";
    }

    private String buildUrl(TournamentResultEntity r) {
        return r.getTournament().getLink();
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FMT);
    }

    private String formatAmount(Double amount) {
        if (amount == null) return "0";
        return amount == Math.floor(amount)
                ? String.valueOf(amount.longValue())
                : String.valueOf(amount);
    }
}