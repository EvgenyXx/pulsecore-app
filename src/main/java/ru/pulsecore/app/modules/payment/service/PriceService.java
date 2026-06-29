package ru.pulsecore.app.modules.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.config.CacheNames;
import ru.pulsecore.app.modules.admin.api.dto.PricesResponse;
import ru.pulsecore.app.modules.payment.domain.SubscriptionPeriod;
import ru.pulsecore.app.modules.payment.exception.PaymentException;
import ru.pulsecore.app.modules.shared.model.AppSettings;
import ru.pulsecore.app.modules.shared.repository.AppSettingsRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService {

    private final AppSettingsRepository repository;

    @Cacheable(CacheNames.PRICES)
    public PricesResponse getPrices() {
        Map<Integer, Integer> prices = Arrays.stream(SubscriptionPeriod.values())
                .collect(Collectors.toMap(
                        SubscriptionPeriod::getMonths,
                        p -> getPrice(p.getMonths())
                ));
        return new PricesResponse(prices);
    }

    public int getPrice(int months) {
        String key = SubscriptionPeriod.fromMonths(months).getPriceKey();
        return repository.findByKey(key)
                .map(s -> Integer.parseInt(s.getValue()))
                .orElseThrow(() -> new PaymentException("Цена не найдена: " + key));
    }

    @CacheEvict(value = CacheNames.PRICES, allEntries = true)
    @Transactional
    public void update(int price1, int price2) {
        setValue(SubscriptionPeriod.ONE_MONTH.getPriceKey(), String.valueOf(price1));
        setValue(SubscriptionPeriod.TWO_MONTHS.getPriceKey(), String.valueOf(price2));
        log.info("Цены обновлены: 1мес={}, 2мес={}", price1, price2);
    }

    private void setValue(String key, String value) {
        AppSettings setting = repository.findByKey(key)
                .orElse(AppSettings.builder().key(key).build());
        setting.setValue(value);
        repository.save(setting);
    }
}