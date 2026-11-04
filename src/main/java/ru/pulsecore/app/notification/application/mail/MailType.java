package ru.pulsecore.app.notification.application.mail;

import lombok.Getter;

@Getter
public enum MailType {

    //не должны проверять
    VERIFICATION("verification"),
    PASSWORD_RESET("password_reset"),
    WELCOME("welcome"),
    /// ////////////////////////////////////////

    //admin
    ADMIN_NEW_USER("admin_new_user"),//admin
    ADMIN_PAYMENT_RECEIVED("admin_payment_received"),//admin
    ADMIN_MASTERS_UNAVAILABLE("admin_masters_unavailable"),//admin
    BROKEN_URI("broken_uri"),//admin


    TOURNAMENT_RESULT("tournament_result"),
    NEW_TOURNAMENT("new_tournament"),
    CANCELED_TOURNAMENT("cancelled_tournament"),
    PLAYER_REPLACED("player_replaced"),
    PLAYER_TRANSFERRED("player_transferred"),
    TOURNAMENT_SCHEDULE_CHANGED("tournament_schedule_changed"),
    SCHEDULED_REPORT("scheduled_report"),
    BROADCAST("broadcast");

    private final String code;

    MailType(String code) {
        this.code = code;
    }

    public boolean requiresNotificationsEnabled() {
        return switch (this) {
            case TOURNAMENT_RESULT,
                 NEW_TOURNAMENT,
                 CANCELED_TOURNAMENT,
                 PLAYER_REPLACED,
                 PLAYER_TRANSFERRED,
                 TOURNAMENT_SCHEDULE_CHANGED,
                 SCHEDULED_REPORT,
                 BROADCAST -> true;
            default -> false;
        };
    }

    public boolean requiresActiveSubscription() {
        return switch (this) {
            case TOURNAMENT_RESULT,
                 NEW_TOURNAMENT,
                 CANCELED_TOURNAMENT,
                 PLAYER_REPLACED,
                 PLAYER_TRANSFERRED,
                 TOURNAMENT_SCHEDULE_CHANGED,
                 SCHEDULED_REPORT,
                 BROADCAST -> true;
            default -> false;
        };
    }
}