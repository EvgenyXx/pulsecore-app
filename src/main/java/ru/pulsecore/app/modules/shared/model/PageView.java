// modules/shared/model/PageView.java
package ru.pulsecore.app.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "page_views")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id")
    private UUID playerId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(length = 45)
    private String ip;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}