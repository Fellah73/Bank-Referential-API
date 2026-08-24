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

import dz.banquealgerie.referential.domain.dto.banking.accountStatus.AccountStatusDto;
import dz.banquealgerie.referential.domain.dto.banking.accountStatus.AccountStatusUpdateDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceExcelExportDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceUpdateDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.BulkImportResponseDto;
import dz.banquealgerie.referential.domain.dto.geographic.ImportErrorDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.banking.AccountStatusEntity;
import dz.banquealgerie.referential.domain.entities.banking.AgenceEntity;
import dz.banquealgerie.referential.domain.entities.banking.EtablissementEntity;
import dz.banquealgerie.referential.mappers.BankingMapper;
import dz.banquealgerie.referential.repositories.banking.AccountStatusRepository;
import dz.banquealgerie.referential.repositories.banking.AgenceRepository;
import dz.banquealgerie.referential.repositories.banking.EtablissementRepository;
import dz.banquealgerie.referential.service.BankingService;
import dz.banquealgerie.referential.utils.excel.ExcelUtils;
import dz.banquealgerie.referential.utils.excel.banking.export.AgenceExcelExport;
import dz.banquealgerie.referential.utils.excel.banking.export.EtablissementExcelExport;
import dz.banquealgerie.referential.utils.excel.banking.imports.AgenceExcelParser;
import dz.banquealgerie.referential.utils.excel.banking.imports.EtablissementExcelParser;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class BankingServiceImpl implements BankingService {

    private final EtablissementRepository etablissementRepository;
    private final AgenceRepository agenceRepository;
    private final AccountStatusRepository accountStatusRepository;

    private final BankingMapper bankingMapper;

    private final Validator validator;

    public BankingServiceImpl(EtablissementRepository etablissementRepository, AgenceRepository agenceRepository,
            AccountStatusRepository accountStatusRepository,
            BankingMapper bankingMapper,
            Validator validator) {
        this.etablissementRepository = etablissementRepository;
        this.agenceRepository = agenceRepository;
        this.accountStatusRepository = accountStatusRepository;
        this.bankingMapper = bankingMapper;
        this.validator = validator;
    }

    // create etablissement
    @Override
    public EtablissementEntity createEtablissment(EtablissementDto etablissementDto, UserEntity currentUser) {
        try {

            // convert dto to entity
            EtablissementEntity etablissement = bankingMapper.toEtablissementEntity(etablissementDto);

            EtablissementEntity existingEtablissement = etablissementRepository
                    .findByCode(etablissement.getCode());
            if (existingEtablissement != null) {
                throw new RuntimeException("Country with code " + etablissement.getCode() + " already exists");
            }

            EtablissementEntity etablissementToSave = EtablissementEntity.builder()
                    .code(etablissement.getCode())
                    .abreviation(etablissement.getAbreviation())
                    .libelle(etablissement.getLibelle())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            etablissementRepository.save(etablissementToSave);

            return etablissementToSave;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all countries with pagination
    @Override
    public Page<EtablissementDto> getAllEtablissement(Pageable pageable) {
        try {
            Page<EtablissementEntity> etablissementList = etablissementRepository.findAll(pageable);
            Page<EtablissementDto> etablissementDtoList = bankingMapper.toEtablissementDtoList(etablissementList);
            return etablissementDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get etablissement by code
    @Override
    public EtablissementDto getEtablissementByCode(String code) {
        try {
            EtablissementEntity existingEtablissement = etablissementRepository.findByCode(code);
            if (existingEtablissement == null) {
                throw new RuntimeException("Country with code " + code + " does not exist");
            }

            return bankingMapper.toEtablissementDto(existingEtablissement);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update country
    @Override
    public EtablissementDto updateEtablissement(String code, EtablissementUpdateDto etablissmenetDto,
            UserEntity currentUser) {
        try {
            EtablissementEntity existingEtablissement = etablissementRepository.findByCode(code);
            if (existingEtablissement == null) {
                throw new RuntimeException("Country with code " + code + " does not exist");
            }

            EtablissementEntity etablissement = bankingMapper.toEtablissementEntity(etablissmenetDto);

            existingEtablissement.setAbreviation(
                    etablissement.getAbreviation() != null ? etablissement.getAbreviation()
                            : existingEtablissement.getAbreviation());
            existingEtablissement.setLibelle(etablissement.getLibelle() != null ? etablissement.getLibelle()
                    : existingEtablissement.getLibelle());
            existingEtablissement.setUpdatedAt(java.time.LocalDateTime.now());
            existingEtablissement.setUpdatedBy(currentUser.getUsername());

            etablissementRepository.save(existingEtablissement);

            EtablissementDto updatedEtablissementDto = bankingMapper.toEtablissementDto(existingEtablissement);
            return updatedEtablissementDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change etablissement status
    @Override
    public void changeEtablissementStatus(String code, UserEntity currentUser) {
        try {
            EtablissementEntity existingEtablissement = etablissementRepository.findByCode(code);
            if (existingEtablissement == null) {
                throw new RuntimeException("Etablissement with code " + code + " does not exist");
            }

            existingEtablissement.setIsActive(!existingEtablissement.getIsActive());
            existingEtablissement.setUpdatedAt(java.time.LocalDateTime.now());
            existingEtablissement.setUpdatedBy(currentUser.getUsername());

            etablissementRepository.save(existingEtablissement);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // search countries by abreviation or libelle
    @Override
    public Page<EtablissementDto> searchEtablissements(String searchTerm, Pageable pageable) {
        try {
            Page<EtablissementEntity> etablissementList = etablissementRepository.findAll(pageable);

            Streamable<EtablissementEntity> streamableEtablissementList = Streamable.of(etablissementList.getContent());

            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableEtablissementList = streamableEtablissementList
                        .filter(etablissement -> etablissement.getLibelle().toLowerCase()
                                .contains(searchTerm.toLowerCase())
                                || etablissement.getAbreviation().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableEtablissementList.isEmpty()) {
                throw new RuntimeException("No etablissement found for the given search criteria");
            }

            // streamble to page
            etablissementList = streamableEtablissementList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            Page<EtablissementDto> etablissementDtoList = bankingMapper.toEtablissementDtoList(etablissementList);
            return etablissementDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // etablissement bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importEtablissementsBulk(MultipartFile file, UserEntity currentUser,
            boolean updateIfExists) {

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<EtablissementDto> etablissementDtos;
        try {
            etablissementDtos = EtablissementExcelParser.parse(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        List<ImportErrorDto> errors = new ArrayList<>();
        try {
            Set<String> processedCodes = new HashSet<>();
            Set<String> processedLibelles = new HashSet<>();
            Set<String> processedAbreviation = new HashSet<>();
            int successCount = 0;
            int rowNumber = 2;

            String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

            for (EtablissementDto dto : etablissementDtos) {

                Set<ConstraintViolation<EtablissementDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.iterator().next().getMessage();
                    errors.add(new ImportErrorDto(rowNumber, dto.getCode(), errorMsg));
                    rowNumber++;
                    continue;
                }

                if (processedCodes.contains(dto.getCode())) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(), "Code en double dans le même fichier Excel"));
                    rowNumber++;
                    continue;
                }

                if (processedLibelles.contains(dto.getLibelle())) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(),
                                    "Libellé en double dans le même fichier Excel"));
                    rowNumber++;
                    continue;
                }

                if (processedAbreviation.contains(dto.getAbreviation())) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(),
                                    "abréviation en double dans le même fichier Excel"));
                    rowNumber++;
                    continue;
                }

                Optional<EtablissementEntity> existingEtablissementOpt = etablissementRepository
                        .findById(dto.getCode());

                if (existingEtablissementOpt.isPresent()) {
                    if (!updateIfExists) {
                        errors.add(new ImportErrorDto(rowNumber, dto.getCode(),
                                "L'établissement avec ce code existe déjà"));
                        rowNumber++;
                        continue;
                    }

                    // Upsert
                    EtablissementEntity entityToUpdate = existingEtablissementOpt.get();
                    entityToUpdate.setLibelle(dto.getLibelle());
                    entityToUpdate.setAbreviation(dto.getAbreviation());
                    entityToUpdate.setUpdatedBy(username);
                    entityToUpdate.setUpdatedAt(java.time.LocalDateTime.now());

                    etablissementRepository.save(entityToUpdate);
                } else {
                    EtablissementEntity newEntity = EtablissementEntity.builder()
                            .code(dto.getCode())
                            .libelle(dto.getLibelle())
                            .abreviation(dto.getAbreviation())
                            .isActive(true)
                            .createdBy(username)
                            .createdAt(java.time.LocalDateTime.now())
                            .build();

                    etablissementRepository.save(newEntity);
                }

                processedCodes.add(dto.getCode());
                processedLibelles.add(dto.getLibelle());
                processedAbreviation.add(dto.getAbreviation());
                successCount++;
                rowNumber++;
            }

            return BulkImportResponseDto.builder()
                    .totalRows(etablissementDtos.size())
                    .successCount(successCount)
                    .errorCount(errors.size())
                    .errors(errors)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'importation en masse des établissements : " + e.getMessage());
        }
    }

    // etablissement bulk-export
    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportEtablissementsToExcel() {

        try {
            List<EtablissementEntity> etablissementEntities = etablissementRepository.findAll();

            return EtablissementExcelExport.exportEtablissementsToExcel(etablissementEntities);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des ÉTABLISSEMENTS : " + e.getMessage());
        }

    }

    // agence methods

    // create agence
    @Override
    @Transactional
    public AgenceDto createAgence(String etablissementCode, AgenceDto agenceToCreateDTO, UserEntity currentUser) {
        try {
            EtablissementEntity existingEtablissement = etablissementRepository.findById(etablissementCode)
                    .orElse(null);
            if (existingEtablissement == null) {
                throw new RuntimeException("Etablissement with code " + etablissementCode + " does not exist");
            }

            AgenceEntity existingAgence = agenceRepository.findById(agenceToCreateDTO.getCode()).orElse(null);
            if (existingAgence != null) {
                throw new RuntimeException("Agence with code " + agenceToCreateDTO.getCode() + " already exists");
            }

            AgenceEntity newAgence = bankingMapper.toAgenceEntity(agenceToCreateDTO);
            newAgence.setEtablissement(existingEtablissement);
            newAgence.setIsActive(true);
            newAgence.setCreatedAt(java.time.LocalDateTime.now());
            newAgence.setCreatedBy(currentUser.getUsername());

            agenceRepository.save(newAgence);
            return bankingMapper.toAgenceDto(newAgence);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get agences by etablissement code with pagination
    @Override
    public Page<AgenceDto> getAgencesByEtablissementCode(String etablissementCode, Pageable pageable) {
        try {

            EtablissementEntity existingEtablissement = etablissementRepository.findById(etablissementCode)
                    .orElse(null);
            if (existingEtablissement == null) {
                throw new RuntimeException("Etablissement with code " + etablissementCode + " does not exist");
            }

            Page<AgenceEntity> agenceList = agenceRepository.findByEtablissementCode(existingEtablissement.getCode(),
                    pageable);
            return bankingMapper.toAgenceDtoList(agenceList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update agence
    @Override
    public AgenceDto updateAgence(String etablissementCode, String agenceCode, AgenceUpdateDto agenceToUpdateDTO,
            UserEntity currentUser) {
        try {
            EtablissementEntity existingEtablissement = etablissementRepository.findById(etablissementCode)
                    .orElse(null);
            if (existingEtablissement == null) {
                throw new RuntimeException("Etablissement with code " + etablissementCode + " does not exist");
            }

            AgenceEntity existingAgence = agenceRepository.findById(agenceCode).orElse(null);
            if (existingAgence == null) {
                throw new RuntimeException("Agence with code " + agenceCode + " does not exist");
            }

            existingAgence.setLibelle(
                    agenceToUpdateDTO.getLibelle() != null ? agenceToUpdateDTO.getLibelle()
                            : existingAgence.getLibelle());
            existingAgence.setUpdatedAt(java.time.LocalDateTime.now());
            existingAgence.setUpdatedBy(currentUser.getUsername());

            agenceRepository.save(existingAgence);
            return bankingMapper.toAgenceDto(existingAgence);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // search agences by libelle or code
    @Override
    public Page<AgenceDto> searchAgences(String searchTerm, Pageable pageable) {
        try {
            Page<AgenceEntity> agenceList = agenceRepository.findAll(pageable);

            Streamable<AgenceEntity> streamableAgenceList = Streamable.of(agenceList.getContent());

            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableAgenceList = streamableAgenceList
                        .filter(agence -> agence.getLibelle().toLowerCase()
                                .contains(searchTerm.toLowerCase())
                                || agence.getCode().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableAgenceList.isEmpty()) {
                throw new RuntimeException("No agence found for the given search criteria");
            }

            agenceList = streamableAgenceList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            Page<AgenceDto> agenceDtoList = bankingMapper.toAgenceDtoList(agenceList);
            return agenceDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change agence status
    @Override
    public void changeAgenceStatus(String agenceCode, UserEntity currentUser) {
        try {
            AgenceEntity existingAgence = agenceRepository.findById(agenceCode).orElse(null);
            if (existingAgence == null) {
                throw new RuntimeException("Agence with code " + agenceCode + " does not exist");
            }

            existingAgence.setIsActive(!existingAgence.getIsActive());
            existingAgence.setUpdatedAt(java.time.LocalDateTime.now());
            existingAgence.setUpdatedBy(currentUser.getUsername());

            agenceRepository.save(existingAgence);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // agence by etablissement bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importAgencesByEtablissementBulk(MultipartFile file, UserEntity currentUser,
            boolean updateIfExists, String etablissementCode) {

        Optional<EtablissementEntity> existingEtablissement = etablissementRepository.findById(etablissementCode);

        if (existingEtablissement == null) {
            throw new RuntimeException("Etablissement with code " + etablissementCode + " does not exist");
        }

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<AgenceDto> agenceDtos;
        try {
            agenceDtos = AgenceExcelParser.parse(file.getInputStream(), false);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        List<ImportErrorDto> errors = new ArrayList<>();
        Set<String> processedCodes = new HashSet<>();

        int successCount = 0;
        int rowNumber = 2;
        String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

        for (AgenceDto dto : agenceDtos) {

            Set<ConstraintViolation<AgenceDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errorMsg = violations.iterator().next().getMessage();
                errors.add(new ImportErrorDto(rowNumber, dto.getCode(), errorMsg));
                rowNumber++;
                continue;
            }

            if (processedCodes.contains(dto.getCode())) {
                errors.add(new ImportErrorDto(rowNumber, dto.getCode(), "Code en double dans le même fichier Excel"));
                rowNumber++;
                continue;
            }

            Optional<AgenceEntity> existingAgenceOpt = agenceRepository.findById(dto.getCode());

            if (existingAgenceOpt.isPresent()) {
                if (!updateIfExists) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(), "L'agence avec ce code existe déjà en BDD"));
                    rowNumber++;
                    continue;
                }

                AgenceEntity entityToUpdate = existingAgenceOpt.get();
                entityToUpdate.setLibelle(dto.getLibelle());
                entityToUpdate.setEtablissement(existingEtablissement.get());
                entityToUpdate.setUpdatedBy(username);
                entityToUpdate.setUpdatedAt(java.time.LocalDateTime.now());

                agenceRepository.save(entityToUpdate);
            } else {
                AgenceEntity newEntity = AgenceEntity.builder()
                        .code(dto.getCode())
                        .libelle(dto.getLibelle())
                        .etablissement(existingEtablissement.get())
                        .isActive(true)
                        .createdBy(username)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

                agenceRepository.save(newEntity);
            }

            processedCodes.add(dto.getCode());
            successCount++;
            rowNumber++;
        }

        return BulkImportResponseDto.builder()
                .totalRows(agenceDtos.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    // agence bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importAgencesBulk(MultipartFile file, UserEntity currentUser,
            boolean updateIfExists) {

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<AgenceDto> agenceDtos;
        try {
            agenceDtos = AgenceExcelParser.parse(file.getInputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        List<ImportErrorDto> errors = new ArrayList<>();
        Set<String> processedCodes = new HashSet<>();

        int successCount = 0;
        int rowNumber = 2;
        String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

        for (AgenceDto dto : agenceDtos) {

            Set<ConstraintViolation<AgenceDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errorMsg = violations.iterator().next().getMessage();
                errors.add(new ImportErrorDto(rowNumber, dto.getCode(), errorMsg));
                rowNumber++;
                continue;
            }

            if (processedCodes.contains(dto.getCode())) {
                errors.add(new ImportErrorDto(rowNumber, dto.getCode(), "Code en double dans le même fichier Excel"));
                rowNumber++;
                continue;
            }

            Optional<EtablissementEntity> etablissementOpt = etablissementRepository
                    .findById(dto.getEtablissementCode());
            if (etablissementOpt.isEmpty()) {
                errors.add(new ImportErrorDto(rowNumber, dto.getCode(),
                        "L'établissement parente avec le code " + dto.getEtablissementCode() + " n'existe pas en BDD"));
                rowNumber++;
                continue;
            }

            EtablissementEntity etablissementEntity = etablissementOpt.get();

            Optional<AgenceEntity> existingAgenceOpt = agenceRepository.findById(dto.getCode());

            if (existingAgenceOpt.isPresent()) {
                if (!updateIfExists) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(), "L'agence avec ce code existe déjà en BDD"));
                    rowNumber++;
                    continue;
                }

                AgenceEntity entityToUpdate = existingAgenceOpt.get();
                entityToUpdate.setLibelle(dto.getLibelle());
                entityToUpdate.setEtablissement(etablissementEntity);
                entityToUpdate.setUpdatedBy(username);
                entityToUpdate.setUpdatedAt(java.time.LocalDateTime.now());

                agenceRepository.save(entityToUpdate);
            } else {
                AgenceEntity newEntity = AgenceEntity.builder()
                        .code(dto.getCode())
                        .libelle(dto.getLibelle())
                        .etablissement(etablissementEntity)
                        .isActive(true)
                        .createdBy(username)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

                agenceRepository.save(newEntity);
            }

            processedCodes.add(dto.getCode());
            successCount++;
            rowNumber++;
        }

        return BulkImportResponseDto.builder()
                .totalRows(agenceDtos.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    // agence by etablissement bulk-export
    @Override
    @Transactional(readOnly = true)
    public AgenceExcelExportDto exportAgencesByEtablissementToExcel(String etablissementCode) {
        List<AgenceEntity> entities;
        String fileName;
        try {

            EtablissementEntity etablissement = etablissementRepository.findByCode(etablissementCode);

            if (etablissement == null) {
                throw new RuntimeException("Etablissement with code " + etablissementCode + " does not exist");
            }

            entities = agenceRepository.findByEtablissementCode(etablissementCode);

            String etabIdentifier = (etablissement.getAbreviation() != null
                    && !etablissement.getAbreviation().isBlank())
                            ? etablissement.getAbreviation()
                            : etablissementCode;

            fileName = "agences_" + etabIdentifier + ".xlsx";

            ByteArrayInputStream stream = AgenceExcelExport.exportAgencesToExcel(entities);
            return new AgenceExcelExportDto(stream, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des AGENCES : " + e.getMessage());
        }

    }

    // agence bulk-export
    @Override
    @Transactional(readOnly = true)
    public AgenceExcelExportDto exportAgencesToExcel() {
        List<AgenceEntity> entities;
        String fileName;
        try {
            entities = agenceRepository.findAll();
            fileName = "agences_global.xlsx";

            ByteArrayInputStream stream = AgenceExcelExport.exportAgencesToExcel(entities);
            return new AgenceExcelExportDto(stream, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des AGENCES : " + e.getMessage());
        }

    }

    // account-status methods

    // create account status
    @Override
    public AccountStatusDto createAccountStatus(AccountStatusDto accountStatusToCreateDTO, UserEntity currentUser) {

        try {

            AccountStatusEntity accountStatus = bankingMapper.toAccountStatusEntity(accountStatusToCreateDTO);

            Optional<AccountStatusEntity> existingAccountStatus = accountStatusRepository
                    .findById(accountStatus.getCode());
            if (existingAccountStatus.isPresent()) {
                throw new RuntimeException("Account status with code " + accountStatus.getCode() + " already exists");
            }

            AccountStatusEntity accountStatusToSave = AccountStatusEntity.builder()
                    .code(accountStatus.getCode())
                    .libelle(accountStatus.getLibelle())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            accountStatusRepository.save(accountStatusToSave);

            return bankingMapper.toAccountStatusDto(accountStatusToSave);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all account-status with pagination
    @Override
    public Page<AccountStatusDto> getAllAccountStatuses(Pageable pageable) {
        try {
            Page<AccountStatusEntity> accountStatusList = accountStatusRepository.findAll(pageable);
            Page<AccountStatusDto> accountStatusDtoList = bankingMapper.toAccountStatusDtoList(accountStatusList);
            return accountStatusDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update account-status
    @Override
    public AccountStatusDto updateAccountStatus(String code, AccountStatusUpdateDto accountStatusDto,
            UserEntity currentUser) {
        try {
            AccountStatusEntity existingAccountStatus = accountStatusRepository.findById(code)
                    .orElseThrow(() -> new RuntimeException("Account status with code " + code + " does not exist"));

            AccountStatusEntity accountStatus = bankingMapper.toAccountStatusEntity(accountStatusDto);

            existingAccountStatus.setLibelle(
                    accountStatus.getLibelle() != null ? accountStatus.getLibelle()
                            : existingAccountStatus.getLibelle());
            existingAccountStatus.setUpdatedAt(java.time.LocalDateTime.now());
            existingAccountStatus.setUpdatedBy(currentUser.getUsername());

            // saving the updated account status
            accountStatusRepository.save(existingAccountStatus);

            AccountStatusDto updatedAccountStatusDto = bankingMapper.toAccountStatusDto(existingAccountStatus);
            return updatedAccountStatusDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change account-status status
    @Override
    public void changeAccountStatus(String code, UserEntity currentUser) {
        try {
            AccountStatusEntity existingAccountStatus = accountStatusRepository.findById(code)
                    .orElseThrow(() -> new RuntimeException("Account status with code " + code + " does not exist"));

            existingAccountStatus.setIsActive(!existingAccountStatus.getIsActive());
            existingAccountStatus.setUpdatedAt(java.time.LocalDateTime.now());
            existingAccountStatus.setUpdatedBy(currentUser.getUsername());

            accountStatusRepository.save(existingAccountStatus);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // search account statuses by code or libelle
    @Override
    public Page<AccountStatusDto> searchAccountStatuses(String searchTerm, Pageable pageable) {
        try {
            Page<AccountStatusEntity> accountStatusList = accountStatusRepository.findAll(pageable);

            Streamable<AccountStatusEntity> streamableAccountStatusList = Streamable.of(accountStatusList.getContent());

            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableAccountStatusList = streamableAccountStatusList
                        .filter(accountStatus -> accountStatus.getLibelle().toLowerCase()
                                .contains(searchTerm.toLowerCase())
                                || accountStatus.getCode().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableAccountStatusList.isEmpty()) {
                throw new RuntimeException("No account status found for the given search criteria");
            }

            accountStatusList = streamableAccountStatusList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            Page<AccountStatusDto> accountStatusDtoList = bankingMapper.toAccountStatusDtoList(accountStatusList);
            return accountStatusDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
