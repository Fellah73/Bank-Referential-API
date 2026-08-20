package dz.banquealgerie.referential.repositories.banking;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.banking.AgenceEntity;

@Repository
public interface AgenceRepository extends JpaRepository<AgenceEntity, String> {
    Page<AgenceEntity> findByEtablissementCode(String etablissementCode, Pageable pageable);

    List<AgenceEntity> findByEtablissementCode(String etablissementCode);
}
