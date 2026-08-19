package dz.banquealgerie.referential.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueDto;
import dz.banquealgerie.referential.domain.dto.regulatory.FormeJuridiqueUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.regulatory.FormeJuridiqueEntity;
import dz.banquealgerie.referential.mappers.RegulatoryMapper;
import dz.banquealgerie.referential.repositories.regulatory.FormJuridiqueRepository;
import dz.banquealgerie.referential.service.RegulatoryService;

@Service
public class RegulatoryServiceImpl implements RegulatoryService {
    private final RegulatoryMapper regulatoryMapper;
    private final FormJuridiqueRepository formJuridiqueRepository;

    public RegulatoryServiceImpl(FormJuridiqueRepository formJuridiqueRepository, RegulatoryMapper regulatoryMapper) {
        this.formJuridiqueRepository = formJuridiqueRepository;
        this.regulatoryMapper = regulatoryMapper;
    }

    // form-juridique methods

    // create form juridique
    @Override
    public FormeJuridiqueDto createLegalForm(FormeJuridiqueDto formeJuridiqueDto, UserEntity currentUser) {

        try {

            FormeJuridiqueEntity formeJuridique = regulatoryMapper.toFormeJuridiqueEntity(formeJuridiqueDto);

            FormeJuridiqueEntity existingFormeJuridique = formJuridiqueRepository.findById(formeJuridique.getCode())
                    .orElse(null);
            if (existingFormeJuridique != null) {
                throw new RuntimeException("Forme juridique with code " + formeJuridique.getCode() + " already exists");
            }

            FormeJuridiqueEntity formeJuridiqueToSave = FormeJuridiqueEntity.builder()
                    .code(formeJuridique.getCode())
                    .abreviation(formeJuridique.getAbreviation())
                    .libelle(formeJuridique.getLibelle())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            formJuridiqueRepository.save(formeJuridiqueToSave);

            return regulatoryMapper.toFormeJuridiqueDto(formeJuridiqueToSave);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all legal forms with pagination
    @Override
    public Page<FormeJuridiqueDto> getAllLegalForms(Pageable pageable) {
        try {
            Page<FormeJuridiqueEntity> legalFormsList = formJuridiqueRepository.findAll(pageable);
            Page<FormeJuridiqueDto> legalFormsDtoList = regulatoryMapper.toFormeJuridiqueDtoList(legalFormsList);
            return legalFormsDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update legal form
    @Override
    public FormeJuridiqueDto updateLegalForm(String code, FormeJuridiqueUpdateDto formeJuridiqueToUpdateDTO,
            UserEntity currentUser) {
        try {
            FormeJuridiqueEntity existingFormeJuridique = formJuridiqueRepository.findById(code)
                    .orElse(null);
            if (existingFormeJuridique == null) {
                throw new RuntimeException("Forme juridique with code " + code + " does not exist");
            }

            FormeJuridiqueEntity formeJuridique = regulatoryMapper.toFormeJuridiqueEntity(formeJuridiqueToUpdateDTO);

            existingFormeJuridique.setAbreviation(
                    formeJuridique.getAbreviation() != null ? formeJuridique.getAbreviation()
                            : existingFormeJuridique.getAbreviation());
            existingFormeJuridique.setLibelle(formeJuridique.getLibelle() != null ? formeJuridique.getLibelle()
                    : existingFormeJuridique.getLibelle());
            existingFormeJuridique.setUpdatedAt(java.time.LocalDateTime.now());
            existingFormeJuridique.setUpdatedBy(currentUser.getUsername());

            formJuridiqueRepository.save(existingFormeJuridique);

            FormeJuridiqueDto updatedFormeJuridiqueDto = regulatoryMapper.toFormeJuridiqueDto(existingFormeJuridique);
            return updatedFormeJuridiqueDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

     // change legal form status
    @Override
    public void changeLegalFormStatus(String code, UserEntity currentUser) {
        try {
            FormeJuridiqueEntity existingFormeJuridique = formJuridiqueRepository.findById(code)
                    .orElse(null);
            if (existingFormeJuridique == null) {
                throw new RuntimeException("Legal form with code " + code + " does not exist");
            }

            existingFormeJuridique.setIsActive(!existingFormeJuridique.getIsActive());
            existingFormeJuridique.setUpdatedAt(java.time.LocalDateTime.now());
            existingFormeJuridique.setUpdatedBy(currentUser.getUsername());

            // saving the updated legal form
            formJuridiqueRepository.save(existingFormeJuridique);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
