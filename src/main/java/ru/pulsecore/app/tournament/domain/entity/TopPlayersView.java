package ru.pulsecore.app.tournament.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import ru.pulsecore.app.player.domain.enums.City;

import java.util.UUID;

@Entity
@Table(name = "top_players_view")
@Immutable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopPlayersView {
    @Id
    @Column(name = "player_id")
    private UUID playerId;
    private String name;
    private String primaryLeague;
    private String period;
    private Double total;
    private Long tournaments;

    @Enumerated(EnumType.STRING)
    @Column(name = "city")
    private City city;
}