package dz.banquealgerie.referential.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dz.banquealgerie.referential.domain.dto.geographic.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.PaysUpdateDto;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;

@Component
public class GeographicMapper {

    public PaysEntity toPaysEntity(PaysDto dto){
        return PaysEntity.builder()
                .code(dto.getCode())
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public PaysEntity toPaysEntity(PaysUpdateDto dto){
        return PaysEntity.builder()
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public PaysDto toPaysDto(PaysEntity entity){
        PaysDto dto = new PaysDto();
        dto.setCode(entity.getCode());
        dto.setAbreviation(entity.getAbreviation());
        dto.setLibelle(entity.getLibelle());

        return dto;
    }

    public Page<PaysDto> toPaysDtoList(Page<PaysEntity> entities){
        return entities.map(this::toPaysDto);
    }
}
