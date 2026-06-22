package ru.pulsecore.app.modules.lineup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.modules.lineup.domain.HallStream;


@Repository
public interface HallStreamRepository extends JpaRepository<HallStream, String> {

    @Query("SELECT h.streamUrl FROM HallStream h WHERE h.hall = :hall")
    String findStreamUrlByHall(@Param("hall") String hall);
}