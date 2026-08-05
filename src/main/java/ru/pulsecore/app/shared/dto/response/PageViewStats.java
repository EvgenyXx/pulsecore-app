package ru.pulsecore.app.shared.dto.response;

public record PageViewStats(String path, String method, long count) {}