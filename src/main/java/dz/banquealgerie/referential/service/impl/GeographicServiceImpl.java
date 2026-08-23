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
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneDto;
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneExcelExportDto;
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.geographic.CommuneEntity;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;
import dz.banquealgerie.referential.domain.entities.geographic.WilayaEntity;
import dz.banquealgerie.referential.mappers.GeographicMapper;
import dz.banquealgerie.referential.repositories.geographic.CommuneRepository;
import dz.banquealgerie.referential.repositories.geographic.PaysRepository;
import dz.banquealgerie.referential.repositories.geographic.WilayaRepository;
import dz.banquealgerie.referential.service.GeographicService;
import dz.banquealgerie.referential.utils.excel.ExcelUtils;
import dz.banquealgerie.referential.utils.excel.geographical.export.CommuneExcelExport;
import dz.banquealgerie.referential.utils.excel.geographical.export.PaysExcelExport;
import dz.banquealgerie.referential.utils.excel.geographical.export.WilayaExcelExport;
import dz.banquealgerie.referential.utils.excel.geographical.imports.CommuneExcelParser;
import dz.banquealgerie.referential.utils.excel.geographical.imports.PaysExcelParser;
import dz.banquealgerie.referential.utils.excel.geographical.imports.WilayaExcelParser;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class GeographicServiceImpl implements GeographicService {

    private final PaysRepository paysRepository;
    private final WilayaRepository wilayaRepository;
    private final CommuneRepository communeRepository;

    private final GeographicMapper geographicMapper;

    private final Validator validator;

    public GeographicServiceImpl(PaysRepository paysRepository, WilayaRepository wilayaRepository,
            CommuneRepository communeRepository, GeographicMapper geographicMapper, Validator validator) {
        this.paysRepository = paysRepository;
        this.wilayaRepository = wilayaRepository;
        this.communeRepository = communeRepository;
        this.geographicMapper = geographicMapper;
        this.validator = validator;
    }

    // create country
    @Override
    public PaysEntity createPays(PaysDto paysDto, UserEntity currentUser) {

        try {

            // convert dto to entity
            PaysEntity pays = geographicMapper.toPaysEntity(paysDto);

            PaysEntity existingPays = paysRepository.findPaysByCode(pays.getCode());
            if (existingPays != null) {
                throw new RuntimeException("Country with code " + pays.getCode() + " already exists");
            }

            PaysEntity paysToSave = PaysEntity.builder()
                    .code(pays.getCode())
                    .abreviation(pays.getAbreviation())
                    .libelle(pays.getLibelle())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            // saving the new country
            paysRepository.save(paysToSave);

            return paysToSave;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all countries with pagination
    @Override
    public Page<PaysDto> getAllPays(Pageable pageable) {
        try {
            Page<PaysEntity> paysList = paysRepository.findAll(pageable);
            Page<PaysDto> paysDtoList = geographicMapper.toPaysDtoList(paysList);
            return paysDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get country by code
    @Override
    public PaysDto getPaysByCode(String code) {
        try {
            PaysEntity existingPays = paysRepository.findPaysByCode(code);
            if (existingPays == null) {
                throw new RuntimeException("Country with code " + code + " does not exist");
            }

            return geographicMapper.toPaysDto(existingPays);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update country
    @Override
    public PaysDto updatePays(String code, PaysUpdateDto paysDto, UserEntity currentUser) {
        try {
            PaysEntity existingPays = paysRepository.findPaysByCode(code);
            if (existingPays == null) {
                throw new RuntimeException("Country with code " + code + " does not exist");
            }

            PaysEntity pays = geographicMapper.toPaysEntity(paysDto);

            existingPays.setAbreviation(
                    pays.getAbreviation() != null ? pays.getAbreviation() : existingPays.getAbreviation());
            existingPays.setLibelle(pays.getLibelle() != null ? pays.getLibelle() : existingPays.getLibelle());
            existingPays.setUpdatedAt(java.time.LocalDateTime.now());
            existingPays.setUpdatedBy(currentUser.getUsername());

            // saving the updated country
            paysRepository.save(existingPays);

            PaysDto updatedPaysDto = geographicMapper.toPaysDto(existingPays);
            return updatedPaysDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change country status
    @Override
    public void changePaysStatus(String code, UserEntity currentUser) {
        try {
            PaysEntity existingPays = paysRepository.findPaysByCode(code);
            if (existingPays == null) {
                throw new RuntimeException("Country with code " + code + " does not exist");
            }

            existingPays.setIsActive(!existingPays.getIsActive());
            existingPays.setUpdatedAt(java.time.LocalDateTime.now());
            existingPays.setUpdatedBy(currentUser.getUsername());

            // saving the updated country
            paysRepository.save(existingPays);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // search countries by abreviation or libelle
    @Override
    public Page<PaysDto> searchPays(String searchTerm, Pageable pageable) {
        try {
            Page<PaysEntity> paysList = paysRepository.findAll(pageable);

            Streamable<PaysEntity> streamablePaysList = Streamable.of(paysList.getContent());

            // search by abreviation or libelle
            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamablePaysList = streamablePaysList
                        .filter(pays -> pays.getLibelle().toLowerCase().contains(searchTerm.toLowerCase())
                                || pays.getAbreviation().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamablePaysList.isEmpty()) {
                throw new RuntimeException("No countries found for the given search criteria");
            }

            // streamble to page
            paysList = streamablePaysList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            // entites to dto
            Page<PaysDto> paysDtoList = geographicMapper.toPaysDtoList(paysList);
            return paysDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // pays bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importPaysBulk(MultipartFile file, UserEntity currentUser, boolean updateIfExists) {

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<PaysDto> paysDtos;
        try {
            paysDtos = PaysExcelParser.parse(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        List<ImportErrorDto> errors = new ArrayList<>();

        Set<String> processedCodes = new HashSet<>();
        Set<String> processedLibelles = new HashSet<>();
        Set<String> processedAbreviation = new HashSet<>();
        int successCount = 0;
        int rowNumber = 2;

        String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

        for (PaysDto dto : paysDtos) {

            Set<ConstraintViolation<PaysDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                String errorMsg = violations.iterator().next().getMessage();
                errors.add(new ImportErrorDto(rowNumber, dto.getCode(), errorMsg));
                rowNumber++;
                continue;
            }

            // doublons de codes
            if (processedCodes.contains(dto.getCode())) {
                errors.add(
                        new ImportErrorDto(rowNumber, dto.getCode(), "Code en double dans le même fichier Excel"));
                rowNumber++;
                continue;
            }

            // doublons de libelle
            if (processedLibelles.contains(dto.getLibelle())) {
                errors.add(
                        new ImportErrorDto(rowNumber, dto.getCode(),
                                "Libellé en double dans le même fichier Excel"));
                rowNumber++;
                continue;
            }

            // doublons d'abreviation
            if (processedAbreviation.contains(dto.getAbreviation())) {
                errors.add(
                        new ImportErrorDto(rowNumber, dto.getCode(),
                                "abréviation en double dans le même fichier Excel"));
                rowNumber++;
                continue;
            }

            Optional<PaysEntity> existingPaysOpt = paysRepository.findById(dto.getCode());

            if (existingPaysOpt.isPresent()) {
                if (!updateIfExists) {
                    errors.add(new ImportErrorDto(rowNumber, dto.getCode(), "Le pays avec ce code existe déjà"));
                    rowNumber++;
                    continue;
                }

                // Upsert
                PaysEntity entityToUpdate = existingPaysOpt.get();
                entityToUpdate.setLibelle(dto.getLibelle());
                entityToUpdate.setAbreviation(dto.getAbreviation());
                entityToUpdate.setUpdatedBy(username);
                entityToUpdate.setUpdatedAt(java.time.LocalDateTime.now());

                paysRepository.save(entityToUpdate);
            } else {
                PaysEntity newEntity = PaysEntity.builder()
                        .code(dto.getCode())
                        .libelle(dto.getLibelle())
                        .abreviation(dto.getAbreviation())
                        .isActive(true)
                        .createdBy(username)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

                paysRepository.save(newEntity);
            }

            processedCodes.add(dto.getCode());
            processedLibelles.add(dto.getLibelle());
            processedAbreviation.add(dto.getAbreviation());
            successCount++;
            rowNumber++;
        }

        return BulkImportResponseDto.builder()
                .totalRows(paysDtos.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    // pays bulk-export
    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportPaysToExcel() {

        try {
            List<PaysEntity> paysEntities = paysRepository.findAll();

            return PaysExcelExport.exportPaysToExcel(paysEntities);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des PAYS : " + e.getMessage());
        }

    }

    // wilaya methods

    // create wilaya
    @Override
    public WilayaEntity createWilaya(WilayaDto wilaya, UserEntity currentUser) {
        try {

            // convert dto to entity
            WilayaEntity wilayaEntity = geographicMapper.toWilayaEntity(wilaya);

            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(wilayaEntity.getCode());
            if (existingWilaya != null) {
                throw new RuntimeException("Wilaya with code " + wilayaEntity.getCode() + " already exists");
            }

            WilayaEntity wilayaToSave = WilayaEntity.builder()
                    .code(wilayaEntity.getCode())
                    .libelle(wilayaEntity.getLibelle())
                    .libelleAr(wilayaEntity.getLibelleAr())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            // saving the new wilaya
            wilayaRepository.save(wilayaToSave);

            return wilayaToSave;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all wilayas with pagination
    @Override
    public Page<WilayaDto> getAllWilayas(Pageable pageable) {
        try {
            Page<WilayaEntity> wilayaList = wilayaRepository.findAll(pageable);
            Page<WilayaDto> wilayaDtoList = geographicMapper.toWilayaDtoList(wilayaList);
            return wilayaDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get wilaya by code
    @Override
    public WilayaDto getWilayaByCode(String code) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            return geographicMapper.toWilayaDto(existingWilaya);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update wilaya
    @Override
    public WilayaDto updateWilaya(String code, WilayaUpdateDto wilaya, UserEntity currentUser) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            existingWilaya.setLibelle(wilaya.getLibelle() != null ? wilaya.getLibelle() : existingWilaya.getLibelle());
            existingWilaya.setLibelleAr(
                    wilaya.getLibelleAr() != null ? wilaya.getLibelleAr() : existingWilaya.getLibelleAr());
            existingWilaya.setUpdatedAt(java.time.LocalDateTime.now());
            existingWilaya.setUpdatedBy(currentUser.getUsername());

            wilayaRepository.save(existingWilaya);
            return geographicMapper.toWilayaDto(existingWilaya);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change wilaya status
    @Override
    public void changeWilayaStatus(String code, UserEntity currentUser) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            existingWilaya.setIsActive(!existingWilaya.getIsActive());
            existingWilaya.setUpdatedAt(java.time.LocalDateTime.now());
            existingWilaya.setUpdatedBy(currentUser.getUsername());

            wilayaRepository.save(existingWilaya);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // search wilayas by abreviation or libelle
    @Override
    public Page<WilayaDto> searchWilayas(String searchTerm, Pageable pageable) {
        try {
            Page<WilayaEntity> wilayaList = wilayaRepository.findAll(pageable);

            Streamable<WilayaEntity> streamableWilayaList = Streamable.of(wilayaList.getContent());

            // search by abreviation or libelle
            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableWilayaList = streamableWilayaList
                        .filter(wilaya -> wilaya.getLibelle().toLowerCase().contains(searchTerm.toLowerCase())
                                || wilaya.getLibelleAr().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableWilayaList.isEmpty()) {
                throw new RuntimeException("No wilayas found for the given search criteria");
            }

            // streamble to page
            wilayaList = streamableWilayaList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            // entites to dto
            Page<WilayaDto> wilayaDtoList = geographicMapper.toWilayaDtoList(wilayaList);
            return wilayaDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // wilaya bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importWilayasBulk(MultipartFile file, UserEntity currentUser, boolean updateIfExists) {

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<WilayaDto> wilayaDtos;
        try {
            wilayaDtos = WilayaExcelParser.parse(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        try {

            List<ImportErrorDto> errors = new ArrayList<>();
            Set<String> processedCodes = new HashSet<>();
            Set<String> processedLibelles = new HashSet<>();
            Set<String> processedArabicLibelles = new HashSet<>();

            int successCount = 0;
            int rowNumber = 2;
            String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

            for (WilayaDto dto : wilayaDtos) {

                // A. Validation syntaxique
                Set<ConstraintViolation<WilayaDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.iterator().next().getMessage();
                    errors.add(new ImportErrorDto(rowNumber, dto.getCode(), errorMsg));
                    rowNumber++;
                    continue;
                }

                // doublons de codes
                if (processedCodes.contains(dto.getCode())) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(), "Code en double dans le même fichier Excel"));
                    rowNumber++;
                    continue;
                }

                // doublons de libelle
                if (processedLibelles.contains(dto.getLibelle())) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(),
                                    "Libellé en double dans le même fichier Excel"));
                    rowNumber++;
                    continue;
                }

                // doublons de libelle arabe
                if (processedArabicLibelles.contains(dto.getLibelleAr())) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(),
                                    "Libellé arabe en double dans le même fichier Excel"));
                    rowNumber++;
                    continue;
                }

                Optional<WilayaEntity> existingWilayaOpt = wilayaRepository.findById(dto.getCode());

                if (existingWilayaOpt.isPresent()) {
                    if (!updateIfExists) {
                        errors.add(new ImportErrorDto(rowNumber, dto.getCode(), "La wilaya avec ce code existe déjà"));
                        rowNumber++;
                        continue;
                    }

                    WilayaEntity entityToUpdate = existingWilayaOpt.get();
                    entityToUpdate.setLibelle(dto.getLibelle());
                    entityToUpdate.setLibelleAr(dto.getLibelleAr());
                    entityToUpdate.setUpdatedBy(username);
                    entityToUpdate.setUpdatedAt(java.time.LocalDateTime.now());

                    wilayaRepository.save(entityToUpdate);
                } else {
                    WilayaEntity newEntity = WilayaEntity.builder()
                            .code(dto.getCode())
                            .libelle(dto.getLibelle())
                            .libelleAr(dto.getLibelleAr())
                            .isActive(true)
                            .createdBy(username)
                            .createdAt(java.time.LocalDateTime.now())
                            .build();

                    wilayaRepository.save(newEntity);
                }

                processedCodes.add(dto.getCode());
                processedLibelles.add(dto.getLibelle());
                processedArabicLibelles.add(dto.getLibelleAr());
                successCount++;
                rowNumber++;
            }

            return BulkImportResponseDto.builder()
                    .totalRows(wilayaDtos.size())
                    .successCount(successCount)
                    .errorCount(errors.size())
                    .errors(errors)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // pays bulk-export
    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportWilayasToExcel() {

        try {
            List<WilayaEntity> wilayaEntities = wilayaRepository.findAll();

            return WilayaExcelExport.exportWilayasToExcel(wilayaEntities);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des WILAYAS : " + e.getMessage());
        }

    }

    // commune methods
    @Override
    public void createCommune(String code, CommuneDto commune, UserEntity currentUser) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            CommuneEntity existingCommune = communeRepository.findById(commune.getCode()).orElse(null);
            if (existingCommune != null) {
                throw new RuntimeException("Commune with code " + commune.getCode() + " already exists");
            }

            CommuneEntity newCommune = geographicMapper.toCommuneEntity(commune);
            newCommune.setWilaya(existingWilaya);
            newCommune.setCreatedAt(java.time.LocalDateTime.now());
            newCommune.setCreatedBy(currentUser.getUsername());

            communeRepository.save(newCommune);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get communes by wilaya code with pagination
    @Override
    public Page<CommuneDto> getCommunesByWilayaCode(String wilayaCode, Pageable pageable) {
        try {
            Page<CommuneEntity> communeList = communeRepository.findByWilayaCode(wilayaCode, pageable);
            return geographicMapper.toCommuneDtoList(communeList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update commune
    @Override
    public CommuneDto updateCommune(String code, String communeCode, CommuneUpdateDto commune, UserEntity currentUser) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            CommuneEntity existingCommune = communeRepository.findById(communeCode).orElse(null);
            if (existingCommune == null) {
                throw new RuntimeException("Commune with code " + communeCode + " does not exist");
            }

            existingCommune.setLibelle(
                    commune.getLibelle() != null ? commune.getLibelle() : existingCommune.getLibelle());
            existingCommune.setLibelleAr(
                    commune.getLibelleAr() != null ? commune.getLibelleAr() : existingCommune.getLibelleAr());
            existingCommune.setUpdatedAt(java.time.LocalDateTime.now());
            existingCommune.setUpdatedBy(currentUser.getUsername());

            communeRepository.save(existingCommune);
            return geographicMapper.toCommuneDto(existingCommune);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change commune status
    @Override
    public void changeCommuneStatus(String communeCode, UserEntity currentUser) {
        try {
            CommuneEntity existingCommune = communeRepository.findById(communeCode).orElse(null);
            if (existingCommune == null) {
                throw new RuntimeException("Commune with code " + communeCode + " does not exist");
            }

            existingCommune.setIsActive(!existingCommune.getIsActive());
            existingCommune.setUpdatedAt(java.time.LocalDateTime.now());
            existingCommune.setUpdatedBy(currentUser.getUsername());

            communeRepository.save(existingCommune);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // search communes by libelle or libelleAr
    @Override
    public Page<CommuneDto> searchCommunes(String searchTerm, Pageable pageable) {
        try {
            Page<CommuneEntity> communeList = communeRepository.findAll(pageable);

            Streamable<CommuneEntity> streamableCommuneList = Streamable.of(communeList.getContent());

            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableCommuneList = streamableCommuneList
                        .filter(commune -> commune.getLibelle().toLowerCase().contains(searchTerm.toLowerCase())
                                || commune.getLibelleAr().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableCommuneList.isEmpty()) {
                throw new RuntimeException("No communes found for the given search criteria");
            }

            communeList = streamableCommuneList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            Page<CommuneDto> communeDtoList = geographicMapper.toCommuneDtoList(communeList);
            return communeDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // commune bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importCommunesBulk(MultipartFile file, UserEntity currentUser,
            boolean updateIfExists) {

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<CommuneDto> communeDtos;
        try {
            communeDtos = CommuneExcelParser.parse(file.getInputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        List<ImportErrorDto> errors = new ArrayList<>();
        Set<String> processedCodes = new HashSet<>();

        int successCount = 0;
        int rowNumber = 2;
        String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

        for (CommuneDto dto : communeDtos) {

            Set<ConstraintViolation<CommuneDto>> violations = validator.validate(dto);
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

            Optional<WilayaEntity> wilayaOpt = wilayaRepository.findById(dto.getWilayaCode());
            if (wilayaOpt.isEmpty()) {
                errors.add(new ImportErrorDto(rowNumber, dto.getCode(),
                        "La wilaya parente avec le code [" + dto.getWilayaCode() + "] n'existe pas en BDD"));
                rowNumber++;
                continue;
            }

            WilayaEntity wilayaEntity = wilayaOpt.get();

            Optional<CommuneEntity> existingCommuneOpt = communeRepository.findById(dto.getCode());

            if (existingCommuneOpt.isPresent()) {
                if (!updateIfExists) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(), "La commune avec ce code existe déjà en BDD"));
                    rowNumber++;
                    continue;
                }

                CommuneEntity entityToUpdate = existingCommuneOpt.get();
                entityToUpdate.setLibelle(dto.getLibelle());
                entityToUpdate.setLibelleAr(dto.getLibelleAr());
                entityToUpdate.setWilaya(wilayaEntity);
                entityToUpdate.setUpdatedBy(username);
                entityToUpdate.setUpdatedAt(java.time.LocalDateTime.now());

                communeRepository.save(entityToUpdate);
            } else {
                CommuneEntity newEntity = CommuneEntity.builder()
                        .code(dto.getCode())
                        .libelle(dto.getLibelle())
                        .libelleAr(dto.getLibelleAr())
                        .wilaya(wilayaEntity)
                        .isActive(true)
                        .createdBy(username)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

                communeRepository.save(newEntity);
            }

            processedCodes.add(dto.getCode());
            successCount++;
            rowNumber++;
        }

        return BulkImportResponseDto.builder()
                .totalRows(communeDtos.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    // commune bulk-import
    @Override
    @Transactional
    public BulkImportResponseDto importCommunesByWilayaBulk(MultipartFile file, String wilayaCode,
            UserEntity currentUser,
            boolean updateIfExists) {

        WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(wilayaCode);
        if (existingWilaya == null) {
            throw new RuntimeException("Wilaya with code " + wilayaCode + " does not exist");
        }

        if (!ExcelUtils.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        List<CommuneDto> communeDtos;
        try {
            communeDtos = CommuneExcelParser.parse(file.getInputStream(), false);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        List<ImportErrorDto> errors = new ArrayList<>();
        Set<String> processedCodes = new HashSet<>();

        int successCount = 0;
        int rowNumber = 2;
        String username = (currentUser != null) ? currentUser.getUsername() : "SYSTEM";

        for (CommuneDto dto : communeDtos) {

            Set<ConstraintViolation<CommuneDto>> violations = validator.validate(dto);
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

            Optional<CommuneEntity> existingCommuneOpt = communeRepository.findById(dto.getCode());

            if (existingCommuneOpt.isPresent()) {
                if (!updateIfExists) {
                    errors.add(
                            new ImportErrorDto(rowNumber, dto.getCode(), "La commune avec ce code existe déjà en BDD"));
                    rowNumber++;
                    continue;
                }

                CommuneEntity entityToUpdate = existingCommuneOpt.get();
                entityToUpdate.setLibelle(dto.getLibelle());
                entityToUpdate.setLibelleAr(dto.getLibelleAr());
                entityToUpdate.setWilaya(existingWilaya);
                entityToUpdate.setUpdatedBy(username);
                entityToUpdate.setUpdatedAt(java.time.LocalDateTime.now());

                communeRepository.save(entityToUpdate);
            } else {
                CommuneEntity newEntity = CommuneEntity.builder()
                        .code(dto.getCode())
                        .libelle(dto.getLibelle())
                        .libelleAr(dto.getLibelleAr())
                        .wilaya(existingWilaya)
                        .isActive(true)
                        .createdBy(username)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

                communeRepository.save(newEntity);
            }

            processedCodes.add(dto.getCode());
            successCount++;
            rowNumber++;
        }

        return BulkImportResponseDto.builder()
                .totalRows(communeDtos.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    // commune bulk-export
    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportCommunesToExcel() {

        try {
            List<CommuneEntity> communeEntities = communeRepository.findAll();

            return CommuneExcelExport.exportCommunesToExcel(communeEntities);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des COMMUNES : " + e.getMessage());
        }

    }

    // commune by wilaya bulk-export
    @Override
    @Transactional(readOnly = true)
    public CommuneExcelExportDto exportCommuneByWilayaToExcel(String wilayaCode) {

        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(wilayaCode);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + wilayaCode + " does not exist");
            }

            List<CommuneEntity> communeEntities = communeRepository.findByWilayaCode(wilayaCode);

            ByteArrayInputStream stream = CommuneExcelExport.exportCommunesToExcel(communeEntities);
            return new CommuneExcelExportDto(stream, "communes_" + existingWilaya.getLibelle() + "_export.xlsx");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation des COMMUNES : " + e.getMessage());
        }

    }
}
