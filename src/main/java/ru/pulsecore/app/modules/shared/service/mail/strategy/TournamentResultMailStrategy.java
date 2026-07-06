package ru.pulsecore.app.modules.shared.service.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.service.mail.*;
import ru.pulsecore.app.modules.shared.service.mail.context.MailContext;
import ru.pulsecore.app.modules.shared.service.mail.context.TournamentResultContext;
import ru.pulsecore.app.modules.shared.service.mail.template.MailFormat;

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