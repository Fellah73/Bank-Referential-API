package dz.banquealgerie.referential.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;

public interface RegulatoryService {
    // legal form routes
    FormeJuridiqueDto createLegalForm(FormeJuridiqueDto formeJuridiqueDto, UserEntity currentUser);

    Page<FormeJuridiqueDto> getAllLegalForms(Pageable pageable);

    FormeJuridiqueDto updateLegalForm(String code, FormeJuridiqueUpdateDto formeJuridiqueToUpdateDTO,
            UserEntity currentUser);

    void changeLegalFormStatus(String code, UserEntity currentUser);
}
