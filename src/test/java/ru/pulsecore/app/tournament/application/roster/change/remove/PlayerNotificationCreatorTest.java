package ru.pulsecore.app.tournament.application.roster.change.remove;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerNotificationCreatorTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerNotificationRepository notificationRepository;

    @InjectMocks
    private PlayerNotificationCreator creator;

    private PlayerData player() {
        return new PlayerData(
                UUID.randomUUID(),
                "Иванов Иван",
                "ivan@example.com",
                null,
                true,
                true,
                true,
                null,
                null
        );
    }

    private TournamentDto tournamentDto() {
        TournamentDto dto = new TournamentDto();
        dto.setLink("https://masters-league.com/tours/liga-b-1608/");
        dto.setHall("Зал 10");
        return dto;
    }

    private TournamentEntity tournamentEntity() {
        TournamentEntity entity = new TournamentEntity();
        entity.setId(1L);
        entity.setExternalId(3011996L);
        entity.setLink("https://masters-league.com/tours/liga-b-1608/");
        return entity;
    }

    @Test
    void shouldCreateNotificationWhenTournamentExists() {
        PlayerData player = player();
        TournamentDto dto = tournamentDto();
        TournamentEntity entity = tournamentEntity();

        when(tournamentRepository.findByLink(dto.getLink())).thenReturn(Optional.of(entity));

        creator.createNotificationForTransfer(player, dto);

        verify(notificationRepository).save(any(PlayerNotification.class));
    }

    @Test
    void shouldNotCreateNotificationWhenTournamentNotFound() {
        PlayerData player = player();
        TournamentDto dto = tournamentDto();

        when(tournamentRepository.findByLink(dto.getLink())).thenReturn(Optional.empty());

        creator.createNotificationForTransfer(player, dto);

        verify(notificationRepository, never()).save(any(PlayerNotification.class));
    }

    @Test
    void shouldSetHallNumberFromTournament() {
        PlayerData player = player();
        TournamentDto dto = tournamentDto();
        TournamentEntity entity = tournamentEntity();

        when(tournamentRepository.findByLink(dto.getLink())).thenReturn(Optional.of(entity));

        creator.createNotificationForTransfer(player, dto);

        verify(notificationRepository).save(argThat(pn ->
                pn.getHall() == 10  // Зал 10 → 10
        ));
    }
}