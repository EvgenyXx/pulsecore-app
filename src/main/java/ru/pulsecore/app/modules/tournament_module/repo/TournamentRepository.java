package ru.pulsecore.app.modules.tournament_module.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.modules.tournament_module.entity.TournamentEntity;


import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {

    Optional<TournamentEntity> findByExternalId(Long externalId);


}