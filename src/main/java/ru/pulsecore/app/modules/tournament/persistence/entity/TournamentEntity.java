package ru.pulsecore.app.modules.tournament.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournament")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long externalId;

    private String link;

    private LocalDate date;

    private String time;

    private boolean started;

    private boolean finished;

    private boolean cancelled;

    private boolean processed;

    // 🔥 ДОБАВИЛ
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerNotification> notifications = new ArrayList<>();
}