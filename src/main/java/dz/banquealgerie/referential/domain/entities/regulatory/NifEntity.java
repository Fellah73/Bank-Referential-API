package dz.banquealgerie.referential.domain.entities.regulatory;

import java.time.LocalDate;

import dz.banquealgerie.referential.domain.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "nifs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NifEntity extends BaseEntity {
    @Id
    @Column(name = "nif", length = 20, nullable = false)
    private String nif;

    @Column(name = "rc", length = 50)
    private String rc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity", referencedColumnName = "code", nullable = false)
    private ActivityEntity activity;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @Column(name = "designation", length = 255, nullable = false)
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forme_juridique", referencedColumnName = "code", nullable = false)
    private FormeJuridiqueEntity formeJuridique;
}
