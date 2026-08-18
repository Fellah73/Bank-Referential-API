package dz.banquealgerie.referential.domain.dto.banking.etablissement;

import org.jspecify.annotations.Nullable;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EtablissementUpdateDto {
     @Nullable
    @Pattern(regexp = "^[A-Z]{20}$", message = "L'abréviation doit contenir exactement 20 lettres majuscules")
    private String abreviation;

    @Nullable
    @Size(min = 3, max = 100, message = "Le libellé doit faire entre 3 et 100 caractères")
    private String libelle;
}
