package ru.pulsecore.app.payment.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConfirmationType {
    REDIRECT("redirect"),
    EMBEDDED("embedded"),
    QR("qr"),
    EXTERNAL("external"),
    MOBILE_APPLICATION("mobile_application");

    private final String value;

    ConfirmationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}