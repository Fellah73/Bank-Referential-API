package dz.banquealgerie.referential.domain.dto.banking.accountStatus;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AccountStatusUpdateDto {
    
    @Pattern(regexp = "^[A-Z]{4}$", message = "Le libellé doit contenir exactement 4 lettres majuscules")
    private String libelle;
}
