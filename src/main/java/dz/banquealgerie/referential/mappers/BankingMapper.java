package dz.banquealgerie.referential.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementUpdateDto;
import dz.banquealgerie.referential.domain.entities.banking.EtablissementEntity;

@Component
public class BankingMapper {

    // etablisselent methods
    public EtablissementEntity toEtablissementEntity(EtablissementDto dto) {
        return EtablissementEntity.builder()
                .code(dto.getCode())
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public EtablissementEntity toEtablissementEntity(EtablissementUpdateDto dto) {
        return EtablissementEntity.builder()
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public EtablissementDto toEtablissementDto(EtablissementEntity etablissement) {
        EtablissementDto dto = new EtablissementDto();
        dto.setCode(etablissement.getCode());
        dto.setAbreviation(etablissement.getAbreviation());
        dto.setLibelle(etablissement.getLibelle());

        return dto;
    }

    public Page<EtablissementDto> toEtablissementDtoList(Page<EtablissementEntity> entities) {
        return entities.map(this::toEtablissementDto);
    }

}