// modules/admin/dto/PlayerPageViewStats.java
package ru.pulsecore.app.modules.admin.api.dto;

import java.util.List;

public record PlayerPageViewStats(
        String name,
        List<PathCount> paths,
        long totalCount,
        double percent
) {
    public record PathCount(String path, long count) {}
}