package ru.pulsecore.app.shared.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pulsecore.app.notification.application.mail.context.MailContext;

import java.util.UUID;


//todo удалить
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailNotificationEvent {
    private String emailType;
    private UUID playerId;
    private MailContext contextMessage;

    public MailNotificationEvent(String emailType, MailContext contextMessage) {
        this.emailType = emailType;
        this.contextMessage = contextMessage;
    }
}