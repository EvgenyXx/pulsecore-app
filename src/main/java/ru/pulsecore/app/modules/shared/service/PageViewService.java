package ru.pulsecore.app.modules.shared.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.shared.model.PageView;
import ru.pulsecore.app.modules.shared.repository.PageViewRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageViewService {

    private static final List<String> IGNORED_PATHS = List.of(
            "/api/chat/",
            "/api/online",
            "/api/auth/me"
    );

    private final PageViewRepository pageViewRepository;
    private final OnlineService onlineService;

    @Async
    public void save(UUID playerId, String email, String path, String method, String userAgent, String ip) {
        try {
            if (shouldIgnore(path)) {
                return;
            }
            pageViewRepository.save(PageView.builder()
                    .playerId(playerId)
                    .email(email)
                    .path(path)
                    .method(method)
                    .userAgent(userAgent)
                    .ip(ip)
                    .createdAt(Instant.now())
                    .build());
            if (playerId != null) {
                onlineService.markOnline(playerId);
            }
        } catch (Exception e) {
            log.error("Ошибка сохранения page view: {}", e.getMessage());
        }
    }

    private boolean shouldIgnore(String path) {
        if (path == null) return true;
        for (String ignored : IGNORED_PATHS) {
            if (path.startsWith(ignored)) {
                return true;
            }
        }
        return false;
    }
}