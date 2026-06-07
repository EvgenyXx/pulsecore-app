// modules/admin/dto/PageViewStats.java
package ru.pulsecore.app.modules.admin.dto;

public record PageViewStats(String path, String method, long count) {}