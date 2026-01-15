package com.arcos.maestromvp.Orchestrators;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.LLM.LLMClient;
import com.arcos.maestromvp.Piped.Service.PipedService;
import com.arcos.maestromvp.Spotify.Service.Entities.PlayList;
import com.arcos.maestromvp.Spotify.Service.Entities.Song;
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
        PlayList query = llmClient.generateChatResponse(prompt);
        //for (Song song : query.getSongList())
            //pipedService.searchAndGetUrl(song.getUri());
    }
}
