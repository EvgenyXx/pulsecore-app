package ru.pulsecore.app.tournament.domain.enums;

public enum LineupType {
    STANDARD,
    FOUR_PL_NEW;

   public static LineupType fromApiType(String type) {
    if (type == null) return STANDARD;
    if (type.toLowerCase().contains("new")) return FOUR_PL_NEW;
    return STANDARD;
}
}