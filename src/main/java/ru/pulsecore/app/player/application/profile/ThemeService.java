package ru.pulsecore.app.player.application.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.model.AppSettings;
import ru.pulsecore.app.shared.repository.AppSettingsRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private static final String PREFIX = "theme:";
    private static final String DEFAULT_THEME = "dark";

    private final AppSettingsRepository appSettingsRepository;

    public String getTheme(UUID playerId) {
        return appSettingsRepository.findByKey(PREFIX + playerId)
                .map(AppSettings::getValue)
                .orElse(DEFAULT_THEME);
    }

    @Transactional
    public void setTheme(UUID playerId, String theme) {
        AppSettings setting = appSettingsRepository.findByKey(PREFIX + playerId)
                .orElse(AppSettings.builder().key(PREFIX + playerId).build());
        setting.setValue(theme);
        appSettingsRepository.save(setting);
    }
}