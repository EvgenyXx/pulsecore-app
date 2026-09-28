package ru.pulsecore.app.player.application.mapper;

import org.mapstruct.*;
import ru.pulsecore.app.admin.api.dto.request.UpdatePlayerRequest;
import ru.pulsecore.app.player.api.dto.response.PlayerProfileResponse;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.persistence.repository.projection.PlayerDataProjection;
import ru.pulsecore.app.shared.dto.response.PlayerData;


@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdatePlayerRequest request, @MappingTarget Player entity);

    @Mapping(target = "hasActiveSubscription", expression = "java(player.hasActiveSubscription())")
    PlayerData toData(Player player);

    PlayerData toData(PlayerDataProjection projection);

    PlayerProfileResponse toProfileResponse(Player player);
}