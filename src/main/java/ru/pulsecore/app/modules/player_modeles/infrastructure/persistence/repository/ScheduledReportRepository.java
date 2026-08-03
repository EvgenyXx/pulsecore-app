package ru.pulsecore.app.modules.player_modeles.infrastructure.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.modules.player_modeles.entity.ReportStatus;
import ru.pulsecore.app.modules.player_modeles.entity.ScheduledReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduledReportRepository extends JpaRepository<ScheduledReport, UUID> {
    List<ScheduledReport> findByStatusAndScheduledAtBefore(ReportStatus reportStatus, LocalDateTime time);

    // ScheduledReportRepository.java
    List<ScheduledReport> findByPlayerIdAndStatus(UUID playerId, ReportStatus status);
}
