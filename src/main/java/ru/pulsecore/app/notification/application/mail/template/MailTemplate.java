package ru.pulsecore.app.notification.application.mail.template;

import lombok.Getter;

@Getter
public enum MailTemplate {
    ADMIN_NEW_USER("admin_new_user"),
    ADMIN_PAYMENT("admin_payment"),
    VERIFICATION("verification"),
    PASSWORD_RESET("password_reset"),
    WELCOME("welcome"),
    NEW_TOURNAMENT("new_tournament"),
    TOURNAMENT_RESULT("tournament_result"),
    BROADCAST("broadcast"),
    SCHEDULED_REPORT("scheduled_report"),
    BROKEN_URI("broken_uri"),
    PLAYER_REPLACED("player_replaced"),
    CANCELED_TOURNAMENT("canceled_tournament"),
    TOURNAMENT_SCHEDULE_CHANGED("tournament_schedule_changed"),
    PAYMENT_SUCCESS_USER("payment_success_user"),
    PLAYER_TRANSFERRED("player_transferred");


    private final String fileName;

    MailTemplate(String fileName) {
        this.fileName = fileName;
    }

}