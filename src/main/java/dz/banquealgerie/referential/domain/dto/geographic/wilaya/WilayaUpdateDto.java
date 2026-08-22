package dz.banquealgerie.referential.domain.dto.geographic.wilaya;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class WilayaUpdateDto {
    @Nullable
    @Pattern(regexp = "^[\\p{L}\\s'-]{4,100}$", message = "La libellé doit contenir entre 4 et 100 lettres")
    private String libelle;

    
    @Nullable
    @Pattern(regexp = "^[\\p{IsArabic}\\s]{4,100}$", message = "Le libellé doit contenir entre 4 et 100 lettres arabes")
    private String libelleAr;
}
