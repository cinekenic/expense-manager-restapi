package com.crud.restapi.controller;

import com.crud.restapi.dto.ProfileDTO;
import com.crud.restapi.io.AuthRequest;
import com.crud.restapi.io.AuthResponse;
import com.crud.restapi.io.ProfileRequest;
import com.crud.restapi.io.ProfileResponse;
import com.crud.restapi.service.CustomUserDetailsService;
import com.crud.restapi.service.ProfileService;
import com.crud.restapi.service.TokenBlackListService;
import com.crud.restapi.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
//@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://cinekenic.github.io")
public class AuthController {

    private final ModelMapper modelMapper;
    private final ProfileService profileService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlackListService tokenBlackListService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ProfileResponse createProfile(@Valid @RequestBody ProfileRequest profileRequest) {
        log.info("API /register is called {}", profileRequest);
        ProfileDTO profileDTO = mapToProfileDTO(profileRequest);
        profileDTO = profileService.createProfile(profileDTO);
        log.info("Printing the profile dto details {}", profileDTO);
        return mapToProfileResponse(profileDTO);
    }

    @PostMapping("/login")
    public AuthResponse authenticateProfile(@RequestBody AuthRequest authRequest) throws Exception {
        log.info("API /login is called {}", authRequest);
        authenticate(authRequest);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new AuthResponse(token, authRequest.getEmail());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/signout")
    public void signout(HttpServletRequest request) {
        String jwtToken = extractJwtTokenFromRequest(request);
        if (jwtToken != null) {
            tokenBlackListService.addTokenToBlacklist(jwtToken);
        }
    }

    private String extractJwtTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void authenticate(AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        }catch (DisabledException ex) {
            throw new Exception("Profile disabled");
        }catch (BadCredentialsException ex) {
            throw new Exception("Bad Credentials");
        }
    }

    private ProfileDTO mapToProfileDTO(ProfileRequest profileRequest) {
        return modelMapper.map(profileRequest, ProfileDTO.class);
    }

    private ProfileResponse mapToProfileResponse(ProfileDTO profileDTO) {
        return modelMapper.map(profileDTO, ProfileResponse.class);
    }
}
