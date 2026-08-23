package dz.banquealgerie.referential.controller;

import java.io.ByteArrayInputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dz.banquealgerie.referential.domain.dto.ApiResponse;
import dz.banquealgerie.referential.domain.dto.geographic.BulkImportResponseDto;
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneDto;
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneExcelExportDto;
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.service.GeographicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/geographic")
@RequiredArgsConstructor
public class GeographicController {

    private final GeographicService geographicService;

    // pays routes

    // create country
    @PostMapping("/pays")
    @Tag(name = "1. Référentiel Géographique - Pays", description = "Gestion du référentiel des pays")
    public ResponseEntity<ApiResponse<PaysDto>> createCountry(
            @Valid @RequestBody PaysDto paysToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            // created pays
            geographicService.createPays(paysToCreateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<PaysDto>builder()
                    .success(true)
                    .message("Pays created successfully")
                    .statusCode(201)
                    .data(paysToCreateDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<PaysDto>builder()
                    .success(false)
                    .message("Failed to create pays: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get all countries with pagination
    @GetMapping("/pays")
    @Tag(name = "1. Référentiel Géographique - Pays", description = "Gestion du référentiel des pays")
    public ResponseEntity<ApiResponse<Page<PaysDto>>> getAllCountries(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PaysDto> paysList = geographicService.getAllPays(pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<PaysDto>>builder()
                    .success(true)
                    .message("Pays list retrieved successfully")
                    .statusCode(200)
                    .data(paysList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Page<PaysDto>>builder()
                    .success(false)
                    .message("Failed to retrieve pays list: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get country by code
    @GetMapping("/pays/{code}")
    @Tag(name = "1. Référentiel Géographique - Pays", description = "Gestion du référentiel des pays")
    public ResponseEntity<ApiResponse<PaysDto>> getCountryByCode(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code pays doit contenir exactement 3 chiffres") String code) {
        try {
            PaysDto pays = geographicService.getPaysByCode(code);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<PaysDto>builder()
                    .success(true)
                    .message("Pays retrieved successfully")
                    .statusCode(200)
                    .data(pays)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<PaysDto>builder()
                    .success(false)
                    .message("Failed to retrieve pays: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // update country
    @PatchMapping("/pays/{code}")
    @Tag(name = "1. Référentiel Géographique - Pays", description = "Gestion du référentiel des pays")
    public ResponseEntity<ApiResponse<PaysDto>> updateCountry(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code pays doit contenir exactement 3 chiffres") String code,
            @RequestBody PaysUpdateDto paysToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            // updated pays
            PaysDto updatedPays = geographicService.updatePays(code, paysToUpdateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<PaysDto>builder()
                    .success(true)
                    .message("Pays updated successfully")
                    .statusCode(200)
                    .data(updatedPays)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<PaysDto>builder()
                    .success(false)
                    .message("Failed to update pays: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change country status
    @PatchMapping("/pays/{code}/status")
    @Tag(name = "1. Référentiel Géographique - Pays", description = "Gestion du référentiel des pays")
    public ResponseEntity<ApiResponse<Void>> changeCountryStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code pays doit contenir exactement 3 chiffres") String code,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            // change pays status
            geographicService.changePaysStatus(code, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Pays status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change pays status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // search countries by abreviation or libelle
    @GetMapping("/pays/search")
    @Tag(name = "1. Référentiel Géographique - Pays", description = "Gestion du référentiel des pays")
    public ResponseEntity<ApiResponse<Page<PaysDto>>> searchCountries(
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<PaysDto> paysList = geographicService.searchPays(searchTerm, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<PaysDto>>builder()
                    .success(true)
                    .message("Pays list retrieved successfully")
                    .statusCode(200)
                    .data(paysList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Page<PaysDto>>builder()
                    .success(false)
                    .message("Failed to retrieve pays list: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // bulk import countries
    @PostMapping(value = "/pays/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "1. Référentiel Géographique - Pays", description = "Gestion du référentiel des pays")
    public ResponseEntity<ApiResponse<BulkImportResponseDto>> importPaysBulk(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateIfExists", defaultValue = "false") boolean updateIfExists,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            BulkImportResponseDto response = geographicService.importPaysBulk(file, currentUser, updateIfExists);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BulkImportResponseDto>builder()
                    .success(true)
                    .message("Importation des pays exécutée")
                    .statusCode(HttpStatus.CREATED.value())
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<BulkImportResponseDto>builder()
                            .success(false)
                            .message("Failed to import pays: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    // export countries
    @GetMapping("/pays/bulk-export")
    @Tag(name = "1. Référentiel Géographique - Pays", description = "Gestion du référentiel des pays")
    public ResponseEntity<InputStreamResource> exportPaysBulk(
            @AuthenticationPrincipal UserEntity currentUser) {

        ByteArrayInputStream response = geographicService.exportPaysToExcel();
        InputStreamResource file = new InputStreamResource(response);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pays_export.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    // wilaya routes
    @PostMapping("/wilaya")
    @Tag(name = "2. Référentiel Géographique - Wilayas", description = "Gestion du référentiel des wilayas")
    public ResponseEntity<ApiResponse<WilayaDto>> createWilaya(
            @Valid @RequestBody WilayaDto wilayaToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            // created wilaya
            geographicService.createWilaya(wilayaToCreateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<WilayaDto>builder()
                    .success(true)
                    .message("Wilaya created successfully")
                    .statusCode(201)
                    .data(wilayaToCreateDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<WilayaDto>builder()
                    .success(false)
                    .message("Failed to create wilaya: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get all wilayas with pagination
    @GetMapping("/wilaya")
    @Tag(name = "2. Référentiel Géographique - Wilayas", description = "Gestion du référentiel des wilayas")
    public ResponseEntity<ApiResponse<Page<WilayaDto>>> getAllWilayas(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<WilayaDto> wilayaList = geographicService.getAllWilayas(pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<WilayaDto>>builder()
                    .success(true)
                    .message("Wilaya list retrieved successfully")
                    .statusCode(200)
                    .data(wilayaList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Page<WilayaDto>>builder()
                    .success(false)
                    .message("Failed to retrieve wilaya list: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get wilaya by code
    @GetMapping("/wilaya/{code}")
    @Tag(name = "2. Référentiel Géographique - Wilayas", description = "Gestion du référentiel des wilayas")
    public ResponseEntity<ApiResponse<WilayaDto>> getWilayaByCode(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code wilaya doit contenir exactement 2 chiffres") String code) {
        try {
            WilayaDto wilaya = geographicService.getWilayaByCode(code);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<WilayaDto>builder()
                    .success(true)
                    .message("Wilaya retrieved successfully")
                    .statusCode(200)
                    .data(wilaya)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<WilayaDto>builder()
                    .success(false)
                    .message("Failed to retrieve wilaya: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // update wilaya
    @PatchMapping("/wilaya/{code}")
    @Tag(name = "2. Référentiel Géographique - Wilayas", description = "Gestion du référentiel des wilayas")
    public ResponseEntity<ApiResponse<WilayaDto>> updateWilaya(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code wilaya doit contenir exactement 2 chiffres") String code,
            @Valid @RequestBody WilayaUpdateDto wilayaToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            // updated wilaya
            WilayaDto updatedWilaya = geographicService.updateWilaya(code, wilayaToUpdateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<WilayaDto>builder()
                    .success(true)
                    .message("Wilaya updated successfully")
                    .statusCode(200)
                    .data(updatedWilaya)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<WilayaDto>builder()
                    .success(false)
                    .message("Failed to update wilaya: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change wilaya status
    @PatchMapping("/wilaya/{code}/status")
    @Tag(name = "2. Référentiel Géographique - Wilayas", description = "Gestion du référentiel des wilayas")
    public ResponseEntity<ApiResponse<Void>> changeWilayaStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code wilaya doit contenir exactement 2 chiffres") String code,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            // change wilaya status
            geographicService.changeWilayaStatus(code, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Wilaya status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change wilaya status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // search wilayas by abreviation or libelle
    @GetMapping("/wilaya/search")
    @Tag(name = "2. Référentiel Géographique - Wilayas", description = "Gestion du référentiel des wilayas")
    public ResponseEntity<ApiResponse<Page<WilayaDto>>> searchWilayas(
            @RequestParam(required = false) @Pattern(regexp = "^([a-zA-Z\\s]+|[\\p{IsArabic}\\s]+)$", message = "Le terme de recherche ne peut contenir que des lettres et des chiffres") String searchTerm,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<WilayaDto> wilayaList = geographicService.searchWilayas(searchTerm, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<WilayaDto>>builder()
                    .success(true)
                    .message("Wilaya list retrieved successfully")
                    .statusCode(200)
                    .data(wilayaList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Page<WilayaDto>>builder()
                    .success(false)
                    .message("Failed to retrieve wilaya list: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // import wilayas bulk
    @PostMapping(value = "/wilaya/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "2. Référentiel Géographique - Wilayas", description = "Gestion du référentiel des wilayas")
    public ResponseEntity<ApiResponse<BulkImportResponseDto>> importWilayasBulk(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateIfExists", defaultValue = "false") boolean updateIfExists,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            BulkImportResponseDto response = geographicService.importWilayasBulk(file, currentUser, updateIfExists);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BulkImportResponseDto>builder()
                    .success(true)
                    .message("Importation des wilayas exécutée")
                    .statusCode(HttpStatus.CREATED.value())
                    .data(response)
                    .build());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<BulkImportResponseDto>builder()
                            .success(false)
                            .message("Failed to import wilayas: " + e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    // export wilayas bulk
    @GetMapping("/wilaya/bulk-export")
    @Tag(name = "2. Référentiel Géographique - Wilayas", description = "Gestion du référentiel des wilayas")
    public ResponseEntity<InputStreamResource> exportWilayasBulk(
            @AuthenticationPrincipal UserEntity currentUser) {

        ByteArrayInputStream response = geographicService.exportWilayasToExcel();
        InputStreamResource file = new InputStreamResource(response);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wilayas_export.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    // commune routes

    // create commune
    @PostMapping("/wilaya/{wilayaCode}/commune")
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<ApiResponse<CommuneDto>> createCommune(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code wilaya doit contenir exactement 2 chiffres") String wilayaCode,
            @Valid @RequestBody CommuneDto communeToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            // created commune
            geographicService.createCommune(wilayaCode, communeToCreateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CommuneDto>builder()
                    .success(true)
                    .message("Commune created successfully")
                    .statusCode(201)
                    .data(communeToCreateDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<CommuneDto>builder()
                    .success(false)
                    .message("Failed to create commune: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get commune by wilaya code
    @GetMapping("/wilaya/{wilayaCode}/commune")
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<ApiResponse<Page<CommuneDto>>> getCommunesByWilayaCode(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code wilaya doit contenir exactement 2 chiffres") String wilayaCode,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<CommuneDto> communeList = geographicService.getCommunesByWilayaCode(wilayaCode, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<CommuneDto>>builder()
                    .success(true)
                    .message("Commune list retrieved successfully")
                    .statusCode(200)
                    .data(communeList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Page<CommuneDto>>builder()
                    .success(false)
                    .message("Failed to retrieve commune list: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // update commune
    @PatchMapping("/wilaya/{wilayaCode}/commune/{communeCode}")
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<ApiResponse<CommuneDto>> updateCommune(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code wilaya doit contenir exactement 2 chiffres") String wilayaCode,
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{4}$", message = "Le code commune doit contenir exactement 4 chiffres") String communeCode,
            @Valid @RequestBody CommuneUpdateDto communeToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            CommuneDto updatedCommune = geographicService.updateCommune(wilayaCode, communeCode, communeToUpdateDTO,
                    currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<CommuneDto>builder()
                    .success(true)
                    .message("Commune updated successfully")
                    .statusCode(200)
                    .data(updatedCommune)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<CommuneDto>builder()
                    .success(false)
                    .message("Failed to update commune: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change commune status
    @PatchMapping("/commune/{communeCode}/status")
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<ApiResponse<Void>> changeCommuneStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{4}$", message = "Le code commune doit contenir exactement 4 chiffres") String communeCode,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            geographicService.changeCommuneStatus(communeCode, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Commune status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change commune status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // search communes by libelle
    @GetMapping("/commune/search")
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<ApiResponse<Page<CommuneDto>>> searchCommunes(
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<CommuneDto> communeList = geographicService.searchCommunes(searchTerm, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<CommuneDto>>builder()
                    .success(true)
                    .message("Commune list retrieved successfully")
                    .statusCode(200)
                    .data(communeList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Page<CommuneDto>>builder()
                    .success(false)
                    .message("Failed to retrieve commune list: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // communes bulk-import
    @PostMapping(value = "/commune/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<ApiResponse<BulkImportResponseDto>> importCommunesBulk(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateIfExists", defaultValue = "false") boolean updateIfExists,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            BulkImportResponseDto response = geographicService.importCommunesBulk(file, currentUser, updateIfExists);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BulkImportResponseDto>builder()
                    .success(true)
                    .message("Importation des communes exécutée")
                    .statusCode(HttpStatus.CREATED.value())
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<BulkImportResponseDto>builder()
                            .success(false)
                            .message("Failed to import communes: " + e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    // communes bulk-import
    @PostMapping(value = "/wilaya/{wilayaCode}/commune/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<ApiResponse<BulkImportResponseDto>> importCommunesBulk(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code wilaya doit contenir exactement 2 chiffres") String wilayaCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateIfExists", defaultValue = "false") boolean updateIfExists,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            BulkImportResponseDto response = geographicService.importCommunesByWilayaBulk(file, wilayaCode, currentUser,
                    updateIfExists);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BulkImportResponseDto>builder()
                    .success(true)
                    .message("Importation des communes exécutée")
                    .statusCode(HttpStatus.CREATED.value())
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<BulkImportResponseDto>builder()
                            .success(false)
                            .message("Failed to import communes: " + e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    // export commune by wilayas bulk
    @GetMapping("/wilaya/{wilayaCode}/commune/bulk-export")
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<InputStreamResource> exportCommuneByWilayaBulk(
            @PathVariable String wilayaCode,
            @AuthenticationPrincipal UserEntity currentUser) {

        CommuneExcelExportDto response = geographicService.exportCommuneByWilayaToExcel(wilayaCode);
        InputStreamResource file = new InputStreamResource(response.stream());

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(response.fileName(), java.nio.charset.StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    // export global communes bulk
    @GetMapping("/commune/bulk-export")
    @Tag(name = "3. Référentiel Géographique - Communes", description = "Gestion du référentiel des communes")
    public ResponseEntity<InputStreamResource> exportCommunesBulk(
            @AuthenticationPrincipal UserEntity currentUser) {

        ByteArrayInputStream response = geographicService.exportCommunesToExcel();
        InputStreamResource file = new InputStreamResource(response);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=communes_export.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

}
