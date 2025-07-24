package com.crud.restapi.service;

import com.crud.restapi.dto.ProfileDTO;

public interface ProfileService {
    /**
     * It will save the user details to database
     * @param profileDTO
     * @return profileDto
     */

    ProfileDTO createProfile(ProfileDTO profileDTO);
}