// modules/shared/repository/PageViewStatsProjection.java
package ru.pulsecore.app.modules.admin.api.dto;

public interface PageViewStatsProjection {
    String getPath();
    String getMethod();
    long getCount();
}