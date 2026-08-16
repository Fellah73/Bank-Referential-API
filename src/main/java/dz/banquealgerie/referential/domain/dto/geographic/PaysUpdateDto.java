package dz.banquealgerie.referential.domain.dto.geographic;

import org.jspecify.annotations.Nullable;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PaysUpdateDto {
    @Nullable
    @Pattern(regexp = "^[A-Z]{2}$", message = "L'abréviation doit contenir exactement 2 lettres majuscules")
    private String abreviation;

    @Nullable
    @Size(min = 3, max = 100, message = "Le libellé doit faire entre 3 et 100 caractères")
    private String libelle;
}
