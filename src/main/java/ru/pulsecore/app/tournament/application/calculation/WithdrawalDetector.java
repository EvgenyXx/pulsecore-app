package ru.pulsecore.app.tournament.application.calculation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.enums.MatchNumber;
import ru.pulsecore.app.tournament.domain.enums.WithdrawalStage;
import ru.pulsecore.app.tournament.domain.model.Match;

import java.util.List;

@Slf4j
@Component
public class WithdrawalDetector {

    /**
     * Возвращает стадию снятия или null, если никто не снялся.
     * Снятие определяется по первому canceled-матчу.
     */
    public WithdrawalStage detect(List<Match> matches) {
        // 1. Снятие в играх 1–6
        for (Match m : matches) {
            if (m.isCanceled() && number(m).isEarly()) {
                return WithdrawalStage.BEFORE_PLAYOFF;
            }
        }

        // 2. Снятие в полуфинале (7-я)
        for (Match m : matches) {
            if (m.isCanceled() && number(m).isSemifinal()) {
                return WithdrawalStage.PLAYOFF;
            }
        }

        return WithdrawalStage.NONE;
    }

    private MatchNumber number(Match m) {
        return MatchNumber.fromNumber(m.getSortNumber());
    }
}