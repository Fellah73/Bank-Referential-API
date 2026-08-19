package dz.banquealgerie.referential.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dz.banquealgerie.referential.domain.dto.ApiResponse;
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneDto;
import dz.banquealgerie.referential.domain.dto.regulatory.ActivityDto;
import dz.banquealgerie.referential.domain.dto.regulatory.ActivityUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifResponseDto;
import dz.banquealgerie.referential.domain.dto.regulatory.NifUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.service.RegulatoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/regulatory")
@RequiredArgsConstructor
public class RegulatoryController {

    private final RegulatoryService regulatoryService;

    // forme-juridique routes

    // create legal form
    @PostMapping("/legal-forms")
    public ResponseEntity<ApiResponse<FormeJuridiqueDto>> createLegalForm(
            @Valid @RequestBody FormeJuridiqueDto formeJuridiqueDto,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            regulatoryService.createLegalForm(formeJuridiqueDto, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<FormeJuridiqueDto>builder()
                    .success(true)
                    .message("Forme juridique created successfully")
                    .statusCode(201)
                    .data(formeJuridiqueDto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<FormeJuridiqueDto>builder()
                    .success(false)
                    .message("Failed to create forme juridique: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get all legal forms with pagination
    @GetMapping("/legal-forms")
    public ResponseEntity<ApiResponse<Page<FormeJuridiqueDto>>> getAllLegalForms(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<FormeJuridiqueDto> legalFormsList = regulatoryService.getAllLegalForms(pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<FormeJuridiqueDto>>builder()
                    .success(true)
                    .message("Legal forms list retrieved successfully")
                    .statusCode(200)
                    .data(legalFormsList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<FormeJuridiqueDto>>builder()
                            .success(false)
                            .message("Failed to retrieve legal forms list: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    // update legal-form
    @PatchMapping("/legal-forms/{code}")
    public ResponseEntity<ApiResponse<FormeJuridiqueDto>> updateLegalForm(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code forme juridique doit contenir exactement 2 chiffres") String code,
            @Valid @RequestBody FormeJuridiqueUpdateDto formeJuridiqueToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            // updated forme juridique
            FormeJuridiqueDto updatedFormeJuridique = regulatoryService.updateLegalForm(code, formeJuridiqueToUpdateDTO,
                    currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<FormeJuridiqueDto>builder()
                    .success(true)
                    .message("Forme juridique updated successfully")
                    .statusCode(200)
                    .data(updatedFormeJuridique)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<FormeJuridiqueDto>builder()
                    .success(false)
                    .message("Failed to update forme juridique: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change legal-form status
    @PatchMapping("/legal-forms/{code}/status")
    public ResponseEntity<ApiResponse<Void>> changeLegalFormStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{2}$", message = "Le code forme juridique doit contenir exactement 2 chiffres") String code,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            // change legal form status
            regulatoryService.changeLegalFormStatus(code, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Legal form status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change legal form status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // activity routes

    // create activity
    @PostMapping("/activities")
    public ResponseEntity<ApiResponse<ActivityDto>> createActivity(
            @Valid @RequestBody ActivityDto activityToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            regulatoryService.createActivity(activityToCreateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ActivityDto>builder()
                    .success(true)
                    .message("Activity created successfully")
                    .statusCode(201)
                    .data(activityToCreateDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<ActivityDto>builder()
                    .success(false)
                    .message("Failed to create activity: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get all activities with pagination
    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<Page<ActivityDto>>> getAllActivities(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<ActivityDto> activitiesList = regulatoryService.getAllActivities(pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<ActivityDto>>builder()
                    .success(true)
                    .message("Activities list retrieved successfully")
                    .statusCode(200)
                    .data(activitiesList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<ActivityDto>>builder()
                            .success(false)
                            .message("Failed to retrieve activities list: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    // update activities
    @PatchMapping("/activities/{code}")
    public ResponseEntity<ApiResponse<ActivityDto>> updateActivity(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{6}$", message = "Le code activité doit contenir exactement 6 chiffres") String code,
            @Valid @RequestBody ActivityUpdateDto activityToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            ActivityDto updatedActivity = regulatoryService.updateActivity(code, activityToUpdateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ActivityDto>builder()
                    .success(true)
                    .message("Activity updated successfully")
                    .statusCode(200)
                    .data(updatedActivity)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<ActivityDto>builder()
                    .success(false)
                    .message("Failed to update activity: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change activity status
    @PatchMapping("/activities/{code}/status")
    public ResponseEntity<ApiResponse<Void>> changeActivityStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{6}$", message = "Le code activité doit contenir exactement 6 chiffres") String code,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            regulatoryService.changeActivityStatus(code, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Activity status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change activity status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // nif routes

    // create nif
    @PostMapping("/nif")
    public ResponseEntity<ApiResponse<NifDto>> createNif(
            @Valid @RequestBody NifDto nifToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            regulatoryService.createNif(nifToCreateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<NifDto>builder()
                    .success(true)
                    .message("NIF created successfully")
                    .statusCode(201)
                    .data(nifToCreateDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<NifDto>builder()
                    .success(false)
                    .message("Failed to create NIF: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // get all nif with pagination
    @GetMapping("/nif")
    public ResponseEntity<ApiResponse<Page<NifResponseDto>>> getAllNifs(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<NifResponseDto> nifList = regulatoryService.getAllNifs(pageable);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Page<NifResponseDto>>builder()
                    .success(true)
                    .message("NIF list retrieved successfully")
                    .statusCode(200)
                    .data(nifList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<NifResponseDto>>builder()
                            .success(false)
                            .message("Failed to retrieve NIF list: " + e.getMessage())
                            .statusCode(500)
                            .build());
        }
    }

    // update nif
    @PatchMapping("/nif/{code}")
    public ResponseEntity<ApiResponse<NifResponseDto>> updateNif(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{15,20}$", message = "Le code NIF doit contenir entre 15 et 20 chiffres") String code,
            @Valid @RequestBody NifUpdateDto nifToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            NifResponseDto updatedNif = regulatoryService.updateNif(code, nifToUpdateDTO, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<NifResponseDto>builder()
                    .success(true)
                    .message("NIF updated successfully")
                    .statusCode(200)
                    .data(updatedNif)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<NifResponseDto>builder()
                    .success(false)
                    .message("Failed to update NIF: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

    // change nif status
    @PatchMapping("/nif/{code}/status")
    public ResponseEntity<ApiResponse<Void>> changeNifStatus(
            @Validated @PathVariable @Pattern(regexp = "^[0-9]{15,20}$", message = "Le code NIF doit contenir entre 15 et 20 chiffres") String code,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            regulatoryService.changeNifStatus(code, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder()
                    .success(true)
                    .message("NIF status changed successfully")
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to change NIF status: " + e.getMessage())
                    .statusCode(500)
                    .build());
        }
    }

}
