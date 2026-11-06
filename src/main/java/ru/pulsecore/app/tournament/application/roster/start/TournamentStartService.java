package ru.pulsecore.app.tournament.application.roster.start;

/**
 * Сервис для проверки старта турниров.
 * Реализации (выбираются через Spring-профили):
 *  - "start-api" : ApiTournamentStartService (парсит статус через API)
 *  - "start-db"  : DatabaseTournamentStartService (проверяет только время из БД)
 */
public interface TournamentStartService {
    void checkStart();
}