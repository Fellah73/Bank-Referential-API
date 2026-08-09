package dz.banquealgerie.referential.mappers;

import org.mapstruct.Mapper;

import dz.banquealgerie.referential.domain.dto.geographic.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.PaysUpdateDto;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;

@Mapper(componentModel = "spring")
public interface GeographicMapper {

    PaysEntity toPaysEntity(PaysDto dto);

    PaysEntity toPaysEntity(PaysUpdateDto dto); 

    PaysDto toPaysDto(PaysEntity entity);
}
