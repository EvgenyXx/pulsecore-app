package ru.pulsecore.app.player.application.mapper;


import org.mapstruct.Mapper;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.shared.dto.response.SubscriptionStatusResponse;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionStatusResponse toStatusResponse(Subscription subscription);
}
