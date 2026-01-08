package com.arcos.maetromvp.LLM;

import com.arcos.maetromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maetromvp.ContextProviders.WeatherContext.WeatherContext;
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
