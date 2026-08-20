package dz.banquealgerie.referential.repositories.regulatory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.regulatory.NifEntity;

@Repository
public interface NifRepository extends JpaRepository<NifEntity, String> {
    NifEntity findByNif(String nif);

    @Query("SELECT n FROM NifEntity n JOIN FETCH n.activity JOIN FETCH n.formeJuridique")
    List<NifEntity> findAllWithRelations();
}
