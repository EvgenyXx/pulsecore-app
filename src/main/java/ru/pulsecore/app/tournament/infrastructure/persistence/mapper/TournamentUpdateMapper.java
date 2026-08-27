package ru.pulsecore.app.tournament.infrastructure.persistence.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.pulsecore.app.admin.api.dto.request.UpdateTournamentRequest;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;

@Mapper(componentModel = "spring")
public interface TournamentUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateTournamentRequest request, @MappingTarget TournamentEntity entity);
}