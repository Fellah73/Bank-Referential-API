package dz.banquealgerie.referential.domain.dto.geographic;

import io.micrometer.common.lang.Nullable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PaysDto {
    @Nullable
    private String code;

    private String abreviation;

    private String libelle;

}
