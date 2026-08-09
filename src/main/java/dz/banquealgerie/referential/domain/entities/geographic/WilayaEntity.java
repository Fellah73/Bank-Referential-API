package dz.banquealgerie.referential.domain.entities.geographic;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import dz.banquealgerie.referential.domain.entities.BaseEntity;

@Entity
@Table(name = "wilayas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WilayaEntity extends BaseEntity {

    @Id
    @Column(name = "code", length = 10, nullable = false)
    private String code;

    @Column(name = "libelle", nullable = false, length = 150)
    private String libelle;

    @Column(name = "libelle_ar", length = 150)
    private String libelleAr;

    @OneToMany(mappedBy = "wilaya", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommuneEntity> communes = new ArrayList<>();
}
