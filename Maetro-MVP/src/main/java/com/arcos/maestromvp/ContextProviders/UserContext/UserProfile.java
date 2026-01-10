package com.arcos.maestromvp.ContextProviders.UserContext;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserProfile
{
    List<String> likedGenres;
    List<String> hatedGenres;
    Boolean openToDiscovery;
    String additionnalInformation;


    public UserProfile() {
    }

    public void setUserProfile(List<String> likedGenres, List<String> hatedGenres, Boolean openToDiscovery, String additionalInformation) {
        this.likedGenres = likedGenres;
        this.hatedGenres = hatedGenres;
        this.openToDiscovery = openToDiscovery;
        this.additionnalInformation = additionalInformation;
    }

    public List<String> getLikedGenres() {
        return likedGenres;
    }

    public List<String> getHatedGenres() {
        return hatedGenres;
    }

    public Boolean getOpenToDiscovery() {
        return openToDiscovery;
    }

    public String getAdditionnalInformation() {
        return additionnalInformation;
    }
}

