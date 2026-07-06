package ru.pulsecore.app.modules.shared.service.mail.context;

import ru.pulsecore.app.modules.player.api.dto.sum.SumResponse;

public record ScheduledReportContext(
        String to,
        String period,
        String sum,
        String avg,
        String count,
        SumResponse sumResponse
) implements MailContext {


}