package ru.pulsecore.app.modules.shared.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.shared.properties.AppProperties;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeService {

    private final AppProperties appProperties;

    public byte[] generateQrCode() throws Exception {
        String url = appProperties.getBaseUrl();
        log.info("QR URL: {}", url);

        Color qrColor = parseColor(appProperties.getQrColor());

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 900, 900);

        int border = 40;
        int size = 900 + border * 2;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, size, size, 60, 60);

        g.setColor(qrColor);
        for (int x = 0; x < 900; x++) {
            for (int y = 0; y < 900; y++) {
                if (matrix.get(x, y)) {
                    g.fillRect(x + border, y + border, 1, 1);
                }
            }
        }
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    private Color parseColor(String hex) {
        if (hex == null || hex.isBlank()) return Color.BLACK;
        if (!hex.startsWith("#")) hex = "#" + hex;
        try {
            return Color.decode(hex);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }
}