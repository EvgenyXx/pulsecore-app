// src/main/java/ru/pulsecore/app/modules/payment/PriceService.java
package ru.pulsecore.app.modules.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.payment.exception.PaymentException;
import ru.pulsecore.app.modules.shared.model.AppSettings;
import ru.pulsecore.app.modules.shared.repository.AppSettingsRepository;


import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService {

    private final AppSettingsRepository repository;

    public Map<Integer, Integer> getPrices() {
        return Map.of(
                1, getPrice(1),
                2, getPrice(2)
        );
    }

    public int getPrice(int months) {
        String key = "price_" + months + (months == 1 ? "month" : "months");
        return repository.findByKey(key)
                .map(s -> Integer.parseInt(s.getValue()))
                .orElseThrow(() -> new PaymentException("Цена не найдена: " + key));
    }

    @Transactional
    public void update(int price1, int price2) {
        setValue("price_1month", String.valueOf(price1));
        setValue("price_2months", String.valueOf(price2));
        log.info("Цены обновлены: 1мес={}, 2мес={}", price1, price2);
    }

    private void setValue(String key, String value) {
        AppSettings setting = repository.findByKey(key)
                .orElse(AppSettings.builder().key(key).build());
        setting.setValue(value);
        repository.save(setting);
    }
}