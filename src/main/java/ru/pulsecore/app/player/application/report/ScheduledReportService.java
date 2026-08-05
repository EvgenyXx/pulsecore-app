package ru.pulsecore.app.player.application.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.entity.ReportStatus;
import ru.pulsecore.app.player.entity.ScheduledReport;
import ru.pulsecore.app.player.infrastructure.persistence.repository.ScheduledReportRepository;

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

    public List<ScheduledReport> findPendingBefore(LocalDateTime time) {
        return reportRepository.findByStatusAndScheduledAtBefore(ReportStatus.PENDING, time);
    }

    @Transactional
    public void markAsSent(UUID reportId) {
        reportRepository.findById(reportId).ifPresent(report -> {
            report.setStatus(ReportStatus.SENT);
            reportRepository.save(report);
        });
    }



}