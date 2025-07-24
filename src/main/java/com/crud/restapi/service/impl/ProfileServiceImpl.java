package com.crud.restapi.service.impl;

import com.crud.restapi.dto.ProfileDTO;
import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.repository.ProfileRepository;
import com.crud.restapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProfileDTO createProfile(ProfileDTO profileDTO) {
        // Mapowanie DTO -> Entity
        ProfileEntity profileEntity = mapToProfileEntity(profileDTO);

        // Generowanie UUID
        profileEntity.setProfileId(UUID.randomUUID().toString());

        // Zapis do bazy danych
        ProfileEntity savedEntity = profileRepository.save(profileEntity);

        // Logowanie zapisanej encji
        log.info("Saved profile entity: {}", savedEntity);

        // Mapowanie zapisanej encji z powrotem na DTO z timestampami
        return mapToProfileDTO(savedEntity);
    }

    private ProfileDTO mapToProfileDTO(ProfileEntity profileEntity) {
        return modelMapper.map(profileEntity, ProfileDTO.class);
    }

    private ProfileEntity mapToProfileEntity(ProfileDTO profileDTO) {
        return modelMapper.map(profileDTO, ProfileEntity.class);
    }
}
