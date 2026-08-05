// ScheduledReportMailStrategy.java
package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.player.infrastructure.pdf.PdfReportGenerator;

import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.ScheduledReportContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;

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