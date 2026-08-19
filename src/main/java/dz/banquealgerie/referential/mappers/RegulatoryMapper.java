package dz.banquealgerie.referential.mappers;

import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;

import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.entities.regulatory.FormeJuridiqueEntity;

@Component
public class RegulatoryMapper {
    // form-juriduque methods
    public FormeJuridiqueEntity toFormeJuridiqueEntity(FormeJuridiqueDto dto) {
        return FormeJuridiqueEntity.builder()
                .code(dto.getCode())
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public FormeJuridiqueDto toFormeJuridiqueDto(FormeJuridiqueEntity dto) {
        FormeJuridiqueDto formeJuridiqueDto = new FormeJuridiqueDto();
        formeJuridiqueDto.setCode(dto.getCode());
        formeJuridiqueDto.setAbreviation(dto.getAbreviation());
        formeJuridiqueDto.setLibelle(dto.getLibelle());
        return formeJuridiqueDto;
    }

    public FormeJuridiqueEntity toFormeJuridiqueEntity(FormeJuridiqueUpdateDto dto) {
        FormeJuridiqueEntity entity = new FormeJuridiqueEntity();
        entity.setAbreviation(dto.getAbreviation());
        entity.setLibelle(dto.getLibelle());
        return entity;
    }

    public Page<FormeJuridiqueDto> toFormeJuridiqueDtoList(Page<FormeJuridiqueEntity> entityList) {
        return entityList.map(this::toFormeJuridiqueDto);
    }
}
