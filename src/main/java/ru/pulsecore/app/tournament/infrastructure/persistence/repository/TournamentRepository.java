package ru.pulsecore.app.tournament.infrastructure.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.infrastructure.persistence.entity.TournamentEntity;


import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {

    Optional<TournamentEntity> findByExternalId(Long externalId);


}