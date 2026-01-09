package com.arcos.maestromvp.Orchestrators;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.LLM.LLMClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class Orchestrator
{
    ContextOrchestrator contextOrchestrator;
    LLMClient llmClient;

    public Orchestrator(ContextOrchestrator contextOrchestrator, LLMClient llmClient) {
        this.contextOrchestrator = contextOrchestrator;
    }

    public void run(UserProfile userProfile) {

        //todo ICI sera la logique de détection du signe
    }

    public void orchestrate(UserProfile userProfile) {
        Prompt prompt = contextOrchestrator.getPrompt();
        llmClient.call();
    }
}
