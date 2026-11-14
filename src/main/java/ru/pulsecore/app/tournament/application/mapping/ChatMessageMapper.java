package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.tournament.domain.entity.ChatMessage;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface ChatMessageMapper {


    @Mapping(target = "replyToId", expression = "java(entity.getReplyTo() != null ? entity.getReplyTo().getId() : null)")
    ChatMessageDto toDto(ChatMessage entity);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "edited", constant = "false")
    @Mapping(target = "replyTo", ignore = true)
    @Mapping(target = "replyToContent", ignore = true)
    @Mapping(target = "replyToSenderName", ignore = true)
    ChatMessage toEntity(Long lineupId, UUID playerId, String playerName, String message);
}