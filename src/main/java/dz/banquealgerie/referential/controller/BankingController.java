package dz.banquealgerie.referential.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.BulkImportResponseDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.service.BankingService;
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

}
