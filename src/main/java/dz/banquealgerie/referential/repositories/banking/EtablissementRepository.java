package dz.banquealgerie.referential.repositories.banking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.banking.EtablissementEntity;

@Repository
public interface EtablissementRepository extends JpaRepository<EtablissementEntity, String> {
  EtablissementEntity  findByCode(String code);
}
