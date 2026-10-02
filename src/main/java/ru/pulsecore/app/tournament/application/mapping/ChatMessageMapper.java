package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.tournament.domain.entity.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "replyToId", expression = "java(entity.getReplyTo() != null ? entity.getReplyTo().getId() : null)")
    ChatMessageDto toDto(ChatMessage entity);
}