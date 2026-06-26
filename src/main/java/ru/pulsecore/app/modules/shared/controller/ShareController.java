package ru.pulsecore.app.modules.shared.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.shared.api.SharedApi;
import ru.pulsecore.app.modules.shared.service.QrCodeService;

@RestController
@RequestMapping(SharedApi.BASE_PATH)
@RequiredArgsConstructor
public class ShareController {

    private final QrCodeService qrCodeService;

    @GetMapping(SharedApi.QR)
    public ResponseEntity<byte[]> getQrCode() throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCodeService.generateQrCode());
    }
}