package com.crud.restapi.service.impl;

import com.crud.restapi.dto.ProfileDTO;
import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.exceptions.ItemExistsException;
import com.crud.restapi.repository.ProfileRepository;
import com.crud.restapi.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ProfileDTO profileDTO;
    private ProfileEntity profileEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        profileDTO = ProfileDTO.builder()
                .email("test@example.com")
                .password("plainPassword")
                .name("John")
                .build();

        profileEntity = new ProfileEntity();
        profileEntity.setEmail("test@example.com");
        profileEntity.setPassword("encodedPassword");
        profileEntity.setProfileId(UUID.randomUUID().toString());
    }

    @Test
    void shouldCreateProfile_WhenEmailNotExists() {
        // given
        when(profileRepository.existsByEmail(profileDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(profileDTO.getPassword())).thenReturn("encodedPassword");
        when(modelMapper.map(profileDTO, ProfileEntity.class)).thenReturn(profileEntity);
        when(profileRepository.save(any(ProfileEntity.class))).thenReturn(profileEntity);
        when(modelMapper.map(profileEntity, ProfileDTO.class)).thenReturn(profileDTO);

        // when
        ProfileDTO result = profileService.createProfile(profileDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(profileRepository).save(any(ProfileEntity.class));
    }

    @Test
    void shouldThrowException_WhenEmailAlreadyExists() {
        // given
        when(profileRepository.existsByEmail(profileDTO.getEmail())).thenReturn(true);

        // when + then
        assertThrows(ItemExistsException.class, () -> profileService.createProfile(profileDTO));
        verify(profileRepository, never()).save(any(ProfileEntity.class));
    }
}
