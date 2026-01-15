package com.arcos.maestromvp.Spotify.Service.Entities;

public class Song
{
    private String id;
    private String title;

    public String getUri() {
        return "spotify:track:" + this.id;
    }
}
