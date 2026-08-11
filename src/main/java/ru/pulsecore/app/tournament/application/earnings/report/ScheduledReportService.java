package ru.pulsecore.app.tournament.application.earnings.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.domain.ReportStatus;
import ru.pulsecore.app.tournament.domain.entity.ScheduledReport;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.ScheduledReportRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.ScheduledReportProjection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduledReportService {

    private final ScheduledReportRepository reportRepository;

    public void create(UUID playerId, LocalDate from, LocalDate to, LocalDateTime scheduledAt) {
        ScheduledReport report = ScheduledReport.builder()
                .playerId(playerId)
                .dateFrom(from)
                .dateTo(to)
                .scheduledAt(scheduledAt)
                .build();
         reportRepository.save(report);
    }



    public List<ScheduledReport> findPendingByPlayer(UUID playerId) {
        return reportRepository.findByPlayerIdAndStatus(playerId, ReportStatus.PENDING);
    }


    public void deleteByScheduled(UUID scheduled) {
        reportRepository.deleteById(scheduled);
    }

    public List<ScheduledReportProjection> findPendingBefore(LocalDateTime time) {
        return reportRepository.findPendingBefore(time);
    }


    public void markAsSent(UUID reportId) {
        reportRepository.findById(reportId).ifPresent(report -> {
            report.setStatus(ReportStatus.SENT);
            reportRepository.save(report);
        });
    }



}