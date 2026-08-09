package dz.banquealgerie.referential.repositories.geographic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;

@Repository
public interface PaysRepository extends JpaRepository<PaysEntity, String> {
   PaysEntity findPaysByCode(String code);
}
