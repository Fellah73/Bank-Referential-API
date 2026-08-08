package dz.banquealgerie.referential.service;

import dz.banquealgerie.referential.domain.entities.UserEntity;

public interface AuthService {

    String[] login(UserEntity user);

    UserEntity getMyInfo(String email);
}
