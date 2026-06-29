package ru.pulsecore.app.modules.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPeriod {
    ONE_MONTH(1, "1 месяц"),
    TWO_MONTHS(2, "2 месяца");

    private final int months;
    private final String label;

    public String getPriceKey() {
        return "price_" + (months == 1 ? "1month" : months + "months");
    }

    public static SubscriptionPeriod fromMonths(int months) {
        return Arrays.stream(values())
                .filter(p -> p.months == months)
                .findFirst()
                .orElse(null);
    }
}