package ru.pulsecore.app.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pulsecore.app.notification.application.mail.context.MailContext;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailNotificationEvent {
    private String emailType;
    private MailContext contextMessage;
}