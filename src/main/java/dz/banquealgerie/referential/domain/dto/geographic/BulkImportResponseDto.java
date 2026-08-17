package dz.banquealgerie.referential.domain.dto.geographic;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResponseDto {
    private int totalRows;
    private int successCount;
    private int errorCount;

    @Builder.Default
    private List<ImportErrorDto> errors = new ArrayList<>();
}
