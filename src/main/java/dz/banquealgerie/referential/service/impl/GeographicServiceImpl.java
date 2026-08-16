package dz.banquealgerie.referential.service.impl;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneDto;
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.geographic.CommuneEntity;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;
import dz.banquealgerie.referential.domain.entities.geographic.WilayaEntity;
import dz.banquealgerie.referential.mappers.GeographicMapper;
import dz.banquealgerie.referential.repositories.geographic.CommuneRepository;
import dz.banquealgerie.referential.repositories.geographic.PaysRepository;
import dz.banquealgerie.referential.repositories.geographic.WilayaRepository;
import dz.banquealgerie.referential.service.GeographicService;

@Service
public class GeographicServiceImpl implements GeographicService {

    private final PaysRepository paysRepository;
    private final WilayaRepository wilayaRepository;
    private final CommuneRepository communeRepository;

    private final GeographicMapper geographicMapper;

    public GeographicServiceImpl(PaysRepository paysRepository, WilayaRepository wilayaRepository,
            CommuneRepository communeRepository, GeographicMapper geographicMapper) {
        this.paysRepository = paysRepository;
        this.wilayaRepository = wilayaRepository;
        this.communeRepository = communeRepository;
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

    // wilaya methods

    // create wilaya
    @Override
    public WilayaEntity createWilaya(WilayaDto wilaya, UserEntity currentUser) {
        try {

            // convert dto to entity
            WilayaEntity wilayaEntity = geographicMapper.toWilayaEntity(wilaya);

            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(wilayaEntity.getCode());
            if (existingWilaya != null) {
                throw new RuntimeException("Wilaya with code " + wilayaEntity.getCode() + " already exists");
            }

            WilayaEntity wilayaToSave = WilayaEntity.builder()
                    .code(wilayaEntity.getCode())
                    .libelle(wilayaEntity.getLibelle())
                    .libelleAr(wilayaEntity.getLibelleAr())
                    .isActive(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser.getUsername())
                    .build();

            // saving the new wilaya
            wilayaRepository.save(wilayaToSave);

            return wilayaToSave;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get all wilayas with pagination
    @Override
    public Page<WilayaDto> getAllWilayas(Pageable pageable) {
        try {
            Page<WilayaEntity> wilayaList = wilayaRepository.findAll(pageable);
            Page<WilayaDto> wilayaDtoList = geographicMapper.toWilayaDtoList(wilayaList);
            return wilayaDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get wilaya by code
    @Override
    public WilayaDto getWilayaByCode(String code) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            return geographicMapper.toWilayaDto(existingWilaya);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update wilaya
    @Override
    public WilayaDto updateWilaya(String code, WilayaUpdateDto wilaya, UserEntity currentUser) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            existingWilaya.setLibelle(wilaya.getLibelle() != null ? wilaya.getLibelle() : existingWilaya.getLibelle());
            existingWilaya.setLibelleAr(
                    wilaya.getLibelleAr() != null ? wilaya.getLibelleAr() : existingWilaya.getLibelleAr());
            existingWilaya.setUpdatedAt(java.time.LocalDateTime.now());
            existingWilaya.setUpdatedBy(currentUser.getUsername());

            wilayaRepository.save(existingWilaya);
            return geographicMapper.toWilayaDto(existingWilaya);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change wilaya status
    @Override
    public void changeWilayaStatus(String code, UserEntity currentUser) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            existingWilaya.setIsActive(!existingWilaya.getIsActive());
            existingWilaya.setUpdatedAt(java.time.LocalDateTime.now());
            existingWilaya.setUpdatedBy(currentUser.getUsername());

            wilayaRepository.save(existingWilaya);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // search wilayas by abreviation or libelle
    @Override
    public Page<WilayaDto> searchWilayas(String searchTerm, Pageable pageable) {
        try {
            Page<WilayaEntity> wilayaList = wilayaRepository.findAll(pageable);

            Streamable<WilayaEntity> streamableWilayaList = Streamable.of(wilayaList.getContent());

            // search by abreviation or libelle
            if (searchTerm != null && !searchTerm.isEmpty()) {
                streamableWilayaList = streamableWilayaList
                        .filter(wilaya -> wilaya.getLibelle().toLowerCase().contains(searchTerm.toLowerCase())
                                || wilaya.getLibelleAr().toLowerCase().contains(searchTerm.toLowerCase()));
            }
            if (streamableWilayaList.isEmpty()) {
                throw new RuntimeException("No wilayas found for the given search criteria");
            }

            // streamble to page
            wilayaList = streamableWilayaList.toList().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));

            // entites to dto
            Page<WilayaDto> wilayaDtoList = geographicMapper.toWilayaDtoList(wilayaList);
            return wilayaDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // commune methods
    @Override
    public void createCommune(String code, CommuneDto commune, UserEntity currentUser) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            CommuneEntity existingCommune = communeRepository.findById(commune.getCode()).orElse(null);
            if (existingCommune != null) {
                throw new RuntimeException("Commune with code " + commune.getCode() + " already exists");
            }

            CommuneEntity newCommune = geographicMapper.toCommuneEntity(commune);
            newCommune.setWilaya(existingWilaya);
            newCommune.setCreatedAt(java.time.LocalDateTime.now());
            newCommune.setCreatedBy(currentUser.getUsername());

            communeRepository.save(newCommune);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // get communes by wilaya code with pagination
    @Override
    public Page<CommuneDto> getCommunesByWilayaCode(String wilayaCode, Pageable pageable) {
        try {
            Page<CommuneEntity> communeList = communeRepository.findByWilayaCode(wilayaCode, pageable);
            return geographicMapper.toCommuneDtoList(communeList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // update commune
    @Override
    public CommuneDto updateCommune(String code, String communeCode, CommuneUpdateDto commune, UserEntity currentUser) {
        try {
            WilayaEntity existingWilaya = wilayaRepository.findWilayaByCode(code);
            if (existingWilaya == null) {
                throw new RuntimeException("Wilaya with code " + code + " does not exist");
            }

            CommuneEntity existingCommune = communeRepository.findById(communeCode).orElse(null);
            if (existingCommune == null) {
                throw new RuntimeException("Commune with code " + communeCode + " does not exist");
            }

            existingCommune.setLibelle(
                    commune.getLibelle() != null ? commune.getLibelle() : existingCommune.getLibelle());
            existingCommune.setLibelleAr(
                    commune.getLibelleAr() != null ? commune.getLibelleAr() : existingCommune.getLibelleAr());
            existingCommune.setUpdatedAt(java.time.LocalDateTime.now());
            existingCommune.setUpdatedBy(currentUser.getUsername());

            communeRepository.save(existingCommune);
            return geographicMapper.toCommuneDto(existingCommune);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // change commune status
    @Override
    public void changeCommuneStatus(String communeCode, UserEntity currentUser) {
        try {
            CommuneEntity existingCommune = communeRepository.findById(communeCode).orElse(null);
            if (existingCommune == null) {
                throw new RuntimeException("Commune with code " + communeCode + " does not exist");
            }

            existingCommune.setIsActive(!existingCommune.getIsActive());
            existingCommune.setUpdatedAt(java.time.LocalDateTime.now());
            existingCommune.setUpdatedBy(currentUser.getUsername());

            communeRepository.save(existingCommune);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
