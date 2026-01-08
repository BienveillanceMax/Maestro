package com.arcos.maetromvp.Orchestrators;

import com.arcos.maetromvp.ContextProviders.UserContext.UserMoodService;
import com.arcos.maetromvp.LLM.PromptBuilderService;
import com.arcos.maetromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maetromvp.ContextProviders.VisualContext.VisualContextService;
import com.arcos.maetromvp.ContextProviders.WeatherContext.WeatherContext;
import com.arcos.maetromvp.ContextProviders.WeatherContext.WeatherContextService;
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
