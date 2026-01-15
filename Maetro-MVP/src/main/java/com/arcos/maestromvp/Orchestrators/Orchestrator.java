package com.arcos.maestromvp.Orchestrators;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.LLM.Entities.PlaylistResponse;
import com.arcos.maestromvp.LLM.Entities.SongResponse;
import com.arcos.maestromvp.LLM.LLMClient;
import com.arcos.maestromvp.Piped.Service.PipedService;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class Orchestrator
{
    ContextOrchestrator contextOrchestrator;
    LLMClient llmClient;
    PipedService pipedService;

    public Orchestrator(ContextOrchestrator contextOrchestrator, LLMClient llmClient, PipedService pipedService) {
        this.contextOrchestrator = contextOrchestrator;
        this.llmClient = llmClient;
        this.pipedService = pipedService;
    }

    public void run(UserProfile userProfile) {

        orchestrate(userProfile);
    }

    public void orchestrate(UserProfile userProfile) {
        Prompt prompt = contextOrchestrator.getPrompt(userProfile);
        PlaylistResponse playlist = llmClient.generateChatResponse(prompt);

        if (playlist != null && playlist.getSongList() != null) {
            for (SongResponse songResponse : playlist.getSongList()) {
                String searchQuery = songResponse.getTitle() + " " + songResponse.getArtist();
                String url = pipedService.searchAndGetUrl(searchQuery);
                if (url != null) {
                    System.out.println("Playing: " + songResponse.getTitle() + " by " + songResponse.getArtist());
                    System.out.println("URL: " + url);
                    // Simulate playback
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Playback interrupted.");
                    }
                } else {
                    System.out.println("Could not find URL for: " + songResponse.getTitle());
                }
            }
        }
    }
}
