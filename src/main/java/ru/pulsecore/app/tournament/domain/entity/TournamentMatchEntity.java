package ru.pulsecore.app.tournament.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tournament_match",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"tournament_id", "player1_name", "player2_name", "stage"}
        ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentMatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @Column(name = "player1_name", nullable = false)
    private String player1Name;

    @Column(name = "player2_name", nullable = false)
    private String player2Name;

    @Column(name = "winner_name")
    private String winnerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private MatchStage stage;

    @Column(name = "score", length = 50)
    private String score;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}