package ru.pulsecore.app.shop.api.controller.seller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pulsecore.app.shop.api.SellerApi;
import ru.pulsecore.app.shop.api.dto.response.UploadResponse;
import ru.pulsecore.app.shop.infrastructure.storage.FileStorageService;



@Tag(name = "Seller — Upload", description = "Загрузка файлов")
@RestController
@RequestMapping(SellerApi.BASE_PATH)
@RequiredArgsConstructor
public class SellerUploadController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "Загрузить изображение товара")
    @PostMapping(SellerApi.UPLOAD)
    public ResponseEntity<UploadResponse> upload(
            @RequestParam(SellerApi.PARAM_FILE) MultipartFile file) {

        String url = fileStorageService.save(file,"products");
        return ResponseEntity.ok(new UploadResponse(url));
    }
}