package dz.banquealgerie.referential.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneDto;
import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysUpdateDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaDto;
import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaUpdateDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;
import dz.banquealgerie.referential.domain.entities.geographic.WilayaEntity;

public interface GeographicService {
    // pays methods
    PaysEntity createPays(PaysDto pays, UserEntity currentUser);

    PaysDto getPaysByCode(String code);

    PaysDto updatePays(String code, PaysUpdateDto pays, UserEntity currentUser);

    void changePaysStatus(String code, UserEntity currentUser);

    Page<PaysDto> getAllPays(Pageable pageable);

    Page<PaysDto> searchPays(String searchTerm, Pageable pageable);

    // wilaya methods
    WilayaEntity createWilaya(WilayaDto wilaya, UserEntity currentUser);

    Page<WilayaDto> getAllWilayas(Pageable pageable);

    WilayaDto getWilayaByCode(String code);

    WilayaDto updateWilaya(String code, WilayaUpdateDto wilaya, UserEntity currentUser);

    void changeWilayaStatus(String code, UserEntity currentUser);

    Page<WilayaDto> searchWilayas(String searchTerm, Pageable pageable);

    // commune methods
    void createCommune(String code, CommuneDto commune, UserEntity currentUser);

    Page<CommuneDto> getCommunesByWilayaCode(String wilayaCode, Pageable pageable);

    CommuneDto updateCommune(String code,String communeCode, CommuneUpdateDto commune, UserEntity currentUser);

    void changeCommuneStatus(String communeCode, UserEntity currentUser);

}
