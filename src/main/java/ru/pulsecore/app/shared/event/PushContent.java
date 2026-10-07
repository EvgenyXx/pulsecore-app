package ru.pulsecore.app.shared.event;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record PushContent(
            String title,
            String body,
            String url
    ) {

     /**
     * Один и тот же контент для всех указанных игроков.
     */
    public static Map<UUID, PushContent> sameForAll(
            Collection<UUID> ids, String title, String body, String url) {
        PushContent content = new PushContent(title, body, url);
        return ids.stream().collect(Collectors.toMap(id -> id, id -> content));
    }
 }

