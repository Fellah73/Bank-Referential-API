package dz.banquealgerie.referential.domain.entities.regulatory;

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
@Table(name = "activites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ActivityEntity extends BaseEntity {
    @Id
    @Column(name = "code", length = 6, nullable = false)
    private String code;

    @Column(name = "code_section", length = 1, nullable = false)
    private String codeSection;

    @Column(name = "domaine", length = 150, nullable = false)
    private String domaine;

    @Column(name = "libelle", length = 255, nullable = false)
    private String libelle;

}
