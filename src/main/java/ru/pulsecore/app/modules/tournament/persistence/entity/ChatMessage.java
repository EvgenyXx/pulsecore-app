package ru.pulsecore.app.modules.tournament.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_message")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lineup_id", nullable = false)
    private Long lineupId;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private ChatMessage replyTo;

    @Column(name = "reply_to_content")
    private String replyToContent;

    @Column(name = "reply_to_sender_name")
    private String replyToSenderName;
}