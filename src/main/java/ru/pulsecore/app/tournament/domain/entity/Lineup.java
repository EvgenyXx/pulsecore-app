package ru.pulsecore.app.tournament.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.pulsecore.app.tournament.domain.enums.LineupType;

import java.time.LocalDate;

@Entity
@Table(
        name = "lineup",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"league", "time", "date", "hall"})
        },
        indexes = {
                @Index(name = "idx_lineup_date", columnList = "date")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lineup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String league;

    @Column(nullable = false, length = 10)
    private String time;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String players;

    @Column(length = 100)
    private String hall;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String streamUrl;

    @Column(length = 500)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private LineupType type;


}