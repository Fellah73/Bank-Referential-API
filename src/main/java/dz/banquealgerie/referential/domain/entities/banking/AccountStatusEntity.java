package dz.banquealgerie.referential.domain.entities.banking;

import dz.banquealgerie.referential.domain.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "situations_comptes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountStatusEntity extends BaseEntity {
    @Id
    @Column(name = "code", length = 2, nullable = false)
    private String code;

    @Column(name = "libelle", length = 4, nullable = false)
    private String libelle;

}
