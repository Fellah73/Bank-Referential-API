package dz.banquealgerie.referential.domain.dto.regulatory.nif;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NifUpdateDto {
    @Size(max = 50, message = "Le registre du commerce ne doit pas dépasser 50 caractères")
    private String rc;

    @NotBlank(message = "La désignation est obligatoire")
    @Size(min = 2, max = 255, message = "La désignation doit contenir entre 2 et 255 caractères")
    private String designation;

    @NotBlank(message = "Le code activité est obligatoire")
    @Pattern(regexp = "^\\d{6}$", message = "Le code activité doit contenir 6 chiffres")
    private String activityCode;

    @NotBlank(message = "Le code de forme juridique est obligatoire")
    @Pattern(regexp = "^\\d{2}$", message = "Le code de forme juridique doit contenir 2 chiffres")
    private String formeJuridiqueCode;

}
