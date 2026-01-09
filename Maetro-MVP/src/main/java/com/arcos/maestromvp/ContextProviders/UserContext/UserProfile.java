package com.arcos.maestromvp.ContextProviders.UserContext;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public record UserProfile(
        List<String> likedGenres,
        List<String> hatedGenres,
        Boolean openToDiscovery,
        String additionnalInformation

)
{
    public UserProfile {
    }

    public String toPromptString(){
       //TODO
       return "";
   }
}

