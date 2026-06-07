// modules/shared/repository/PageViewStatsProjection.java
package ru.pulsecore.app.modules.shared.repository;

public interface PageViewStatsProjection {
    String getPath();
    String getMethod();
    long getCount();
}