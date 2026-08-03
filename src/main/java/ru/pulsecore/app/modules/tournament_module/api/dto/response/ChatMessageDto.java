package ru.pulsecore.app.modules.tournament_module.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long lineupId;
    private UUID playerId;
    private String playerName;
    private String message;
    private LocalDateTime createdAt;
    private Long replyToId;
    private String replyToContent;
    private String replyToSenderName;
    private boolean edited;
}