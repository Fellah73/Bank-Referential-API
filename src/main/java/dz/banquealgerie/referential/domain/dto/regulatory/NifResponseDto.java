package dz.banquealgerie.referential.domain.dto.regulatory;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NifResponseDto {
    private String nif;
    private String rc;
    private LocalDate dateCreation;
    private String designation;
    private Boolean isActive;

    private ActivityDto activity;
    private FormeJuridiqueDto formeJuridique;
}
