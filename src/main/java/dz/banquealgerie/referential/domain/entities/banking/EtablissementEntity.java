package dz.banquealgerie.referential.domain.entities.banking;

import java.util.ArrayList;
import java.util.List;

import dz.banquealgerie.referential.domain.entities.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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

    @OneToMany(mappedBy = "etablissement", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AgenceEntity> agences = new ArrayList<>();

}