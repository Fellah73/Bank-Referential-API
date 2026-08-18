package dz.banquealgerie.referential.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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

    BulkImportResponseDto importEtablissementsBulk(MultipartFile file, UserEntity currentUser, boolean updateIfExists);
}
