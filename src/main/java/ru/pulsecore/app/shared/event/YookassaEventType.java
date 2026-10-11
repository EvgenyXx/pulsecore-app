package ru.pulsecore.app.shared.event;

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
    PAYMENT_METHOD_ACTIVE("payment_method.active");

    private final String code;

    YookassaEventType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static YookassaEventType fromCode(String code) {
        for (YookassaEventType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}