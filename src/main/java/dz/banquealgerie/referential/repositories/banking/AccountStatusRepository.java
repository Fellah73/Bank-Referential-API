package dz.banquealgerie.referential.repositories.banking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dz.banquealgerie.referential.domain.entities.banking.AccountStatusEntity;

@Repository
public interface AccountStatusRepository extends JpaRepository<AccountStatusEntity, String> {

}
