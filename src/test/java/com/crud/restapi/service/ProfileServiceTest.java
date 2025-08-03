package com.crud.restapi.service;

import com.crud.restapi.dto.ProfileDTO;
import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.exceptions.ItemExistsException;
import com.crud.restapi.repository.ProfileRepository;
import com.crud.restapi.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private ProfileRepository profileRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        modelMapper = new ModelMapper();
        profileService = new ProfileServiceImpl(profileRepository, modelMapper, passwordEncoder);
    }

    @Test
    void shouldCreateProfileSuccessfully() {
        // given
        ProfileDTO input = ProfileDTO.builder()
                .email("test@example.com")
                .password("plainPass")
                .build();

        ProfileEntity savedEntity = new ProfileEntity();
        savedEntity.setEmail("test@example.com");
        savedEntity.setPassword("encodedPass");

        when(profileRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(profileRepository.save(any(ProfileEntity.class))).thenReturn(savedEntity);

        // when
        ProfileDTO result = profileService.createProfile(input);

        // then
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(profileRepository).save(any(ProfileEntity.class));
    }

    @Test
    void shouldThrowExceptionIfEmailExists() {
        // given
        ProfileDTO input = new ProfileDTO();
        input.setEmail("existing@example.com");

        when(profileRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // when + then
        assertThrows(ItemExistsException.class, () -> profileService.createProfile(input));
        verify(profileRepository, never()).save(any());
    }
}
