package dz.banquealgerie.referential.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import dz.banquealgerie.referential.domain.dto.geographic.BulkImportResponseDto;
import dz.banquealgerie.referential.domain.dto.geographic.ImportErrorDto;
import dz.banquealgerie.referential.domain.dto.regulatory.activity.ActivityDto;
import dz.banquealgerie.referential.domain.dto.regulatory.activity.ActivityUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.formeJuridique.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.formeJuridique.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifResponseDto;
import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.ActivityEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.FormeJuridiqueEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.NifEntity;
import dz.banquealgerie.referential.mappers.RegulatoryMapper;
import dz.banquealgerie.referential.repositories.regulatory.ActivityRepository;
import dz.banquealgerie.referential.repositories.regulatory.FormJuridiqueRepository;
import dz.banquealgerie.referential.repositories.regulatory.NifRepository;
import dz.banquealgerie.referential.service.RegulatoryService;
import dz.banquealgerie.referential.utils.excel.ExcelUtils;
import dz.banquealgerie.referential.utils.excel.regulatory.export.ActivitiesExcelExporter;
import dz.banquealgerie.referential.utils.excel.regulatory.export.NifExcelExporter;
import dz.banquealgerie.referential.utils.excel.regulatory.imports.ActivitiesExcelParser;
import dz.banquealgerie.referential.utils.excel.regulatory.imports.NifExcelParser;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class RegulatoryServiceImpl implements RegulatoryService {
    private final RegulatoryMapper regulatoryMapper;

    private final FormJuridiqueRepository formJuridiqueRepository;
    private final ActivityRepository activityRepository;
    private final NifRepository nifRepository;

    private final Validator validator;

    public RegulatoryServiceImpl(FormJuridiqueRepository formJuridiqueRepository, RegulatoryMapper regulatoryMapper,
            ActivityRepository activityRepository, NifRepository nifRepository, Validator validator) {
        this.formJuridiqueRepository = formJuridiqueRepository;
        this.regulatoryMapper = regulatoryMapper;
        this.activityRepository = activityRepository;
        this.nifRepository = nifRepository;
        this.validator = validator;
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

    // search countries by code, abreaviation or libelle
    @Override
    public Page<FormeJuridiqueDto> searchLegalForms(String searchTerm, Pageable pageable) {
        try {
            Page<FormeJuridiqueEntity> legalFormList = formJuridiqueRepository.findAll(pageable);

            Streamable<FormeJuridiqueEntity> streamableLegalFormList = Streamable.of(legalFormList.getContent());

            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableLegalFormList = streamableLegalFormList
                        .filter(legalForm -> legalForm.getLibelle().toLowerCase()
                                .contains(searchTerm.toLowerCase())
                                || legalForm.getAbreviation().toLowerCase().contains(searchTerm.toLowerCase())
                                || legalForm.getCode().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableLegalFormList.isEmpty()) {
                throw new RuntimeException("No legal form found for the given search criteria");
            }

            legalFormList = streamableLegalFormList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            Page<FormeJuridiqueDto> legalFormDtoList = regulatoryMapper.toFormeJuridiqueDtoList(legalFormList);
            return legalFormDtoList;
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

    // search activities by code, libelle or domaine
    @Override
    public Page<ActivityDto> searchActivities(String searchTerm, Pageable pageable) {
        try {
            Page<ActivityEntity> activityList = activityRepository.findAll(pageable);

            Streamable<ActivityEntity> streamableActivityList = Streamable.of(activityList.getContent());

            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableActivityList = streamableActivityList
                        .filter(activity -> activity.getLibelle().toLowerCase()
                                .contains(searchTerm.toLowerCase())
                                || activity.getCode().toLowerCase().contains(searchTerm.toLowerCase())
                                || activity.getDomaine().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableActivityList.isEmpty()) {
                throw new RuntimeException("No activity found for the given search criteria");
            }

            activityList = streamableActivityList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            Page<ActivityDto> activityDtoList = regulatoryMapper.toActivityDtoList(activityList);
            return activityDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // activity bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importActivitiesBulk(MultipartFile file, UserEntity currentUser,
            boolean updateIfExists) {

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<ActivityDto> activityDtos;
        try {
            activityDtos = ActivitiesExcelParser.parse(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        List<ImportErrorDto> errors = new ArrayList<>();
        Set<String> processedActivities = new HashSet<>();

        int successCount = 0;
        int rowNumber = 2;
        String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

        for (ActivityDto dto : activityDtos) {

            Set<ConstraintViolation<ActivityDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errorMsg = violations.iterator().next().getMessage();
                errors.add(new ImportErrorDto(rowNumber, dto.getCode(), errorMsg));
                rowNumber++;
                continue;
            }

            if (processedActivities.contains(dto.getCode())) {
                errors.add(
                        new ImportErrorDto(rowNumber, dto.getCode(), "Activité en double dans le même fichier Excel"));
                rowNumber++;
                continue;
            }

            ActivityEntity activityEntity = activityRepository.findByCode(dto.getCode());

            if (activityEntity != null) {
                if (!updateIfExists) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(), "L'activité avec ce code existe déjà en BDD"));
                    rowNumber++;
                    continue;
                }

                activityEntity.setCodeSection(dto.getCodeSection());
                activityEntity.setDomaine(dto.getDomaine());
                activityEntity.setLibelle(dto.getLibelle());
                activityEntity.setIsActive(true);
                activityEntity.setUpdatedBy(username);
                activityEntity.setUpdatedAt(java.time.LocalDateTime.now());

                activityRepository.save(activityEntity);
            } else {
                ActivityEntity newActivity = ActivityEntity.builder()
                        .code(dto.getCode())
                        .codeSection(dto.getCodeSection())
                        .domaine(dto.getDomaine())
                        .libelle(dto.getLibelle())
                        .isActive(true)
                        .createdBy(username)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

                activityRepository.save(newActivity);
            }

            processedActivities.add(dto.getCode());
            successCount++;
            rowNumber++;
        }

        return BulkImportResponseDto.builder()
                .totalRows(activityDtos.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    // activity bulk-export
    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportActivitiesToExcel() {

        try {
            List<ActivityEntity> activityEntities = activityRepository.findAll();

            List<ActivityDto> activityDtos = activityEntities.stream()
                    .map(regulatoryMapper::toActivityDto)
                    .toList();

            return ActivitiesExcelExporter.exportActivitiesToExcel(activityDtos);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des Activités : " + e.getMessage());
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

    // search nif by nif or designation
    @Override
    @Transactional(readOnly = true)
    public Page<NifResponseDto> searchNifs(String searchTerm, Pageable pageable) {
        try {
            Page<NifEntity> nifList = nifRepository.findAll(pageable);

            Streamable<NifEntity> streamableNifList = Streamable.of(nifList.getContent());

            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableNifList = streamableNifList
                        .filter(nif -> nif.getNif().toLowerCase()
                                .contains(searchTerm.toLowerCase())
                                || nif.getDesignation().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableNifList.isEmpty()) {
                throw new RuntimeException("No NIF found for the given search criteria");
            }

            nifList = streamableNifList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            Page<NifResponseDto> nifDtoList = regulatoryMapper.toNifResponseDtoList(nifList);
            return nifDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // nif bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importNifsBulk(MultipartFile file, UserEntity currentUser,
            boolean updateIfExists) {

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<NifDto> nifDtos;
        try {
            nifDtos = NifExcelParser.parse(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        List<ImportErrorDto> errors = new ArrayList<>();
        Set<String> processedNifs = new HashSet<>();
        Set<String> processedRc = new HashSet<>();

        int successCount = 0;
        int rowNumber = 2;
        String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

        for (NifDto dto : nifDtos) {

            Set<ConstraintViolation<NifDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errorMsg = violations.iterator().next().getMessage();
                errors.add(new ImportErrorDto(rowNumber, dto.getNif(), errorMsg));
                rowNumber++;
                continue;
            }

            if (processedNifs.contains(dto.getNif())) {
                errors.add(new ImportErrorDto(rowNumber, dto.getNif(), "NIF en double dans le même fichier Excel"));
                rowNumber++;
                continue;
            }

            if (processedRc.contains(dto.getRc())) {
                errors.add(new ImportErrorDto(rowNumber, dto.getRc(), "RC en double dans le même fichier Excel"));
                rowNumber++;
                continue;
            }

            Optional<ActivityEntity> activityOpt = activityRepository.findById(dto.getActivityCode());
            if (activityOpt.isEmpty()) {
                errors.add(new ImportErrorDto(rowNumber, dto.getNif(),
                        "L'activité avec le code [" + dto.getActivityCode() + "] n'existe pas en BDD"));
                rowNumber++;
                continue;
            }

            ActivityEntity activityEntity = activityOpt.get();

            Optional<FormeJuridiqueEntity> formeJuridiqueOpt = formJuridiqueRepository
                    .findById(dto.getFormeJuridiqueCode());
            if (formeJuridiqueOpt.isEmpty()) {
                errors.add(new ImportErrorDto(rowNumber, dto.getNif(),
                        "La forme juridique avec le code [" + dto.getFormeJuridiqueCode() + "] n'existe pas en BDD"));
                rowNumber++;
                continue;
            }

            FormeJuridiqueEntity formeJuridique = formeJuridiqueOpt.get();

            Optional<NifEntity> existingNifOpt = nifRepository.findById(dto.getNif());

            if (existingNifOpt.isPresent()) {
                if (!updateIfExists) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getNif(), "Le NIF avec ce code existe déjà en BDD"));
                    rowNumber++;
                    continue;
                }

                NifEntity entityToUpdate = existingNifOpt.get();
                entityToUpdate.setRc(dto.getRc());
                entityToUpdate.setActivity(activityEntity);
                entityToUpdate.setDateCreation(dto.getDateCreation());
                entityToUpdate.setDesignation(dto.getDesignation());
                entityToUpdate.setFormeJuridique(formeJuridique);
                entityToUpdate.setIsActive(true);
                entityToUpdate.setUpdatedBy(username);
                entityToUpdate.setUpdatedAt(java.time.LocalDateTime.now());

                nifRepository.save(entityToUpdate);
            } else {
                NifEntity newEntity = NifEntity.builder()
                        .nif(dto.getNif())
                        .rc(dto.getRc())
                        .activity(activityEntity)
                        .dateCreation(dto.getDateCreation())
                        .designation(dto.getDesignation())
                        .formeJuridique(formeJuridique)
                        .isActive(true)
                        .createdBy(username)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

                nifRepository.save(newEntity);
            }

            processedNifs.add(dto.getNif());
            processedRc.add(dto.getRc());
            successCount++;
            rowNumber++;
        }

        return BulkImportResponseDto.builder()
                .totalRows(nifDtos.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    // nif bulk-export
    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportNifsToExcel() {

        try {
            List<NifEntity> nifEntities = nifRepository.findAllWithRelations();

            List<NifResponseDto> nifDtos = nifEntities.stream()
                    .map(regulatoryMapper::toNifResponseDto)
                    .toList();

            return NifExcelExporter.exportNifsToExcel(nifDtos);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des NIF : " + e.getMessage());
        }

    }
}
