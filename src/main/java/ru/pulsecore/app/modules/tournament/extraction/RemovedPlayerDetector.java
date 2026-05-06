package ru.pulsecore.app.modules.tournament.extraction;

import ru.pulsecore.app.core.model.Match;
import ru.pulsecore.app.modules.tournament.calculation.MatchStage;
import ru.pulsecore.app.modules.tournament.calculation.strategy.removed.RemovedStage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RemovedPlayerDetector {

    public RemovedResult detect(String groupRemovedPlayer, List<Match> matches) {
        String removedPlayer = groupRemovedPlayer;
        RemovedStage stage = RemovedStage.NONE;

        // 1. Групповой этап
        if (removedPlayer != null && !removedPlayer.isBlank()) {
            stage = RemovedStage.GROUP;
        }
        // 2. Полуфинал
        else {
            removedPlayer = detectRemovedPlayerFromSemi(matches);
            if (removedPlayer != null && !removedPlayer.isBlank()) {
                stage = RemovedStage.SEMI_FINAL;
            }
        }

        // 3. Финал
        if (stage == RemovedStage.NONE) {
            removedPlayer = detectCanceledMatch(matches, MatchStage.FINAL);
            if (removedPlayer != null) {
                stage = RemovedStage.FINAL;
            }
        }

        // 4. Матч за 3-е место
        if (stage == RemovedStage.NONE) {
            removedPlayer = detectCanceledMatch(matches, MatchStage.THIRD_PLACE);
            if (removedPlayer != null) {
                stage = RemovedStage.THIRD_PLACE;
            }
        }

        return new RemovedResult(removedPlayer, stage);
    }

    // Универсальный поиск отменённого матча по стадии
    private String detectCanceledMatch(List<Match> matches, MatchStage targetStage) {
        return matches.stream()
                .filter(m -> targetStage.matches(m.getStage()))
                .filter(this::isCanceled)
                .findFirst()
                .map(m -> m.getPlayer1() + " / " + m.getPlayer2())
                .orElse(null);
    }

    // Полуфинал (особая логика)
    private String detectRemovedPlayerFromSemi(List<Match> matches) {
        List<Match> semiMatches = matches.stream()
                .filter(m -> MatchStage.SEMI_FINAL.matches(m.getStage()))
                .toList();

        Match canceledSemi = semiMatches.stream()
                .filter(this::isCanceled)
                .findFirst()
                .orElse(null);

        if (canceledSemi == null) return null;

        String p1 = normalize(canceledSemi.getPlayer1());
        String p2 = normalize(canceledSemi.getPlayer2());

        Match finalMatch = matches.stream()
                .filter(m -> MatchStage.FINAL.matches(m.getStage()))
                .findFirst()
                .orElse(null);

        if (finalMatch == null) return null;

        String f1 = normalize(finalMatch.getPlayer1());
        String f2 = normalize(finalMatch.getPlayer2());

        if (!p1.equals(f1) && !p1.equals(f2)) return canceledSemi.getPlayer1();
        if (!p2.equals(f1) && !p2.equals(f2)) return canceledSemi.getPlayer2();

        return null;
    }

    private boolean isCanceled(Match m) {
        if (m.getStatus() == null) return false;
        String s = m.getStatus().toLowerCase();
        return s.contains("отмен") || s.contains("cancel");
    }

    private String normalize(String name) {
        if (name == null) return "";
        return name.toLowerCase().replace("\u00A0", " ").replaceAll("\\s+", " ").trim();
    }
}