package dz.banquealgerie.referential.domain.dto.banking.agence;

import java.io.ByteArrayInputStream;

public record AgenceExcelExportDto(ByteArrayInputStream stream, String fileName) {
}
