package com.arcos.maestromvp.LLM;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.ContextProviders.WeatherContext.WeatherContext;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class PromptBuilderService
{
    public Prompt createPrompt(UserProfile userProfile, WeatherContext weatherContext, String visualContext, String userMood) {
        return null;
        //TODO
    }
}
