package com.arcos.maestromvp.Piped.Service.Entities;

import java.util.List;

public class Playlist
{
    private List<Song> songList;
    public Playlist(List<Song> songList) {
        this.songList = songList;
    }

    public List<Song> getSongList() {
        return songList;
    }

}
