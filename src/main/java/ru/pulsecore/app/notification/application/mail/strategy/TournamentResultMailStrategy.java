package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;

import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.TournamentResultContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;

@Component
@RequiredArgsConstructor
public class TournamentResultMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;

    @Override
    public String getType() {
        return MailTypes.TOURNAMENT_RESULT;
    }

    @Override
    public void send(MailContext ctx) {
        TournamentResultContext c = (TournamentResultContext) ctx;
        String text = "Ваш результат: " + c.result();
        mailSender.send(MailFormat.TEXT, c.to(), "Результаты турнира", text, null, null);
    }
}