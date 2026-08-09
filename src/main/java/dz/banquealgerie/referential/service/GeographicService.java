package dz.banquealgerie.referential.service;

import dz.banquealgerie.referential.domain.dto.geographic.PaysDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;

public interface GeographicService {
    PaysEntity createPays(PaysEntity pays, UserEntity currentUser);

    PaysEntity getPaysByCode(String code);

    PaysDto updatePays(String code, PaysEntity pays, UserEntity currentUser);

    void changePaysStatus(String code, UserEntity currentUser);
}
