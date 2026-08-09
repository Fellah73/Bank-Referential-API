package dz.banquealgerie.referential.repositories.geographic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.geographic.WilayaEntity;

@Repository
public interface WilayaRepository extends JpaRepository<WilayaEntity, String> {
    // later
}
