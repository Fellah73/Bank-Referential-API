package dz.banquealgerie.referential.domain.dto.geographic.commune;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CommuneDto extends CommuneUpdateDto {

    @NotBlank(message = "Le code wilaya est obligatoire")
    @Pattern(regexp = "^[0-9]{4}$", message = "Le code wilaya doit contenir exactement 4 chiffres")
    private String code;

}
