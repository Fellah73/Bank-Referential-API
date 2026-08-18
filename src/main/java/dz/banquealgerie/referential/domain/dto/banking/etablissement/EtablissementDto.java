package dz.banquealgerie.referential.domain.dto.banking.etablissement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtablissementDto {

    @NotBlank(message = "Le code établissement est obligatoire")
    @Pattern(regexp = "^\\d{3}$", message = "Le code établissement doit contenir exactement 3 chiffres")
    private String code;

    @NotBlank(message = "L'abréviation est obligatoire")
    @Size(max = 20, message = "L'abréviation ne doit pas dépasser 20 caractères")
    private String abreviation;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(min = 3, max = 150, message = "Le libellé doit contenir entre 3 et 150 caractères")
    private String libelle;

}