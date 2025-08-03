package com.crud.restapi.service;

import com.crud.restapi.entity.ProfileEntity;
import com.crud.restapi.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private ProfileRepository profileRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        authService = new AuthService(profileRepository);
    }

    @Test
    void shouldReturnLoggedProfile_WhenAuthenticated() {
        // given
        String email = "user@example.com";
        ProfileEntity profile = new ProfileEntity();
        profile.setEmail(email);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(profileRepository.findByEmail(email)).thenReturn(Optional.of(profile));

            // when
            ProfileEntity result = authService.getLoggedProfile();

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
        }
    }

    @Test
    void shouldThrowException_WhenUserNotFound() {
        // given
        String email = "notfound@example.com";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(profileRepository.findByEmail(email)).thenReturn(Optional.empty());

            // when + then
            assertThrows(
                    org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                    () -> authService.getLoggedProfile()
            );
        }
    }
}
