// modules/shared/repository/PageViewStatsProjection.java
package ru.pulsecore.app.modules.admin.dto;

public interface PageViewStatsProjection {
    String getPath();
    String getMethod();
    long getCount();
}