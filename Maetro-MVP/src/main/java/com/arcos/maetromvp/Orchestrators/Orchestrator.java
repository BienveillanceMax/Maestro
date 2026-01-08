package com.arcos.maetromvp.Orchestrators;

import com.arcos.maetromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maetromvp.LLM.LLMClient;
import org.springframework.ai.chat.prompt.Prompt;

public class Orchestrator
{
    ContextOrchestrator contextOrchestrator;
    LLMClient llmClient;

    public Orchestrator(ContextOrchestrator contextOrchestrator, LLMClient llmClient) {
        this.contextOrchestrator = contextOrchestrator;
    }

    public void Orchestrate(UserProfile userProfile){
        Prompt prompt = contextOrchestrator.getPrompt();
        llmClient.call();

    }
}
