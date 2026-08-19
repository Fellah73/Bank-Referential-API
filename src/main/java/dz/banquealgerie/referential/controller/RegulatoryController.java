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
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueUpdateDto;
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<Page<FormeJuridiqueDto>>builder()
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
            @RequestBody FormeJuridiqueUpdateDto formeJuridiqueToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            // updated forme juridique
            FormeJuridiqueDto updatedFormeJuridique = regulatoryService.updateLegalForm(code, formeJuridiqueToUpdateDTO, currentUser);

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

}
