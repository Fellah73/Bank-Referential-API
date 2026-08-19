package dz.banquealgerie.referential.domain.entities.regulatory;

import java.util.ArrayList;
import java.util.List;

import dz.banquealgerie.referential.domain.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "formes_juridiques")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FormeJuridiqueEntity extends BaseEntity {
    @Id
    @Column(name = "code", length = 2, nullable = false)
    private String code;

    @Column(name = "abreviation", length = 5)
    private String abreviation;

    @Column(name = "libelle", length = 100, nullable = false)
    private String libelle;

    @OneToMany(mappedBy = "formeJuridique", fetch = FetchType.LAZY)
    @Builder.Default
    private List<NifEntity> nifs = new ArrayList<>();

}
