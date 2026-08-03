package ru.pulsecore.app.modules.notification_modules.application.mail.context;

import ru.pulsecore.app.modules.player_modeles.api.dto.response.SumResponse;

public record ScheduledReportContext(
        String to,
        String period,
        String sum,
        String avg,
        String count,
        SumResponse sumResponse
) implements MailContext {


}