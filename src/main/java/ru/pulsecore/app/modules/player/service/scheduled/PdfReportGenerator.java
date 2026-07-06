// 2. PdfReportGenerator.java
package ru.pulsecore.app.modules.player.service.scheduled;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.api.dto.sum.SumResponse;

import java.io.ByteArrayOutputStream;

@Component
public class PdfReportGenerator {

    private static final DeviceRgb BLACK = new DeviceRgb(0, 0, 0);
    private static final DeviceRgb GRAY = new DeviceRgb(161, 161, 170);

    public byte[] generate(SumResponse sumResponse) {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(pdfStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(12, 12, 12, 12);

        if (sumResponse.getTournaments() != null && !sumResponse.getTournaments().isEmpty()) {
            Table table = new Table(new float[]{1, 3, 2});
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(hCell("№"));
            table.addHeaderCell(hCell("Дата"));
            table.addHeaderCell(hCellRight("Сумма"));

            int i = 1;
            for (var t : sumResponse.getTournaments()) {
                table.addCell(dataCell(String.valueOf(i++)));
                table.addCell(dataCell(t.getDate()));
                table.addCell(dataCellRight(String.format("%,.0f ₽", t.getAmount())));
            }
            document.add(table);
        }

        document.close();
        return pdfStream.toByteArray();
    }

    private Cell hCell(String text) {
        return new Cell().setBorderBottom(new SolidBorder(GRAY, 0.5f)).setPadding(6)
                .add(new Paragraph(text).setFontSize(10).setBold().setFontColor(GRAY));
    }

    private Cell hCellRight(String text) {
        return new Cell().setBorderBottom(new SolidBorder(GRAY, 0.5f)).setPadding(6)
                .add(new Paragraph(text).setFontSize(10).setBold().setFontColor(GRAY)
                        .setTextAlignment(TextAlignment.RIGHT));
    }

    private Cell dataCell(String text) {
        return new Cell().setBorderBottom(new SolidBorder(GRAY, 0.2f)).setPadding(6)
                .add(new Paragraph(text).setFontSize(14).setFontColor(BLACK).setBold());
    }

    private Cell dataCellRight(String text) {
        return new Cell().setBorderBottom(new SolidBorder(GRAY, 0.2f)).setPadding(6)
                .add(new Paragraph(text).setFontSize(14).setFontColor(BLACK).setBold()
                        .setTextAlignment(TextAlignment.RIGHT));
    }
}