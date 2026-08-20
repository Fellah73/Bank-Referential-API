package dz.banquealgerie.referential.controller;

import java.io.ByteArrayInputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import dz.banquealgerie.referential.domain.dto.banking.accountStatus.AccountStatusDto;
import dz.banquealgerie.referential.domain.dto.banking.accountStatus.AccountStatusUpdateDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceExcelExportDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceUpdateDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.BulkImportResponseDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.service.BankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/banking")
@RequiredArgsConstructor
public class BankingController {

    private final BankingService bankingService;

    // etablissement routes

    // create etablissement
    @PostMapping("/etablissement")
    @Tag(name = "4. Référentiel Bancaire - Etablissements", description = "Gestion du référentiel des établissements")
    public ResponseEntity<ApiResponse<EtablissementDto>> createEtablissement(
            @Valid @RequestBody EtablissementDto etablissementToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            bankingService.createEtablissment(etablissementToCreateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<EtablissementDto>builder()
                    .success(true)
                    .message("Etablissement created successfully")
                    .statusCode(201)
                    .data(etablissementToCreateDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<EtablissementDto>builder()
                    .success(false)
                    .message("Failed to create etbalissement: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get all etablissement with pagination
    @GetMapping("/etablissement")
    @Tag(name = "4. Référentiel Bancaire - Etablissements", description = "Gestion du référentiel des établissements")
    public ResponseEntity<ApiResponse<Page<EtablissementDto>>> getAllEtablissement(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<EtablissementDto> etablissementList = bankingService.getAllEtablissement(pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<EtablissementDto>>builder()
                    .success(true)
                    .message("Etablissement list retrieved successfully")
                    .statusCode(200)
                    .data(etablissementList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<EtablissementDto>>builder()
                            .success(false)
                            .message("Failed to retrieve etbalissement list: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    // get etablissement by code
    @GetMapping("/etablissement/{code}")
    @Tag(name = "4. Référentiel Bancaire - Etablissements", description = "Gestion du référentiel des établissements")
    public ResponseEntity<ApiResponse<EtablissementDto>> getEtablissementByCode(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code etablissement doit contenir exactement 3 chiffres") String code) {
        try {
            EtablissementDto etablissement = bankingService.getEtablissementByCode(code);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<EtablissementDto>builder()
                    .success(true)
                    .message("Etablissement retrieved successfully")
                    .statusCode(200)
                    .data(etablissement)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<EtablissementDto>builder()
                    .success(false)
                    .message("Failed to retrieve etablissement: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // update etablissement
    @PatchMapping("/etablissement/{code}")
    @Tag(name = "4. Référentiel Bancaire - Etablissements", description = "Gestion du référentiel des établissements")
    public ResponseEntity<ApiResponse<EtablissementDto>> updateEtablissement(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code etbalissement doit contenir exactement 3 chiffres") String code,
            @RequestBody EtablissementUpdateDto etablissementToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            EtablissementDto updatedEtablissement = bankingService.updateEtablissement(code, etablissementToUpdateDTO,
                    currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<EtablissementDto>builder()
                    .success(true)
                    .message("Etablissement updated successfully")
                    .statusCode(200)
                    .data(updatedEtablissement)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<EtablissementDto>builder()
                    .success(false)
                    .message("Failed to update etablissement: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change etablissement status
    @PatchMapping("/etablissement/{code}/status")
    @Tag(name = "4. Référentiel Bancaire - Etablissements", description = "Gestion du référentiel des établissements")
    public ResponseEntity<ApiResponse<Void>> changeEtablissementStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code etablissement doit contenir exactement 3 chiffres") String code,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            bankingService.changeEtablissementStatus(code, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Etablissement status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change etablissement status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // search etablissements by abreviation or libelle
    @GetMapping("/etablissement/search")
    @Tag(name = "4. Référentiel Bancaire - Etablissements", description = "Gestion du référentiel des établissements")
    public ResponseEntity<ApiResponse<Page<EtablissementDto>>> searchEtablissements(
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<EtablissementDto> etablissementList = bankingService.searchEtablissements(searchTerm, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<EtablissementDto>>builder()
                    .success(true)
                    .message("Etablissement list retrieved successfully")
                    .statusCode(200)
                    .data(etablissementList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<EtablissementDto>>builder()
                            .success(false)
                            .message("Failed to retrieve etablissement list: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    // bulk import etablissements
    @PostMapping(value = "/etablissement/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "4. Référentiel Bancaire - Etablissements", description = "Gestion du référentiel des établissements")
    public ResponseEntity<ApiResponse<BulkImportResponseDto>> importEtablissementsBulk(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateIfExists", defaultValue = "false") boolean updateIfExists,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            BulkImportResponseDto response = bankingService.importEtablissementsBulk(file, currentUser, updateIfExists);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BulkImportResponseDto>builder()
                    .success(true)
                    .message("Importation des établissements exécutée")
                    .statusCode(HttpStatus.CREATED.value())
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<BulkImportResponseDto>builder()
                            .success(false)
                            .message("Failed to import établissements: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    // export etablissements
    @GetMapping("/etablissement/bulk-export")
    @Tag(name = "4. Référentiel Bancaire - Etablissements", description = "Gestion du référentiel des établissements")
    public ResponseEntity<InputStreamResource> exportEtablissementsBulk(
            @AuthenticationPrincipal UserEntity currentUser) {

        ByteArrayInputStream response = bankingService.exportEtablissementsToExcel();
        InputStreamResource file = new InputStreamResource(response);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etablissements_export.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    // agence routes

    // create agence
    @PostMapping("/etablissement/{etablissementCode}/agence")
    @Tag(name = "5. Référentiel Bancaire - Agences", description = "Gestion du référentiel des agences")
    public ResponseEntity<ApiResponse<AgenceDto>> createAgence(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code établissement doit contenir exactement 3 chiffres") String etablissementCode,
            @Valid @RequestBody AgenceDto agenceToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            // created agence
            bankingService.createAgence(etablissementCode, agenceToCreateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<AgenceDto>builder()
                    .success(true)
                    .message("Agence created successfully")
                    .statusCode(201)
                    .data(agenceToCreateDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<AgenceDto>builder()
                    .success(false)
                    .message("Failed to create agence: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get agences by etablissement code
    @GetMapping("/etablissement/{etablissementCode}/agence")
    @Tag(name = "5. Référentiel Bancaire - Agences", description = "Gestion du référentiel des agences")
    public ResponseEntity<ApiResponse<Page<AgenceDto>>> getAgencesByEtablissementCode(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code établissement doit contenir exactement 3 chiffres") String etablissementCode,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<AgenceDto> agenceList = bankingService.getAgencesByEtablissementCode(etablissementCode, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<AgenceDto>>builder()
                    .success(true)
                    .message("Agence list retrieved successfully")
                    .statusCode(200)
                    .data(agenceList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Page<AgenceDto>>builder()
                    .success(false)
                    .message("Failed to retrieve agence list: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // update agence
    @PatchMapping("/etablissement/{etablissementCode}/agence/{agenceCode}")
    @Tag(name = "5. Référentiel Bancaire - Agences", description = "Gestion du référentiel des agences")
    public ResponseEntity<ApiResponse<AgenceDto>> updateAgence(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3}$", message = "Le code établissement doit contenir exactement 3 chiffres") String etablissementCode,
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3,20}$", message = "Le code agence doit contenir entre 3 et 20 chiffres") String agenceCode,
            @Valid @RequestBody AgenceUpdateDto agenceToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            AgenceDto updatedAgence = bankingService.updateAgence(etablissementCode, agenceCode, agenceToUpdateDTO,
                    currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<AgenceDto>builder()
                    .success(true)
                    .message("Agence updated successfully")
                    .statusCode(200)
                    .data(updatedAgence)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<AgenceDto>builder()
                    .success(false)
                    .message("Failed to update agence: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change agence status
    @PatchMapping("/agence/{agenceCode}/status")
    @Tag(name = "5. Référentiel Bancaire - Agences", description = "Gestion du référentiel des agences")
    public ResponseEntity<ApiResponse<Void>> changeAgenceStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{3,20}$", message = "Le code agence doit contenir entre 3 et 20 chiffres") String agenceCode,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            bankingService.changeAgenceStatus(agenceCode, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Agence status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change agence status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // agences bulk-import
    @PostMapping(value = "/agence/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "5. Référentiel Bancaire - Agences", description = "Gestion du référentiel des agences")
    public ResponseEntity<ApiResponse<BulkImportResponseDto>> importAgencesBulk(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "updateIfExists", defaultValue = "false") boolean updateIfExists,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            BulkImportResponseDto response = bankingService.importAgencesBulk(file, currentUser, updateIfExists);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BulkImportResponseDto>builder()
                    .success(true)
                    .message("Importation des agences exécutée")
                    .statusCode(HttpStatus.CREATED.value())
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<BulkImportResponseDto>builder()
                            .success(false)
                            .message("Failed to import agences: " + e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    // export agences
    @GetMapping("/agence/bulk-export")
    @Tag(name = "5. Référentiel Bancaire - Agences", description = "Gestion du référentiel des agences")
    public ResponseEntity<InputStreamResource> exportAgencesBulk(
            @RequestParam(required = false) String etablissementCode,
            @AuthenticationPrincipal UserEntity currentUser) {

        AgenceExcelExportDto response = bankingService.exportAgencesToExcel(etablissementCode);
        InputStreamResource file = new InputStreamResource(response.stream());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + response.fileName())
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    // status de compte routes

    // create account-status
    @PostMapping("/account-status")
    @Tag(name = "6. Référentiel Bancaire - Statuts de Compte", description = "Gestion du référentiel des statuts de compte")
    public ResponseEntity<ApiResponse<AccountStatusDto>> createAccountStatus(
            @Valid @RequestBody AccountStatusDto accountStatusToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            bankingService.createAccountStatus(accountStatusToCreateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<AccountStatusDto>builder()
                    .success(true)
                    .message("Account status created successfully")
                    .statusCode(201)
                    .data(accountStatusToCreateDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<AccountStatusDto>builder()
                    .success(false)
                    .message("Failed to create account status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get all account statuses with pagination
    @GetMapping("/account-status")
    @Tag(name = "6. Référentiel Bancaire - Statuts de Compte", description = "Gestion du référentiel des statuts de compte")
    public ResponseEntity<ApiResponse<Page<AccountStatusDto>>> getAllAccountStatuses(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<AccountStatusDto> accountStatusList = bankingService.getAllAccountStatuses(pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<AccountStatusDto>>builder()
                    .success(true)
                    .message("Account status list retrieved successfully")
                    .statusCode(200)
                    .data(accountStatusList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<AccountStatusDto>>builder()
                            .success(false)
                            .message("Failed to retrieve account status list: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    // update account-status
    @PatchMapping("/account-status/{code}")
    @Tag(name = "6. Référentiel Bancaire - Statuts de Compte", description = "Gestion du référentiel des statuts de compte")
    public ResponseEntity<ApiResponse<AccountStatusDto>> updateAccountStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code compte doit contenir exactement 2 chiffres") String code,
            @Valid @RequestBody AccountStatusUpdateDto accountStatusToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            AccountStatusDto updatedAccountStatus = bankingService.updateAccountStatus(code, accountStatusToUpdateDTO,
                    currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<AccountStatusDto>builder()
                    .success(true)
                    .message("Account status updated successfully")
                    .statusCode(200)
                    .data(updatedAccountStatus)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<AccountStatusDto>builder()
                    .success(false)
                    .message("Failed to update account status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change account-status status
    @PatchMapping("/account-status/{code}/status")
    @Tag(name = "6. Référentiel Bancaire - Statuts de Compte", description = "Gestion du référentiel des statuts de compte")
    public ResponseEntity<ApiResponse<Void>> changeAccountStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code compte doit contenir exactement 2 chiffres") String code,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            // change account status
            bankingService.changeAccountStatus(code, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Account status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change account status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

}
