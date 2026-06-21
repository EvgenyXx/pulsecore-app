package ru.pulsecore.app.modules.tournament.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.modules.tournament.api.dto.ChatMessageDto;
import ru.pulsecore.app.modules.tournament.persistence.entity.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "replyToId", expression = "java(entity.getReplyTo() != null ? entity.getReplyTo().getId() : null)")
    ChatMessageDto toDto(ChatMessage entity);
}