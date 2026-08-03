package ru.pulsecore.app.modules.tournament_module.domain;

public enum ChatEventType {
    DELETE,
    EDIT;

    public String asType() {
        return name();
    }
}