package com.arcos.maestromvp.LLM.Entities;

import com.arcos.maestromvp.Piped.Service.Entities.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaylistResponse
{
    private List<SongResponse> songList;
    public PlaylistResponse(List<SongResponse> songList) {
        this.songList = songList;
    }

    public List<SongResponse> getSongList() {
        return songList;
    }
}
