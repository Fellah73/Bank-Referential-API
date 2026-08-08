package dz.banquealgerie.referential.mappers;

import org.mapstruct.Mapper;

import dz.banquealgerie.referential.domain.dto.auth.LoginDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    UserEntity toEntity(LoginDto loginDto);
}
