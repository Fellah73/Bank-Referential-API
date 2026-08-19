package dz.banquealgerie.referential.repositories.regulatory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.regulatory.FormeJuridiqueEntity;

@Repository
public interface FormJuridiqueRepository extends JpaRepository<FormeJuridiqueEntity, String> {

}
