package ru.pulsecore.app.tournament.application.mapping.helper;


import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class ScoreHelper {

    @Named(MappingConstants.NORMALIZE_SCORE)
    public static String normalizeScore(String score) {
        if (score == null || score.isBlank()) return score;

        String separator = score.contains(":") ? ":" : "-";
        String[] parts = score.split(separator.equals(":") ? ":" : "-");
        if (parts.length != 2) return score;

        try {
            int left = Integer.parseInt(parts[0].trim());
            int right = Integer.parseInt(parts[1].trim());

            if (right > left) {
                return right + separator + left;
            }

            return left + separator + right;

        } catch (NumberFormatException e) {
            return score;
        }
    }
}