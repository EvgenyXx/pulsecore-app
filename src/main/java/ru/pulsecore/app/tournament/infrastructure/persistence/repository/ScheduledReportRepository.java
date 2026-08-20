package ru.pulsecore.app.tournament.infrastructure.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.player.domain.ReportStatus;
import ru.pulsecore.app.tournament.domain.entity.ScheduledReport;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.ScheduledReportProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduledReportRepository extends JpaRepository<ScheduledReport, UUID> {


    @Query("SELECT r.id AS id, r.playerId AS playerId, r.dateFrom AS dateFrom, r.dateTo" +
            " AS dateTo FROM ScheduledReport r WHERE r.status = 'PENDING' AND r.scheduledAt < :now")
    List<ScheduledReportProjection> findPendingBefore(@Param("now") LocalDateTime now);


    List<ScheduledReport> findByPlayerIdAndStatus(UUID playerId, ReportStatus status);
}
