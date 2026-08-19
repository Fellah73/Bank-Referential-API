package dz.banquealgerie.referential.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneDto;
import dz.banquealgerie.referential.domain.dto.regulatory.ActivityDto;
import dz.banquealgerie.referential.domain.dto.regulatory.ActivityUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifResponseDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.geographic.CommuneEntity;
import dz.banquealgerie.referential.domain.entities.geographic.WilayaEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.ActivityEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.FormeJuridiqueEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.NifEntity;
import dz.banquealgerie.referential.mappers.RegulatoryMapper;
import dz.banquealgerie.referential.repositories.regulatory.ActivityRepository;
import dz.banquealgerie.referential.repositories.regulatory.FormJuridiqueRepository;
import dz.banquealgerie.referential.repositories.regulatory.NifRepository;
import dz.banquealgerie.referential.service.RegulatoryService;

@Service
public class RegulatoryServiceImpl implements RegulatoryService {
    private final RegulatoryMapper regulatoryMapper;

    private final FormJuridiqueRepository formJuridiqueRepository;
    private final ActivityRepository activityRepository;
    private final NifRepository nifRepository;

    public RegulatoryServiceImpl(FormJuridiqueRepository formJuridiqueRepository, RegulatoryMapper regulatoryMapper,
            ActivityRepository activityRepository, NifRepository nifRepository) {
        this.formJuridiqueRepository = formJuridiqueRepository;
        this.regulatoryMapper = regulatoryMapper;
        this.activityRepository = activityRepository;
        this.nifRepository = nifRepository;
    }

    // form-juridique methods

