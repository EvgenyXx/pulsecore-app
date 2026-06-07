// modules/shared/service/PageViewService.java
package ru.pulsecore.app.modules.shared.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.shared.model.PageView;
import ru.pulsecore.app.modules.shared.repository.PageViewRepository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageViewService {

    private final PageViewRepository pageViewRepository;

    @Async
    public void save(UUID playerId, String email, String path, String method, String userAgent, String ip) {
        try {
            pageViewRepository.save(PageView.builder()
                    .playerId(playerId)
                    .email(email)
                    .path(path)
                    .method(method)
                    .userAgent(userAgent)
                    .ip(ip)
                    .createdAt(Instant.now())
                    .build());
        } catch (Exception e) {
            log.error("Ошибка сохранения page view: {}", e.getMessage());
        }
    }
}