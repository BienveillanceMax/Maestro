package com.arcos.maestromvp.ContextProviders.UserContext.Presentation.Dto;

import java.util.List;

public record UserProfileDto(
        List<String> likedGenres,
        List<String> hatedGenres,
        Boolean openToDiscovery,
        String additionnalInformation) {
}
