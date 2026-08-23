package dz.banquealgerie.referential.service;

import java.io.ByteArrayInputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import dz.banquealgerie.referential.domain.dto.banking.accountStatus.AccountStatusDto;
import dz.banquealgerie.referential.domain.dto.banking.accountStatus.AccountStatusUpdateDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceExcelExportDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceUpdateDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.BulkImportResponseDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.banking.EtablissementEntity;

public interface BankingService {
        // etablissement methods
        EtablissementEntity createEtablissment(EtablissementDto etablissementDto, UserEntity currentUser);

        Page<EtablissementDto> getAllEtablissement(Pageable pageable);

        EtablissementDto getEtablissementByCode(String code);

        EtablissementDto updateEtablissement(String code, EtablissementUpdateDto etablissementToUpdateDTO,
                        UserEntity currentUser);

        void changeEtablissementStatus(String code, UserEntity currentUser);

        Page<EtablissementDto> searchEtablissements(String searchTerm, Pageable pageable);

        BulkImportResponseDto importEtablissementsBulk(MultipartFile file, UserEntity currentUser,
                        boolean updateIfExists);

        ByteArrayInputStream exportEtablissementsToExcel();

        // agence methods
        AgenceDto createAgence(String etablissementCode, AgenceDto agenceToCreateDTO, UserEntity currentUser);

        Page<AgenceDto> getAgencesByEtablissementCode(String etablissementCode, Pageable pageable);

        AgenceDto updateAgence(String etablissementCode, String agenceCode, AgenceUpdateDto agenceToUpdateDTO,
                        UserEntity currentUser);

        Page<AgenceDto> searchAgences(String searchTerm, Pageable pageable);

        void changeAgenceStatus(String agenceCode, UserEntity currentUser);

        BulkImportResponseDto importAgencesByEtablissementBulk(MultipartFile file, UserEntity currentUser, boolean updateIfExists, String etablissementCode);

        BulkImportResponseDto importAgencesBulk(MultipartFile file, UserEntity currentUser, boolean updateIfExists);

        AgenceExcelExportDto exportAgencesByEtablissementToExcel(String etablissementCode);

        AgenceExcelExportDto exportAgencesToExcel();

        // account-status methods
        AccountStatusDto createAccountStatus(AccountStatusDto accountStatusToCreateDTO, UserEntity currentUser);

        Page<AccountStatusDto> getAllAccountStatuses(Pageable pageable);

        AccountStatusDto updateAccountStatus(String code, AccountStatusUpdateDto accountStatusToUpdateDTO,
                        UserEntity currentUser);

        void changeAccountStatus(String code, UserEntity currentUser);
}
