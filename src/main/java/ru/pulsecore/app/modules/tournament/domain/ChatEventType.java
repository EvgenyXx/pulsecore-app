package ru.pulsecore.app.modules.tournament.domain;

public enum ChatEventType {
    DELETE,
    EDIT;

    public String asType() {
        return name();
    }
}