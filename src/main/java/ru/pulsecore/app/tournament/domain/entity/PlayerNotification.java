package ru.pulsecore.app.tournament.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "player_notification",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"player_id", "tournament_id"}
        )
)
public class PlayerNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @Builder.Default
    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Builder.Default
    @Column(name = "evening_sent", nullable = false)
    private boolean eveningSent = false;

    @Builder.Default
    @Column(name = "push_reminder_sent", nullable = false)
    private boolean pushReminderSent = false;

    @Builder.Default
    @Column(name = "push_evening_sent", nullable = false)
    private boolean pushEveningSent = false;

    private Integer hall;
}