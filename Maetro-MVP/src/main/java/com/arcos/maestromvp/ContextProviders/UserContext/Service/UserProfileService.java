package com.arcos.maestromvp.ContextProviders.UserContext.Service;

import com.arcos.maestromvp.ContextProviders.UserContext.Presentation.Dto.UserProfileDto;
import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.ContextProviders.UserContext.UserProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final UserProfile userProfile;

    @Autowired
    public UserProfileService(UserProfile userProfile, UserProfileManager userProfileManager) {
        this.userProfile = userProfile;
    }

    public void createUserProfile(UserProfileDto dto) {
        userProfile.setUserProfile(dto);

        // Debug purposes
        System.out.println(userProfile.getLikedGenres());
        System.out.println(userProfile.getHatedGenres());
        System.out.println(userProfile.getAdditionnalInformation());
    }
}
