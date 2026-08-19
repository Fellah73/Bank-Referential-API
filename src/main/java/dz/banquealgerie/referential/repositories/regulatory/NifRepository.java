package dz.banquealgerie.referential.repositories.regulatory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.regulatory.NifEntity;

@Repository
public interface NifRepository extends JpaRepository<NifEntity, String>{
    NifEntity findByNif(String nif);
}
