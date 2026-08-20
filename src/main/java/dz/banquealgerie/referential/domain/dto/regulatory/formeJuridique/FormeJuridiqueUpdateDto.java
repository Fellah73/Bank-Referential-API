package dz.banquealgerie.referential.domain.dto.regulatory.formeJuridique;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormeJuridiqueUpdateDto {
    @Size(min = 2, max = 5, message = "L'abréviation doit contenir entre 2 et 5 caractères")
    @Pattern(regexp = "^[A-Z0-9.-]{2,5}$", message = "L'abréviation doit être en majuscules")
    private String abreviation;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(min = 3, max = 100, message = "Le libellé doit contenir entre 3 et 100 caractères")
    private String libelle;
}
