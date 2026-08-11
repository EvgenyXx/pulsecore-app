package ru.pulsecore.app.tournament.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerNotificationRepository
        extends JpaRepository<PlayerNotification, Long> {

    boolean existsByPlayerIdAndTournament_ExternalId(UUID playerId, Long externalId);

    void deleteByTournament_FinishedTrueAndTournament_DateBefore(LocalDate date);

    @Query("""
        SELECT pn
        FROM PlayerNotification pn
        JOIN FETCH pn.tournament t
        WHERE t.finished = false
          AND t.cancelled = false
          AND pn.pushReminderSent = false
    """)
    List<PlayerNotification> findPendingWithTournament();

    @Query("SELECT pn FROM PlayerNotification pn JOIN FETCH " +
            "pn.tournament t WHERE t.started = true AND t.finished = false AND t.cancelled = false")
    List<PlayerNotification> findStartedNotFinished();


    @Query("SELECT pn FROM PlayerNotification pn JOIN FETCH pn.tournament" +
            " t WHERE t.started = false AND t.cancelled = false AND t.finished = false")
    List<PlayerNotification> findNotStartedForCancel();
}