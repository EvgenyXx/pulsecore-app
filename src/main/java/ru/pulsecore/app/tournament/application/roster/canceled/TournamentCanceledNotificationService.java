package ru.pulsecore.app.tournament.application.roster.canceled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.CanceledTournamentContext;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.shared.util.PushMessageBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Отправляет уведомления об отмене турнира.
 * Email — через MailNotificationEvent, Push — через PushNotificationEvent.
 * Проверяет настройки игрока перед отправкой.
 * Вызывается из TournamentFinishProcessor при обнаружении статуса CANCELLED.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentCanceledNotificationService {

    private final ApplicationEventPublisher eventPublisher;
    private final PlayerClient playerClient;
    // todo: вынести handleCancelled в отдельный TournamentCancelProcessor
// todo: убрать проверку CANCELLED из TournamentFinishProcessor.processByStatus
// todo: создать findCancelled() запрос в репозитории для TournamentCancelProcessor
    public void sendCancelled(List<PlayerNotification> notifications) {
        Set<UUID> playerIds = notifications.stream()
                .map(PlayerNotification::getPlayerId)
                .collect(Collectors.toSet());

        Map<UUID, PlayerData> playerMap = playerClient.getPlayerDataByIds(playerIds).stream()
                .collect(Collectors.toMap(PlayerData::playerId, p -> p));

        for (PlayerNotification pn : notifications) {
            PlayerData player = playerMap.get(pn.getPlayerId());
            if (player == null) continue;

            TournamentEntity tournament = pn.getTournament();
            String time = tournament.getTime() != null ? tournament.getTime() : "?";
            String date = tournament.getDate() != null ? tournament.getDate().toString() : "?";

            emailSend(player, time, date, tournament.getLink());
            canSendPush(player, time, date);
        }
        log.info("Отмена турнира: уведомления отправлены {} игрокам", notifications.size());
    }

    private void canSendPush(PlayerData playerData,String time,String date) {
        if (playerData.pushEnabled()){
            eventPublisher.publishEvent(
                    new PushNotificationEvent(
                            playerData.playerId(),
                            "❌ Турнир отменён!",
                            PushMessageBuilder.buildCancelledBody(date, time),
                            "/dashboard"
                    )
            );
        }
    }


    private void emailSend(PlayerData playerData,String time,String date,String link) {
           if (playerData.notificationsEnabled()){
               eventPublisher.publishEvent(
                       new MailNotificationEvent(
                               MailTypes.CANCELED_TOURNAMENT,
                               new CanceledTournamentContext(
                                       playerData.email(),
                                       time,date,link

                               )
                       )
               );
           }
       }

}