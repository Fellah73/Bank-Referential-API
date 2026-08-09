package dz.banquealgerie.referential.domain.entities.geographic;

import dz.banquealgerie.referential.domain.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "communes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommuneEntity extends BaseEntity {

    @Id
    @Column(name = "code", length = 10, nullable = false)
    private String code;

    @Column(name = "libelle", nullable = false, length = 150)
    private String libelle;

    @Column(name = "libelle_ar", length = 150)
    private String libelleAr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_wilaya", nullable = false)
    private WilayaEntity wilaya;
}
