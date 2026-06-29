package ru.pulsecore.app.modules.admin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricesResponse implements Serializable {

    private Map<Integer, Integer> prices;
}