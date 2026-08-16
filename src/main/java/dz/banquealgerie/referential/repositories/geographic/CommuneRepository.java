package dz.banquealgerie.referential.repositories.geographic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.geographic.CommuneEntity;

@Repository
public interface CommuneRepository extends JpaRepository<CommuneEntity, String> {
    // later
    Page<CommuneEntity> findByWilayaCode(String wilayaCode, Pageable pageable);
}
