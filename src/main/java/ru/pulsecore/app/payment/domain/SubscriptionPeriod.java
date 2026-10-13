package ru.pulsecore.app.payment.domain;

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
    return switch (this) {
        case ONE_MONTH -> "price_1month";
        case TWO_MONTHS -> "price_2months";
    };
}

    public static SubscriptionPeriod fromMonths(int months) {
        return Arrays.stream(values())
                .filter(p -> p.months == months)
                .findFirst()
                .orElse(null);
    }
}