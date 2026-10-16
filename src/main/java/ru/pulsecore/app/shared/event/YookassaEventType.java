package ru.pulsecore.app.shared.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum YookassaEventType {

    // Платежи
    PAYMENT_WAITING_FOR_CAPTURE("payment.waiting_for_capture"),
    PAYMENT_SUCCEEDED("payment.succeeded"),
    PAYMENT_CANCELED("payment.canceled"),

    // Возвраты
    REFUND_SUCCEEDED("refund.succeeded"),

    // Выплаты
    PAYOUT_SUCCEEDED("payout.succeeded"),
    PAYOUT_CANCELED("payout.canceled"),

    // Сделки
    DEAL_CLOSED("deal.closed"),

    // Способы оплаты
    PAYMENT_METHOD_ACTIVE("payment_method.active"),

    UNKNOWN("");


    private final String code;



    public static YookassaEventType fromCode(String code) {
        if (code == null || code.isBlank()) {
            return UNKNOWN;
        }
        for (YookassaEventType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}