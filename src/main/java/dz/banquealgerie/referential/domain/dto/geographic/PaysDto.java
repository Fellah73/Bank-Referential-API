package dz.banquealgerie.referential.domain.dto.geographic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PaysDto {
    @NotBlank(message = "Le code pays est obligatoire")
    @Pattern(regexp = "^[0-9]{3}$", message = "Le code pays doit contenir exactement 3 chiffres")
    private String code;

    @Pattern(regexp = "^[A-Z]{2}$", message = "L'abréviation doit contenir exactement 2 lettres majuscules")
    private String abreviation;

    @Size(min = 2, max = 100, message = "Le libellé doit faire entre 2 et 100 caractères")
    private String libelle;

}
