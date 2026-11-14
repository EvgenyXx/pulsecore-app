package ru.pulsecore.app.tournament.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.infrastructure.cache.dto.SyncState;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SyncStateRepository {

    private static final Duration TTL = Duration.ofDays(7);
    private static final String PREFIX = "sync:player:";

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public void save(UUID playerId, SyncState state) {
        try {
            redis.opsForValue().set(PREFIX + playerId, mapper.writeValueAsString(state), TTL);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ошибка сериализации SyncState", e);
        }
    }

    public Optional<SyncState> find(UUID playerId) {
        String json = redis.opsForValue().get(PREFIX + playerId);
        if (json == null) return Optional.empty();
        try {
            return Optional.of(mapper.readValue(json, SyncState.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public void delete(UUID playerId) {
        redis.delete(PREFIX + playerId);
    }

    public Set<UUID> findAllPlayerIds() {
        Set<String> keys = redis.keys(PREFIX + "*");
        return keys.stream()
                .map(k -> UUID.fromString(k.substring(PREFIX.length())))
                .collect(Collectors.toSet());
    }

    public List<UUID> findAllSortedByCreatedAt() {
        return findAllPlayerIds().stream()
                .map(id -> find(id).map(state -> Map.entry(id, state.createdAt())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .map(Map.Entry::getKey)
                .toList();
    }




}