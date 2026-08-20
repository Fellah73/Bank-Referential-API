package dz.banquealgerie.referential.domain.dto.regulatory.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto extends ActivityUpdateDto {
    @NotBlank(message = "Le code est obligatoire")
    @Pattern(regexp = "^\\d{6}$", message = "Le code activité doit contenir exactement 6 chiffres")
    private String code;

}
