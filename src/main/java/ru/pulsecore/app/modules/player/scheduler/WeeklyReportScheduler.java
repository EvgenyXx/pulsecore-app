// src/main/java/ru/pulsecore/app/modules/player/scheduler/WeeklyReportScheduler.java
package ru.pulsecore.app.modules.player.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.shared.service.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.shared.service.mail.MailTypes;
import ru.pulsecore.app.modules.tournament.api.dto.WeeklyStatsProjection;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyReportScheduler {

    private final MailStrategyRegistry mail;
    private final PlayerRepository playerRepository;
    private final TournamentResultRepository tournamentResultRepository;

    private static final UUID SHILO_ID = UUID.fromString("1aa334be-2297-4bfd-9028-151c2050ced6");
    private static final UUID KHRIPUNENKO_ID = UUID.fromString("f0dbf9e5-a0dc-4ffe-a11e-f7270c5a7326");

    @Scheduled(cron = "0 0 10 * * SUN")
    public void sendWeeklyReport() {
        var recipient = playerRepository.findById(KHRIPUNENKO_ID).orElse(null);
        if (recipient == null || recipient.getEmail() == null) {
            log.warn("Хрипуненко Павел не найден или нет почты");
            return;
        }

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);

        List<WeeklyStatsProjection> statsList = tournamentResultRepository.getWeeklyStats(SHILO_ID, monday, sunday);
        if (statsList.isEmpty()) {
            log.warn("Нет данных по игроку за период {} - {}", monday, sunday);
            return;
        }

        WeeklyStatsProjection stats = statsList.get(0);
        String report = formatReport(stats, monday, sunday);
        mail.send(MailTypes.PLAYER_REPORT, recipient.getEmail(), report);
        log.info("📊 Отчёт по {} отправлен {} на {}", stats.getName(), recipient.getName(), recipient.getEmail());
    }

    private String formatReport(WeeklyStatsProjection stats, LocalDate from, LocalDate to) {
        return String.format("""
                📊 Статистика игрока %s
                📅 Период: %s — %s
                
                🎯 Сыграно турниров: %d
                💰 Заработано: %.0f ₽
                📈 Средний заработок: %.0f ₽
                
                © PulseCore
                """,
                stats.getName(), from, to,
                stats.getTournaments(), stats.getTotal(), stats.getAverage());
    }
}