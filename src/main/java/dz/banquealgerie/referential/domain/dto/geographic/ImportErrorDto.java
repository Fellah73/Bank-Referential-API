package dz.banquealgerie.referential.domain.dto.geographic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportErrorDto {
    private int rowNumber;
    private String identifier;
    private String errorMessage;
}
