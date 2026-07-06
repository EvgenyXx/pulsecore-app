// ScheduledReportMailStrategy.java
package ru.pulsecore.app.modules.shared.service.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.service.scheduled.PdfReportGenerator;
import ru.pulsecore.app.modules.shared.service.mail.*;
import ru.pulsecore.app.modules.shared.service.mail.context.MailContext;
import ru.pulsecore.app.modules.shared.service.mail.context.ScheduledReportContext;
import ru.pulsecore.app.modules.shared.service.mail.template.MailFormat;
import ru.pulsecore.app.modules.shared.service.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class ScheduledReportMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final PdfReportGenerator pdfGenerator;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.SCHEDULED_REPORT;
    }

    @Override
    public void send(MailContext ctx) {
        ScheduledReportContext c = (ScheduledReportContext) ctx;
        byte[] pdf = pdfGenerator.generate(c.sumResponse());
        String text = templates.format(MailTemplate.SCHEDULED_REPORT,
                c.period(), c.sum(), c.avg(), c.count());
        String fileName = "отчёт_" + c.period().replace(" – ", "_") + ".pdf";

        mailSender.send(
                MailFormat.PDF,
                c.to(),
                "PulseCore — Ваш отчёт за " + c.period(),
                text,
                fileName,
                pdf
        );
    }
}