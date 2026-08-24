package dz.banquealgerie.referential.service;

import java.io.ByteArrayInputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import dz.banquealgerie.referential.domain.dto.geographic.BulkImportResponseDto;
import dz.banquealgerie.referential.domain.dto.regulatory.activity.ActivityDto;
import dz.banquealgerie.referential.domain.dto.regulatory.activity.ActivityUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.formeJuridique.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.formeJuridique.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifResponseDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;

public interface RegulatoryService {
    // legal form routes
    FormeJuridiqueDto createLegalForm(FormeJuridiqueDto formeJuridiqueDto, UserEntity currentUser);

    Page<FormeJuridiqueDto> getAllLegalForms(Pageable pageable);

    FormeJuridiqueDto updateLegalForm(String code, FormeJuridiqueUpdateDto formeJuridiqueToUpdateDTO,
            UserEntity currentUser);

    void changeLegalFormStatus(String code, UserEntity currentUser);

    Page<FormeJuridiqueDto> searchLegalForms(String searchTerm, Pageable pageable);

    // activity routes
    ActivityDto createActivity(ActivityDto activityToCreateDTO, UserEntity currentUser);

    Page<ActivityDto> getAllActivities(Pageable pageable);

    ActivityDto updateActivity(String code, ActivityUpdateDto activityToUpdateDTO, UserEntity currentUser);

    void changeActivityStatus(String code, UserEntity currentUser);

    Page<ActivityDto> searchActivities(String searchTerm, Pageable pageable);

    BulkImportResponseDto importActivitiesBulk(MultipartFile file, UserEntity currentUser, boolean updateIfExists);

    ByteArrayInputStream exportActivitiesToExcel();

    // nif routes
    NifDto createNif(NifDto nifToCreateDTO, UserEntity currentUser);

    Page<NifResponseDto> getAllNifs(Pageable pageable);

    NifResponseDto updateNif(String code, NifUpdateDto nifToUpdateDTO, UserEntity currentUser);

    void changeNifStatus(String code, UserEntity currentUser);

    BulkImportResponseDto importNifsBulk(MultipartFile file, UserEntity currentUser, boolean updateIfExists);

    ByteArrayInputStream exportNifsToExcel();
}
