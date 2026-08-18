package dz.banquealgerie.referential.domain.dto.banking.agence;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AgenceDto extends AgenceUpdateDto {
    @NotBlank(message = "Le code wilaya est obligatoire")
    @Pattern(regexp = "^[0-9]{3,20}$", message = "Le code wilaya doit contenir entre 3 et 20 chiffres")
    private String code;

    @Nullable
    private String etablissementCode;
    
}