    // create form juridique
    @Override
    public FormeJuridiqueDto createLegalForm(FormeJuridiqueDto formeJuridiqueDto, UserEntity currentUser) {

        try {

            FormeJuridiqueEntity formeJuridique = regulatoryMapper.toFormeJuridiqueEntity(formeJuridiqueDto);

            FormeJuridiqueEntity existingFormeJuridique = formJuridiqueRepository.findById(formeJuridique.getCode())
                    .orElse(null);
            if (existingFormeJuridique != null) {
                throw new RuntimeException("Forme juridique with code " + formeJuridique.getCode() + " already exists");
            }

            FormeJuridiqueEntity formeJuridiqueToSave = FormeJuridiqueEntity.builder()
                    .code(formeJuridique.getCode())
                    .abreviation(formeJuridique.getAbreviation())
                    .libelle(formeJuridique.getLibelle())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            formJuridiqueRepository.save(formeJuridiqueToSave);

            return regulatoryMapper.toFormeJuridiqueDto(formeJuridiqueToSave);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all legal forms with pagination
    @Override
    public Page<FormeJuridiqueDto> getAllLegalForms(Pageable pageable) {
        try {
            Page<FormeJuridiqueEntity> legalFormsList = formJuridiqueRepository.findAll(pageable);
            Page<FormeJuridiqueDto> legalFormsDtoList = regulatoryMapper.toFormeJuridiqueDtoList(legalFormsList);
            return legalFormsDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update legal form
    @Override
    public FormeJuridiqueDto updateLegalForm(String code, FormeJuridiqueUpdateDto formeJuridiqueToUpdateDTO,
            UserEntity currentUser) {
        try {
            FormeJuridiqueEntity existingFormeJuridique = formJuridiqueRepository.findById(code)
                    .orElse(null);
            if (existingFormeJuridique == null) {
                throw new RuntimeException("Forme juridique with code " + code + " does not exist");
            }

            FormeJuridiqueEntity formeJuridique = regulatoryMapper.toFormeJuridiqueEntity(formeJuridiqueToUpdateDTO);

            existingFormeJuridique.setAbreviation(
                    formeJuridique.getAbreviation() != null ? formeJuridique.getAbreviation()
                            : existingFormeJuridique.getAbreviation());
            existingFormeJuridique.setLibelle(formeJuridique.getLibelle() != null ? formeJuridique.getLibelle()
                    : existingFormeJuridique.getLibelle());
            existingFormeJuridique.setUpdatedAt(java.time.LocalDateTime.now());
            existingFormeJuridique.setUpdatedBy(currentUser.getUsername());

            formJuridiqueRepository.save(existingFormeJuridique);

            FormeJuridiqueDto updatedFormeJuridiqueDto = regulatoryMapper.toFormeJuridiqueDto(existingFormeJuridique);
            return updatedFormeJuridiqueDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change legal form status
    @Override
    public void changeLegalFormStatus(String code, UserEntity currentUser) {
        try {
            FormeJuridiqueEntity existingFormeJuridique = formJuridiqueRepository.findById(code)
                    .orElse(null);
            if (existingFormeJuridique == null) {
                throw new RuntimeException("Legal form with code " + code + " does not exist");
            }

            existingFormeJuridique.setIsActive(!existingFormeJuridique.getIsActive());
            existingFormeJuridique.setUpdatedAt(java.time.LocalDateTime.now());
            existingFormeJuridique.setUpdatedBy(currentUser.getUsername());

            // saving the updated legal form
            formJuridiqueRepository.save(existingFormeJuridique);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // activity methods

    // create an activity
    @Override
    public ActivityDto createActivity(ActivityDto activityToCreateDTO, UserEntity currentUser) {

        try {

            ActivityEntity activity = regulatoryMapper.toActivityEntity(activityToCreateDTO);

            ActivityEntity existingActivity = activityRepository.findById(activity.getCode()).orElse(null);
            if (existingActivity != null) {
                throw new RuntimeException("Activity with code " + activity.getCode() + " already exists");
            }

            ActivityEntity activityToSave = ActivityEntity.builder()
                    .code(activity.getCode())
                    .codeSection(activity.getCodeSection())
                    .domaine(activity.getDomaine())
                    .libelle(activity.getLibelle())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            activityRepository.save(activityToSave);

            return regulatoryMapper.toActivityDto(activityToSave);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all activities with pagination
    @Override
    public Page<ActivityDto> getAllActivities(Pageable pageable) {
        try {
            Page<ActivityEntity> activitiesList = activityRepository.findAll(pageable);
            Page<ActivityDto> activitiesDtoList = regulatoryMapper.toActivityDtoList(activitiesList);
            return activitiesDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update activity
    @Override
    public ActivityDto updateActivity(String code, ActivityUpdateDto activityToUpdateDTO,
            UserEntity currentUser) {
        try {
            ActivityEntity existingActivity = activityRepository.findById(code)
                    .orElse(null);
            if (existingActivity == null) {
                throw new RuntimeException("Activity with code " + code + " does not exist");
            }

            ActivityEntity activity = regulatoryMapper.toActivityEntity(activityToUpdateDTO);

            existingActivity.setCodeSection(
                    activity.getCodeSection() != null ? activity.getCodeSection()
                            : existingActivity.getCodeSection());
            existingActivity.setDomaine(activity.getDomaine() != null ? activity.getDomaine()
                    : existingActivity.getDomaine());
            existingActivity.setLibelle(activity.getLibelle() != null ? activity.getLibelle()
                    : existingActivity.getLibelle());
            existingActivity.setUpdatedAt(java.time.LocalDateTime.now());
            existingActivity.setUpdatedBy(currentUser.getUsername());

            activityRepository.save(existingActivity);

            ActivityDto updatedActivityDto = regulatoryMapper.toActivityDto(existingActivity);
            return updatedActivityDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change activity status
    @Override
    public void changeActivityStatus(String code, UserEntity currentUser) {
        try {
            ActivityEntity existingActivity = activityRepository.findById(code)
                    .orElse(null);
            if (existingActivity == null) {
                throw new RuntimeException("Activity with code " + code + " does not exist");
            }

            existingActivity.setIsActive(!existingActivity.getIsActive());
            existingActivity.setUpdatedAt(java.time.LocalDateTime.now());
            existingActivity.setUpdatedBy(currentUser.getUsername());

            activityRepository.save(existingActivity);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // nif methods

    // create nif
    @Override
    public NifDto createNif(NifDto nifToCreateDTO, UserEntity currentUser) {
        try {

            NifEntity existingNif = nifRepository.findByNif(nifToCreateDTO.getNif());
            if (existingNif != null) {
                throw new RuntimeException("NIF with code " + nifToCreateDTO.getNif() + " already exists");
            }

            ActivityEntity existingActivity = activityRepository.findByCode(nifToCreateDTO.getActivityCode());
            if (existingActivity == null) {
                throw new RuntimeException(
                        "Activity with code " + nifToCreateDTO.getActivityCode() + " does not exist");
            }

            FormeJuridiqueEntity existingFormeJuridique = formJuridiqueRepository
                    .findById(nifToCreateDTO.getFormeJuridiqueCode()).orElse(null);
            if (existingFormeJuridique == null) {
                throw new RuntimeException(
                        "Forme juridique with code " + nifToCreateDTO.getFormeJuridiqueCode() + " does not exist");
            }

            NifEntity newNif = regulatoryMapper.toNifEntity(nifToCreateDTO);
            newNif.setActivity(existingActivity);
            newNif.setFormeJuridique(existingFormeJuridique);
            newNif.setIsActive(true);
            newNif.setCreatedAt(java.time.LocalDateTime.now());
            newNif.setCreatedBy(currentUser.getUsername());

            nifRepository.save(newNif);

            return regulatoryMapper.toNifDto(newNif);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all nif with pagination
    @Override
    @Transactional(readOnly = true)
    public Page<NifResponseDto> getAllNifs(Pageable pageable) {
        try {
            Page<NifEntity> nifsList = nifRepository.findAll(pageable);
            Page<NifResponseDto> nifsDtoList = regulatoryMapper.toNifResponseDtoList(nifsList);
            return nifsDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update nif
    @Override
    public NifResponseDto updateNif(String code, NifUpdateDto nifToUpdateDTO,
            UserEntity currentUser) {
        try {
            NifEntity existingNif = nifRepository.findById(code)
                    .orElse(null);
            if (existingNif == null) {
                throw new RuntimeException("NIF with code " + code + " does not exist");
            }

            NifEntity nif = regulatoryMapper.toNifEntity(nifToUpdateDTO);

            existingNif.setRc(nif.getRc() != null ? nif.getRc() : existingNif.getRc());
            existingNif.setActivity(nif.getActivity() != null ? nif.getActivity() : existingNif.getActivity());
            existingNif.setDateCreation(
                    nif.getDateCreation() != null ? nif.getDateCreation() : existingNif.getDateCreation());
            existingNif
                    .setDesignation(nif.getDesignation() != null ? nif.getDesignation() : existingNif.getDesignation());
            existingNif.setFormeJuridique(
                    nif.getFormeJuridique() != null ? nif.getFormeJuridique() : existingNif.getFormeJuridique());
            existingNif.setUpdatedAt(java.time.LocalDateTime.now());
            existingNif.setUpdatedBy(currentUser.getUsername());

            nifRepository.save(existingNif);

            return regulatoryMapper.toNifResponseDto(existingNif);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change nif status
    @Override
    public void changeNifStatus(String code, UserEntity currentUser) {
        try {
            NifEntity existingNif = nifRepository.findById(code)
                    .orElse(null);
            if (existingNif == null) {
                throw new RuntimeException("NIF with code " + code + " does not exist");
            }

            existingNif.setIsActive(!existingNif.getIsActive());
            existingNif.setUpdatedAt(java.time.LocalDateTime.now());
            existingNif.setUpdatedBy(currentUser.getUsername());

            nifRepository.save(existingNif);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
