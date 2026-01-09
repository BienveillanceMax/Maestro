package com.arcos.maestromvp.Orchestrators;

import com.arcos.maestromvp.ContextProviders.UserContext.UserMoodService;
import com.arcos.maestromvp.LLM.PromptBuilderService;
import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.ContextProviders.VisualContext.VisualContextService;
import com.arcos.maestromvp.ContextProviders.WeatherContext.WeatherContext;
import com.arcos.maestromvp.ContextProviders.WeatherContext.WeatherContextService;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContextOrchestrator
{
    private WeatherContextService weatherContextService;
    private VisualContextService visualContextService;
    private UserMoodService usermoodService;
    private UserProfile userProfile;

    private PromptBuilderService promptBuilder;

    @Autowired
    public ContextOrchestrator(WeatherContextService weatherContextService, VisualContextService visualContextService, UserProfile userProfile, PromptBuilderService promptBuilder, UserMoodService usermoodService) {
        this.weatherContextService = weatherContextService;
        this.visualContextService = visualContextService;
        this.userProfile = userProfile;
        this.promptBuilder = promptBuilder;
        this.usermoodService = usermoodService;
    }

    public Prompt getPrompt() {
        WeatherContext weatherContext = weatherContextService.getLocalWeather();
        String visualContext = visualContextService.getVisualContext();
        String userMood = UserMoodService.getUserMood();

        Prompt prompt = promptBuilder.createPrompt(userProfile,weatherContext,visualContext, userMood);
        return prompt;
    }
}
