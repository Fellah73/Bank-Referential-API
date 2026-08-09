package dz.banquealgerie.referential.domain.entities.geographic;

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
@Table(name = "pays")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaysEntity extends BaseEntity {

    @Id
    @Column(name = "code", length = 3, nullable = false)
    private String code;

    @Column(name = "abreviation", length = 2)
    private String abreviation;

    @Column(name = "libelle", nullable = false, length = 150)
    private String libelle;
}
