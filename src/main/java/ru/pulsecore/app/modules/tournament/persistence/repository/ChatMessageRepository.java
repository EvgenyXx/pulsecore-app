package ru.pulsecore.app.modules.tournament.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.modules.tournament.persistence.entity.ChatMessage;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByLineupIdOrderByCreatedAtAsc(Long lineupId);

    @Query("SELECT COUNT(DISTINCT c.playerId) FROM ChatMessage c WHERE c.lineupId = :lineupId AND c.createdAt > :after")
    long countDistinctPlayerIdByLineupIdAndCreatedAtAfter(@Param("lineupId") Long lineupId, @Param("after") LocalDateTime after);

    @Query("SELECT c FROM ChatMessage c WHERE c.lineupId = :lineupId AND c.id > :afterId ORDER BY c.createdAt ASC")
    List<ChatMessage> findByIdAfterAndLineupIdOrderByCreatedAtAsc(@Param("afterId") Long afterId, @Param("lineupId") Long lineupId);

    void deleteByPlayerId(UUID id);

    @Query("SELECT DISTINCT c.lineupId FROM ChatMessage c WHERE c.createdAt > :after")
    List<Long> findActiveLineupIds(@Param("after") LocalDateTime after);
}