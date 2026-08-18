package dz.banquealgerie.referential.domain.dto.banking.agence;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AgenceUpdateDto {
     @Pattern(regexp = "^[\\p{L}\\s'-]{4,150}$", message = "La libellé doit contenir entre 4 et 150 lettres")
    private String libelle;
}
