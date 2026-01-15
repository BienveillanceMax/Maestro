package com.arcos.maestromvp.Audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Slf4j
@Service
public class LavaplayerAudioService implements AudioService {

    private AudioPlayerManager playerManager;
    private AudioPlayer player;
    private TrackScheduler scheduler;
    private LocalAudioOutput audioOutput;
    private Thread outputThread;

    @PostConstruct
    public void init() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        player = playerManager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);

        // Start the local audio output
        audioOutput = new LocalAudioOutput(player, playerManager.getConfiguration().getOutputFormat());
        outputThread = new Thread(audioOutput, "LocalAudioOutputThread");
        outputThread.setDaemon(true);
        outputThread.start();

        log.info("LavaplayerAudioService initialized.");
    }

    @PreDestroy
    public void cleanup() {
        if (audioOutput != null) audioOutput.stop();
        if (player != null) player.destroy();
    }

    @Override
    public void play(String query) {
        String search = "ytsearch:" + query;
        log.info("Searching and playing: {}", search);

        playerManager.loadItem(search, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                log.info("Track loaded: {}", track.getInfo().title);
                scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                 if (!playlist.getTracks().isEmpty()) {
                     AudioTrack track = playlist.getTracks().get(0);
                     log.info("Playlist loaded, playing first track: {}", track.getInfo().title);
                     scheduler.queue(track);
                 } else {
                     log.warn("Playlist empty for query: {}", query);
                 }
            }

            @Override
            public void noMatches() {
                log.warn("No matches found for: {}", query);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.error("Load failed for: {}", query, exception);
            }
        });
    }
}
