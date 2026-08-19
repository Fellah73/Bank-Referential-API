package dz.banquealgerie.referential.repositories.regulatory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.regulatory.ActivityEntity;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, String>{
    ActivityEntity findByCode(String code);
}
