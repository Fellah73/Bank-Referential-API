package dz.banquealgerie.referential.domain.dto.geographic;

import org.jspecify.annotations.Nullable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PaysUpdateDto {
    @Nullable
    private String abreviation;

    @Nullable
    private String libelle;
}
