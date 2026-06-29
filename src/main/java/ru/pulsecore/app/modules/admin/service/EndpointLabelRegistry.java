package ru.pulsecore.app.modules.admin.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EndpointLabelRegistry {

    private final Map<String, String> labels;

    public EndpointLabelRegistry(List<EndpointLabelProvider> providers) {
        this.labels = providers.stream()
                .flatMap(p -> p.labels().stream())
                .collect(Collectors.toMap(EndpointLabel::path, EndpointLabel::label, (a, b) -> a));
    }

    public String resolve(String path) {
        String normalized = path.replaceAll(
                "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}",
                "{id}"
        );
        return labels.getOrDefault(normalized, normalized);
    }
}