package dz.banquealgerie.referential.domain.dto.geographic;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CommuneDto {
    private String code;
    private String libelle;
    private String libelleAr;

    private String codeWilaya;
}
