package ru.pulsecore.app.modules.admin.api.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdatePricesRequest {
    @Min(value = 1, message = "Цена за 1 месяц должна быть больше 0")
    private int oneMonth;

    @Min(value = 1, message = "Цена за 2 месяца должна быть больше 0")
    private int twoMonths;
}