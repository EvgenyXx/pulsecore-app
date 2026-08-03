// modules/admin/dto/PageViewStats.java
package ru.pulsecore.app.modules.admin_modules.api.dto;

public record PageViewStats(String path, String method, long count) {}