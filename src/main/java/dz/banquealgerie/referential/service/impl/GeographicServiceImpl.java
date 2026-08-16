package dz.banquealgerie.referential.service.impl;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import dz.banquealgerie.referential.domain.dto.geographic.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.PaysUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;
import dz.banquealgerie.referential.mappers.GeographicMapper;
import dz.banquealgerie.referential.repositories.geographic.PaysRepository;
import dz.banquealgerie.referential.service.GeographicService;

@Service
public class GeographicServiceImpl implements GeographicService {

    private final PaysRepository paysRepository;
    private final GeographicMapper geographicMapper;

    public GeographicServiceImpl(PaysRepository paysRepository, GeographicMapper geographicMapper) {
        this.paysRepository = paysRepository;
        this.geographicMapper = geographicMapper;
    }

    // create country
    @Override
    public PaysEntity createPays(PaysDto paysDto, UserEntity currentUser) {

        try {
            
            // convert dto to entity
            PaysEntity pays = geographicMapper.toPaysEntity(paysDto);

            PaysEntity existingPays = paysRepository.findPaysByCode(pays.getCode());
            if (existingPays != null) {
                throw new RuntimeException("Country with code " + pays.getCode() + " already exists");
            }

            PaysEntity paysToSave = PaysEntity.builder()
                    .code(pays.getCode())
                    .abreviation(pays.getAbreviation())
                    .libelle(pays.getLibelle())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            // saving the new country
            paysRepository.save(paysToSave);

            return paysToSave;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all countries with pagination
    @Override
    public Page<PaysDto> getAllPays(Pageable pageable) {
        try {
            Page<PaysEntity> paysList = paysRepository.findAll(pageable);
            Page<PaysDto> paysDtoList = geographicMapper.toPaysDtoList(paysList);
            return paysDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get country by code
    @Override
    public PaysDto getPaysByCode(String code) {
        try {
            PaysEntity existingPays = paysRepository.findPaysByCode(code);
            if (existingPays == null) {
                throw new RuntimeException("Country with code " + code + " does not exist");
            }

            return geographicMapper.toPaysDto(existingPays);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update country
    @Override
    public PaysDto updatePays(String code, PaysUpdateDto paysDto, UserEntity currentUser) {
        try {
            PaysEntity existingPays = paysRepository.findPaysByCode(code);
            if (existingPays == null) {
                throw new RuntimeException("Country with code " + code + " does not exist");
            }

            PaysEntity pays = geographicMapper.toPaysEntity(paysDto);

            existingPays.setAbreviation(
                    pays.getAbreviation() != null ? pays.getAbreviation() : existingPays.getAbreviation());
            existingPays.setLibelle(pays.getLibelle() != null ? pays.getLibelle() : existingPays.getLibelle());
            existingPays.setUpdatedAt(java.time.LocalDateTime.now());
            existingPays.setUpdatedBy(currentUser.getUsername());

            // saving the updated country
            paysRepository.save(existingPays);

            PaysDto updatedPaysDto = geographicMapper.toPaysDto(existingPays);
            return updatedPaysDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change country status
    @Override
    public void changePaysStatus(String code, UserEntity currentUser) {
        try {
            PaysEntity existingPays = paysRepository.findPaysByCode(code);
            if (existingPays == null) {
                throw new RuntimeException("Country with code " + code + " does not exist");
            }

            existingPays.setIsActive(!existingPays.getIsActive());
            existingPays.setUpdatedAt(java.time.LocalDateTime.now());
            existingPays.setUpdatedBy(currentUser.getUsername());

            // saving the updated country
            paysRepository.save(existingPays);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // search countries by abreviation or libelle
    @Override
    public Page<PaysDto> searchPays(String searchTerm, Pageable pageable) {
        try {
            Page<PaysEntity> paysList = paysRepository.findAll(pageable);

            Streamable<PaysEntity> streamablePaysList = Streamable.of(paysList.getContent());

            // search by abreviation or libelle
            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamablePaysList = streamablePaysList
                        .filter(pays -> pays.getLibelle().toLowerCase().contains(searchTerm.toLowerCase())
                                || pays.getAbreviation().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamablePaysList.isEmpty()) {
                throw new RuntimeException("No countries found for the given search criteria");
            }

            // streamble to page
            paysList = streamablePaysList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            // entites to dto
            Page<PaysDto> paysDtoList = geographicMapper.toPaysDtoList(paysList);
            return paysDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
