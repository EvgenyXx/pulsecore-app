package ru.pulsecore.app.modules.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pulsecore.app.modules.shared.model.AppSettings;

import java.util.Optional;
import java.util.UUID;

public interface AppSettingsRepository extends JpaRepository<AppSettings, UUID> {
    Optional<AppSettings> findByKey(String key);
}