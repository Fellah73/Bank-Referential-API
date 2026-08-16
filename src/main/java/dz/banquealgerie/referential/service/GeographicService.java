package dz.banquealgerie.referential.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.banquealgerie.referential.domain.dto.geographic.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.PaysUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;

public interface GeographicService {
    PaysEntity createPays(PaysDto pays, UserEntity currentUser);

    PaysDto getPaysByCode(String code);

    PaysDto updatePays(String code, PaysUpdateDto pays, UserEntity currentUser);

    void changePaysStatus(String code, UserEntity currentUser);

    Page<PaysDto> getAllPays(Pageable pageable);

    Page<PaysDto> searchPays(String searchTerm, Pageable pageable);
}
