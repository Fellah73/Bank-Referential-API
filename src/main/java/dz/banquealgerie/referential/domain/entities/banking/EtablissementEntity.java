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
@Table(name = "etablissements_declarants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EtablissementEntity extends BaseEntity {

    @Id
    @Column(name = "code", length = 3, nullable = false)
    private String code;

    @Column(name = "abreviation", length = 20, nullable = false)
    private String abreviation;

    @Column(name = "libelle", length = 150, nullable = false)
    private String libelle;

}