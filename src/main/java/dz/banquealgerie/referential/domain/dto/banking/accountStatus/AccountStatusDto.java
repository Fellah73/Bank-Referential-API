package dz.banquealgerie.referential.domain.dto.banking.accountStatus;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AccountStatusDto extends AccountStatusUpdateDto {
    @NotBlank(message = "Le code pays est obligatoire")
    @Pattern(regexp = "^[0-9]{2}$", message = "Le code pays doit contenir exactement 2 chiffres")
    private String code;

    @Nullable
    private Boolean isActive;
}
