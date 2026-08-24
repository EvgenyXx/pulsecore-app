//package ru.pulsecore.app.tournament.application.event;
//
//import lombok.Getter;
//import org.springframework.context.ApplicationEvent;
//import ru.pulsecore.app.tournament.domain.model.Match;
//
//import java.util.List;
//
//@Getter
//public class MatchesCalculatedEvent extends ApplicationEvent {
//
//    private final Long tournamentId;
//    private final List<Match> matches;
//    private final String tournamentDate;  // String
//
//    public MatchesCalculatedEvent(Object source, Long tournamentId, List<Match> matches, String tournamentDate) {
//        super(source);
//        this.tournamentId = tournamentId;
//        this.matches = matches;
//        this.tournamentDate = tournamentDate;
//    }
//}