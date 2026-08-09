package ru.pulsecore.app.notification.application.mail.context.admin;

import ru.pulsecore.app.notification.application.mail.context.MailContext;

public record AdminNewUserContext(
        String name,
        String email,
        String ip,
        String device,
        String os,
        String browser,
        String userAgent
) implements MailContext {


}