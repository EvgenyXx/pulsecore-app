package ru.pulsecore.app.modules.player.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.config.CacheNames;
import ru.pulsecore.app.modules.player.domain.ReportStatus;
import ru.pulsecore.app.modules.player.domain.ScheduledReport;
import ru.pulsecore.app.modules.player.repository.ScheduledReportRepository;

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