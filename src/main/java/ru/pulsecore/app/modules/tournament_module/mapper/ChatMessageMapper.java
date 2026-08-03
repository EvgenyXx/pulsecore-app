package ru.pulsecore.app.modules.tournament_module.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.modules.tournament_module.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.modules.tournament_module.entity.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "replyToId", expression = "java(entity.getReplyTo() != null ? entity.getReplyTo().getId() : null)")
    ChatMessageDto toDto(ChatMessage entity);
}