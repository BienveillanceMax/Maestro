package com.arcos.maestromvp.Spotify.Service.Entities;

import java.util.List;

public class PlayList
{
    private List<Song> songList;
    public PlayList(List<Song> songList) {
        this.songList = songList;
    }

    public List<Song> getSongList() {
        return songList;
    }

}
