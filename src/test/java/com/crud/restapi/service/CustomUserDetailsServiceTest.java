package com.crud.restapi.service;

import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private ProfileRepository profileRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        customUserDetailsService = new CustomUserDetailsService(profileRepository);
    }

    @Test
    void shouldLoadUserByUsername() {
        // given
        String email = "test@example.com";
        String password = "encoded-password";
        ProfileEntity profile = new ProfileEntity();
        profile.setEmail(email);
        profile.setPassword(password);

        when(profileRepository.findByEmail(email)).thenReturn(Optional.of(profile));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // then
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).isEmpty();

        verify(profileRepository).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        String email = "notfound@example.com";
        when(profileRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Profile not found for the email " + email);
    }
}
