package ru.pulsecore.app.player.infrastructure.persistence.mapping;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.pulsecore.app.admin.api.dto.request.UpdatePlayerRequest;
import ru.pulsecore.app.player.domain.Player;



@Mapper(componentModel = "spring")
public interface PlayerUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdatePlayerRequest request, @MappingTarget Player entity);
}