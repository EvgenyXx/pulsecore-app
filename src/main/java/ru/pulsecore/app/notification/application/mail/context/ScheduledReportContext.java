package ru.pulsecore.app.notification.application.mail.context;

import ru.pulsecore.app.player.api.dto.response.SumResponse;

public record ScheduledReportContext(
        String to,
        String period,
        String sum,
        String avg,
        String count,
        SumResponse sumResponse
) implements MailContext {


}