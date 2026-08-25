package dz.banquealgerie.referential.domain.dto.regulatory.nif;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NifDto extends NifUpdateDto {
    @NotBlank(message = "Le NIF est obligatoire")
    @Size(min = 15, max = 20, message = "Le NIF doit contenir entre 15 et 20 caractères")
    private String nif;

    private LocalDate dateCreation;

    @Nullable
    private Boolean isActive;
}
