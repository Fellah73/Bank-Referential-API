package dz.banquealgerie.referential.mappers;

import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;

import dz.banquealgerie.referential.domain.dto.regulatory.activity.ActivityDto;
import dz.banquealgerie.referential.domain.dto.regulatory.activity.ActivityUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.formeJuridique.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.formeJuridique.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifResponseDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifUpdateDto;
import dz.banquealgerie.referential.domain.entities.regulatory.ActivityEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.FormeJuridiqueEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.NifEntity;

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
        formeJuridiqueDto.setIsActive(dto.getIsActive());
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

    // activity methods
    public ActivityEntity toActivityEntity(ActivityDto dto) {
        return ActivityEntity.builder()
                .code(dto.getCode())
                .codeSection(dto.getCodeSection())
                .domaine(dto.getDomaine())
                .libelle(dto.getLibelle())
                .build();
    }

    public ActivityEntity toActivityEntity(ActivityUpdateDto dto) {
        return ActivityEntity.builder()
                .codeSection(dto.getCodeSection())
                .domaine(dto.getDomaine())
                .libelle(dto.getLibelle())
                .build();
    }

    public ActivityDto toActivityDto(ActivityEntity entity) {
        ActivityDto activityDto = new ActivityDto();
        activityDto.setCode(entity.getCode());
        activityDto.setCodeSection(entity.getCodeSection());
        activityDto.setDomaine(entity.getDomaine());
        activityDto.setLibelle(entity.getLibelle());
        activityDto.setIsActive(entity.getIsActive());
        return activityDto;
    }

    public Page<ActivityDto> toActivityDtoList(Page<ActivityEntity> entityList) {
        return entityList.map(this::toActivityDto);
    }

    // nif methods
    public NifEntity toNifEntity(NifDto dto) {
        return NifEntity.builder()
                .nif(dto.getNif())
                .rc(dto.getRc())
                .activity(ActivityEntity.builder().code(dto.getActivityCode()).build())
                .dateCreation(dto.getDateCreation())
                .designation(dto.getDesignation())
                .formeJuridique(FormeJuridiqueEntity.builder().code(dto.getFormeJuridiqueCode()).build())
                .build();
    }

    public NifEntity toNifEntity(NifUpdateDto dto) {
        return NifEntity.builder()
                .rc(dto.getRc())
                .activity(ActivityEntity.builder().code(dto.getActivityCode()).build())
                .designation(dto.getDesignation())
                .formeJuridique(FormeJuridiqueEntity.builder().code(dto.getFormeJuridiqueCode()).build())
                .build();
    }

    public NifDto toNifDto(NifEntity entity) {
        NifDto nifDto = new NifDto();
        nifDto.setNif(entity.getNif());
        nifDto.setRc(entity.getRc());
        nifDto.setActivityCode(entity.getActivity().getCode());
        nifDto.setDateCreation(entity.getDateCreation());
        nifDto.setDesignation(entity.getDesignation());
        nifDto.setFormeJuridiqueCode(entity.getFormeJuridique().getCode());
        return nifDto;
    }

    public NifResponseDto toNifResponseDto(NifEntity entity) {
        if (entity == null) {
            return null;
        }

        NifResponseDto dto = new NifResponseDto();
        dto.setNif(entity.getNif());
        dto.setRc(entity.getRc());
        dto.setDateCreation(entity.getDateCreation());
        dto.setDesignation(entity.getDesignation());
        dto.setIsActive(entity.getIsActive());

        if (entity.getActivity() != null) {
            dto.setActivity(toActivityDto(entity.getActivity()));
        }

        if (entity.getFormeJuridique() != null) {
            dto.setFormeJuridique(toFormeJuridiqueDto(entity.getFormeJuridique()));
        }

        return dto;
    }

    public Page<NifResponseDto> toNifResponseDtoList(Page<NifEntity> entityList) {
        return entityList.map(this::toNifResponseDto);
    }
}
