package dz.banquealgerie.referential.domain.dto.geographic.commune;

import java.io.ByteArrayInputStream;

public record CommuneExcelExportDto(ByteArrayInputStream stream, String fileName){}
