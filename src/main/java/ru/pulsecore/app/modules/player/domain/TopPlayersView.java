// ==================== TopPlayersView.java ====================
package ru.pulsecore.app.modules.player.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

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
}