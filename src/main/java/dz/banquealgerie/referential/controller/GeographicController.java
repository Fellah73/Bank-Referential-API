package dz.banquealgerie.referential.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dz.banquealgerie.referential.domain.dto.ApiResponse;
import dz.banquealgerie.referential.domain.dto.geographic.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.PaysUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;
import dz.banquealgerie.referential.mappers.GeographicMapper;
import dz.banquealgerie.referential.service.GeographicService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/geographic")
@RequiredArgsConstructor
public class GeographicController {

    private final GeographicService geographicService;
    private final GeographicMapper geographicMapper;

    // pays routes

    // create country
    @PostMapping("/pays")
    public ResponseEntity<ApiResponse<PaysDto>> createCountry(
            @RequestBody PaysDto paysToCreateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {

            PaysEntity pays = geographicMapper.toPaysEntity(paysToCreateDTO);

            // created pays
            geographicService.createPays(pays, currentUser);

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
    public ResponseEntity<ApiResponse<PaysDto>> getCountryByCode(@PathVariable String code) {
        try {
            PaysEntity pays = geographicService.getPaysByCode(code);
            PaysDto paysDto = geographicMapper.toPaysDto(pays);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<PaysDto>builder()
                    .success(true)
                    .message("Pays retrieved successfully")
                    .statusCode(200)
                    .data(paysDto)
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
    public ResponseEntity<ApiResponse<PaysDto>> updateCountry(
            @PathVariable String code,
            @RequestBody PaysUpdateDto paysToUpdateDTO,
            @AuthenticationPrincipal UserEntity currentUser) {

        try {
            PaysEntity pays = geographicMapper.toPaysEntity(paysToUpdateDTO);

            // updated pays
            PaysDto updatedPays = geographicService.updatePays(code, pays, currentUser);

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
    @PatchMapping("/pays/status/{code}")
    public ResponseEntity<ApiResponse<Void>> changeCountryStatus(
            @PathVariable String code,
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

    // search countries by code or libelle
    @GetMapping("/pays/search")
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

}
