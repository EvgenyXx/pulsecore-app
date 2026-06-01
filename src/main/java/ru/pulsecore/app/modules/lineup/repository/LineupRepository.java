package ru.pulsecore.app.modules.lineup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pulsecore.app.modules.lineup.domain.Lineup;

import java.time.LocalDate;
import java.util.List;

public interface LineupRepository extends JpaRepository<Lineup, Long> {

    @Modifying
    @Query(value = """
    INSERT INTO lineup (date, league, time, hall, players)
    VALUES (:date, :league, :time, :hall, :players)
    ON CONFLICT (league, time, date, hall) DO UPDATE 
        SET players = EXCLUDED.players
""", nativeQuery = true)
    void upsertLineup(@Param("date") LocalDate date,
                      @Param("league") String league,
                      @Param("time") String time,
                      @Param("hall") String hall,
                      @Param("players") String players);

    List<Lineup> findByDate(LocalDate date);

    List<Lineup> findByDateBetweenOrderByDateAscTimeAsc(LocalDate start, LocalDate end);

    // Новый метод — поиск по дате и списку залов
    @Query("SELECT l FROM Lineup l WHERE l.date = :date AND l.hall IN :halls ORDER BY l.time")
    List<Lineup> findByDateAndHallIn(@Param("date") LocalDate date, @Param("halls") List<String> halls);

    @Modifying
    @Query("DELETE FROM Lineup WHERE date = :date")
    void deleteAllByDate(@Param("date") LocalDate date);

    @Modifying
    @Query("DELETE FROM Lineup WHERE date < :date")
    void deleteByDateBefore(@Param("date") LocalDate date);
}