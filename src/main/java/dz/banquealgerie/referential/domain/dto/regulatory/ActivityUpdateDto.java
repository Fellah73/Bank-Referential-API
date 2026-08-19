package dz.banquealgerie.referential.domain.dto.regulatory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUpdateDto {
    @NotBlank(message = "Le code section est obligatoire")
    @Pattern(regexp = "^[1-9]$", message = "Le code section doit être un seul chiffre")
    private String codeSection;

    @NotBlank(message = "Le domaine est obligatoire")
    @Size(min = 2, max = 150, message = "Le domaine doit contenir entre 2 et 150 caractères")
    private String domaine;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(min = 3, max = 255, message = "Le libellé doit contenir entre 3 et 255 caractères")
    private String libelle;
}
