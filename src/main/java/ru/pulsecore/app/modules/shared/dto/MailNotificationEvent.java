package ru.pulsecore.app.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pulsecore.app.modules.notification.application.mail.context.MailContext;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailNotificationEvent {
    private String emailType;
    private MailContext contextMessage;
}