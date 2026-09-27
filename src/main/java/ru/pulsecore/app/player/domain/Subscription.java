package ru.pulsecore.app.player.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "player_id", unique = true)
    private Player player;

    @Builder.Default
    private boolean active = false;

    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isActiveNow() {
        return active && expiresAt != null && expiresAt.isAfter(LocalDateTime.now());
    }

    public void activate(int days) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime base;

        if (expiresAt != null && expiresAt.isAfter(now)) {
            base = expiresAt;
        } else {
            base = now;
        }

        this.active = true;

        if (this.startedAt == null) {
            this.startedAt = now;
        }

        this.expiresAt = base.plusDays(days);
    }
}