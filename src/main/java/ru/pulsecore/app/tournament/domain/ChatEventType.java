package ru.pulsecore.app.tournament.domain;

public enum ChatEventType {
    DELETE,
    EDIT;

    public String asType() {
        return name();
    }
}