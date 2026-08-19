package dz.banquealgerie.referential.domain.dto.regulatory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormeJuridiqueDto extends FormeJuridiqueUpdateDto {
    @NotBlank(message = "Le code est obligatoire")
    @Pattern(regexp = "^\\d{2}$", message = "Le code doit contenir exactement 2 chiffres")
    private String code;

}
