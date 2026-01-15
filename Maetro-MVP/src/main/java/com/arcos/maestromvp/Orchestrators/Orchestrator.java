package com.arcos.maestromvp.Orchestrators;

import com.arcos.maestromvp.Audio.AudioService;
import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.LLM.Entities.PlaylistResponse;
import com.arcos.maestromvp.LLM.Entities.SongResponse;
import com.arcos.maestromvp.LLM.LLMClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class Orchestrator
{
    ContextOrchestrator contextOrchestrator;
    LLMClient llmClient;
    AudioService audioService;

    public Orchestrator(ContextOrchestrator contextOrchestrator, LLMClient llmClient, AudioService audioService) {
        this.contextOrchestrator = contextOrchestrator;
        this.llmClient = llmClient;
        this.audioService = audioService;
    }

    public void run(UserProfile userProfile) {
        orchestrate(userProfile);
    }

    public void orchestrate(UserProfile userProfile) {
        Prompt prompt = contextOrchestrator.getPrompt(userProfile);
        PlaylistResponse playlist = llmClient.generateChatResponse(prompt);

        if (playlist != null && playlist.getSongList() != null) {
            for (SongResponse songResponse : playlist.getSongList()) {
                String searchQuery = songResponse.getTitle() + " - " + songResponse.getArtist();
                System.out.println("Queueing: " + searchQuery);
                audioService.play(searchQuery);
            }
        }
    }
}
