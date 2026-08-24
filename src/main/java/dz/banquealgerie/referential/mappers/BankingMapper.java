package dz.banquealgerie.referential.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dz.banquealgerie.referential.domain.dto.banking.accountStatus.AccountStatusDto;
import dz.banquealgerie.referential.domain.dto.banking.accountStatus.AccountStatusUpdateDto;
import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementDto;
import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementUpdateDto;
import dz.banquealgerie.referential.domain.entities.banking.AccountStatusEntity;
import dz.banquealgerie.referential.domain.entities.banking.AgenceEntity;
import dz.banquealgerie.referential.domain.entities.banking.EtablissementEntity;

@Component
public class BankingMapper {

    // etablisselent methods
    public EtablissementEntity toEtablissementEntity(EtablissementDto dto) {
        return EtablissementEntity.builder()
                .code(dto.getCode())
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public EtablissementEntity toEtablissementEntity(EtablissementUpdateDto dto) {
        return EtablissementEntity.builder()
                .abreviation(dto.getAbreviation())
                .libelle(dto.getLibelle())
                .build();
    }

    public EtablissementDto toEtablissementDto(EtablissementEntity etablissement) {
        EtablissementDto dto = new EtablissementDto();
        dto.setCode(etablissement.getCode());
        dto.setAbreviation(etablissement.getAbreviation());
        dto.setLibelle(etablissement.getLibelle());
        dto.setIsActive(etablissement.getIsActive());

        return dto;
    }

    public Page<EtablissementDto> toEtablissementDtoList(Page<EtablissementEntity> entities) {
        return entities.map(this::toEtablissementDto);
    }

    // agence methods
    public AgenceEntity toAgenceEntity(AgenceDto agenceDto) {
        return AgenceEntity.builder()
                .code(agenceDto.getCode())
                .libelle(agenceDto.getLibelle())
                .build();
    }

    public AgenceDto toAgenceDto(AgenceEntity agenceEntity) {
        AgenceDto agenceDto = new AgenceDto();
        agenceDto.setCode(agenceEntity.getCode());
        agenceDto.setLibelle(agenceEntity.getLibelle());
        agenceDto.setIsActive(agenceEntity.getIsActive());
        return agenceDto;
    }

    public Page<AgenceDto> toAgenceDtoList(Page<AgenceEntity> entities) {
        return entities.map(this::toAgenceDto);
    }

    // acount-status methods
    public AccountStatusEntity toAccountStatusEntity(AccountStatusDto accountStatusDto) {
        return AccountStatusEntity.builder()
                .code(accountStatusDto.getCode())
                .libelle(accountStatusDto.getLibelle())
                .build();
    }

    public AccountStatusDto toAccountStatusDto(AccountStatusEntity accountStatus) {
        AccountStatusDto dto = new AccountStatusDto();
        dto.setCode(accountStatus.getCode());
        dto.setLibelle(accountStatus.getLibelle());
        dto.setIsActive(accountStatus.getIsActive());
        return dto;
    }

    public Page<AccountStatusDto> toAccountStatusDtoList(Page<AccountStatusEntity> entities) {
        return entities.map(this::toAccountStatusDto);
    }

    public AccountStatusEntity toAccountStatusEntity(AccountStatusUpdateDto accountStatusDto) {
        AccountStatusEntity existingEntity = new AccountStatusEntity();
        existingEntity.setLibelle(accountStatusDto.getLibelle());
        return existingEntity;
    }

}