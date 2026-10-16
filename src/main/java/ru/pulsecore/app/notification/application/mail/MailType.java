package ru.pulsecore.app.notification.application.mail;

import lombok.Getter;

@Getter
public enum MailType {

    VERIFICATION("verification"),
    PASSWORD_RESET("password_reset"),
    WELCOME("welcome"),
    TOURNAMENT_RESULT("tournament_result"),

    NEW_TOURNAMENT("new_tournament"),
    CANCELED_TOURNAMENT("cancelled_tournament"),
    PLAYER_REPLACED("player_replaced"),
    PLAYER_TRANSFERRED("player_transferred"),
    TOURNAMENT_SCHEDULE_CHANGED("tournament_schedule_changed"),

    ADMIN_NEW_USER("admin_new_user"),
    ADMIN_PAYMENT_RECEIVED("admin_payment_received"),
    ADMIN_MASTERS_UNAVAILABLE("admin_masters_unavailable"),

    SCHEDULED_REPORT("scheduled_report"),
    BROKEN_URI("broken_uri"),

    BROADCAST("broadcast");

    private final String code;

    MailType(String code) {
        this.code = code;
    }

    public boolean requiresNotificationsEnabled() {
        return switch (this) {
            case NEW_TOURNAMENT,
                 CANCELED_TOURNAMENT,
                 PLAYER_REPLACED,
                 PLAYER_TRANSFERRED,
                 TOURNAMENT_SCHEDULE_CHANGED -> true;
            default -> false;
        };
    }

    public boolean requiresActiveSubscription() {
        return switch (this) {
            case NEW_TOURNAMENT,
                 CANCELED_TOURNAMENT,
                 PLAYER_REPLACED,
                 PLAYER_TRANSFERRED,
                 TOURNAMENT_SCHEDULE_CHANGED -> true;
            default -> false;
        };
    }
}