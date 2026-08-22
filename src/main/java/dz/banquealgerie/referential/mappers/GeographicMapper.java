package dz.banquealgerie.referential.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaDto;
import dz.banquealgerie.referential.domain.entities.geographic.CommuneEntity;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;
import dz.banquealgerie.referential.domain.entities.geographic.WilayaEntity;

@Component
public class GeographicMapper {

    // pays methods
    public PaysEntity toPaysEntity(PaysDto dto) {
        return PaysEntity.builder()
                .code(dto.getCode())
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public PaysEntity toPaysEntity(PaysUpdateDto dto) {
        return PaysEntity.builder()
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public PaysDto toPaysDto(PaysEntity entity) {
        PaysDto dto = new PaysDto();
        dto.setCode(entity.getCode());
        dto.setAbreviation(entity.getAbreviation());
        dto.setLibelle(entity.getLibelle());
        dto.setIsActive(entity.getIsActive());

        return dto;
    }

    public Page<PaysDto> toPaysDtoList(Page<PaysEntity> entities) {
        return entities.map(this::toPaysDto);
    }

    // wilaya methods
    public WilayaEntity toWilayaEntity(WilayaDto dto) {
        return WilayaEntity.builder()
                .code(dto.getCode())
                .libelle(dto.getLibelle())
                .libelleAr(dto.getLibelleAr())
                .build();
    }

    public WilayaDto toWilayaDto(WilayaEntity entity) {
        WilayaDto dto = new WilayaDto();
        dto.setCode(entity.getCode());
        dto.setLibelle(entity.getLibelle());
        dto.setLibelleAr(entity.getLibelleAr());
        dto.setIsActive(entity.getIsActive());
        
        return dto;
    }

    public Page<WilayaDto> toWilayaDtoList(Page<WilayaEntity> entities) {
        return entities.map(this::toWilayaDto);
    }

    // commune methods
    public CommuneEntity toCommuneEntity(CommuneDto dto) {
        CommuneEntity entity = new CommuneEntity();
        entity.setCode(dto.getCode());
        entity.setLibelle(dto.getLibelle());
        entity.setLibelleAr(dto.getLibelleAr());
        return entity;
    }

    public CommuneDto toCommuneDto(CommuneEntity entity) {
        CommuneDto dto = new CommuneDto();
        dto.setCode(entity.getCode());
        dto.setLibelle(entity.getLibelle());
        dto.setLibelleAr(entity.getLibelleAr());
        dto.setWilayaCode(entity.getWilaya().getCode());

        return dto;
    }

    public Page<CommuneDto> toCommuneDtoList(Page<CommuneEntity> entities) {
        return entities.map(this::toCommuneDto);
    }
}
