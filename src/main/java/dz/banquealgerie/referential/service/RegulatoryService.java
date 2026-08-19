package dz.banquealgerie.referential.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.banquealgerie.referential.domain.dto.regulatory.ActivityDto;
import dz.banquealgerie.referential.domain.dto.regulatory.ActivityUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifResponseDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;

public interface RegulatoryService {
    // legal form routes
    FormeJuridiqueDto createLegalForm(FormeJuridiqueDto formeJuridiqueDto, UserEntity currentUser);

    Page<FormeJuridiqueDto> getAllLegalForms(Pageable pageable);

    FormeJuridiqueDto updateLegalForm(String code, FormeJuridiqueUpdateDto formeJuridiqueToUpdateDTO,
            UserEntity currentUser);

    void changeLegalFormStatus(String code, UserEntity currentUser);

    // activity routes
    ActivityDto createActivity(ActivityDto activityToCreateDTO, UserEntity currentUser);

    Page<ActivityDto> getAllActivities(Pageable pageable);

    ActivityDto updateActivity(String code, ActivityUpdateDto activityToUpdateDTO, UserEntity currentUser);

    void changeActivityStatus(String code, UserEntity currentUser);

    // nif routes
    NifDto createNif(NifDto nifToCreateDTO, UserEntity currentUser);

    Page<NifResponseDto> getAllNifs(Pageable pageable);

    NifResponseDto updateNif(String code, NifUpdateDto nifToUpdateDTO, UserEntity currentUser);

    void changeNifStatus(String code, UserEntity currentUser);
}
