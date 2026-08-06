package ru.pulsecore.app.tournament.application.report;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.api.dto.response.ScheduledReportResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduledReportFacade {

    private final ScheduledReportService reportService;

    @Transactional
    public void createReport(UUID playerId, LocalDate from, LocalDate to, LocalDateTime scheduledAt) {
        reportService.create(playerId, from, to, scheduledAt);
    }

    public List<ScheduledReportResponse> getPlayerReports(UUID playerId) {
        return reportService.findPendingByPlayer(playerId).stream()
                .map(ScheduledReportResponse::from)
                .toList();
    }


    @Transactional
    public void deleteByScheduled(UUID scheduled) {
        reportService.deleteByScheduled(scheduled);
    }
}