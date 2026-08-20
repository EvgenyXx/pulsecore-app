package ru.pulsecore.app.tournament.domain.enums;

public enum ChatEventType {
    DELETE,
    EDIT;

    public String asType() {
        return name();
    }
}