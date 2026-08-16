package dz.banquealgerie.referential.domain.dto.geographic.wilaya;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class WilayaDto {
    @NotBlank(message = "Le code wilaya est obligatoire")
    @Pattern(regexp = "^[0-9]{2}$", message = "Le code wilaya doit contenir exactement 2 chiffres")
    private String code;

    @Pattern(regexp = "^[a-zA-Z\\s]{4,100}$", message = "La libellé doit contenir entre 4 et 100 lettres")
    private String libelle;

    @Pattern(regexp = "^[\\p{IsArabic}\\s]{4,100}$", message = "Le libellé doit contenir entre 4 et 100 lettres arabes")
    private String libelleAr;

}
