package dz.banquealgerie.referential.domain.entities.banking;

import dz.banquealgerie.referential.domain.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "agences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AgenceEntity extends BaseEntity {
    @Id
    @Column(name = "code", length = 10, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_ed", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EtablissementEntity etablissement;

    @Column(name = "libelle", length = 150, nullable = false)
    private String libelle;

}
