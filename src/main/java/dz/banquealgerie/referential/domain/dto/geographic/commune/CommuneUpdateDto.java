package dz.banquealgerie.referential.domain.dto.geographic.commune;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CommuneUpdateDto {
    
    @Pattern(regexp = "^[\\p{L}\\s'-]{4,100}$", message = "La libellé doit contenir entre 4 et 100 lettres")
    private String libelle;

    @Pattern(regexp = "^[\\p{IsArabic}\\s]{4,100}$", message = "Le libellé doit contenir entre 4 et 100 lettres arabes")
    private String libelleAr;
}
