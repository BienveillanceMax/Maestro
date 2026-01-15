package com.arcos.maestromvp.LLM;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.ContextProviders.WeatherContext.WeatherContext;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilderService
{
    public Prompt createPrompt(UserProfile userProfile, WeatherContext weatherContext, String visualContext, String userMood) {

        String weatherDescription = interpretWeather(weatherContext);
        String userPreferences = interpretUserPreferences(userProfile);
        String visualDescription = interpretVisuals(visualContext);
        String moodDescription = interpretMood(userMood);

        String systemText = """
                You are a sophisticated Music Agent connected to a music player.
                Your goal is to curate the perfect playlist for the user's current moment.
                
                You have access to context about the weather, the visual environment, the user's mood, and their musical preferences.
                
                Follow these guidelines:
                1. ANALYZE: Deeply understand the emotional and atmospheric vibe from the context.
                2. DECIDE: Select a specific genre, playlist style, or track that perfectly matches this vibe.
                3. ACTION: Your output should be a specific list of songs (Title and Artist).
                
                Generate a playlist of songs that best fit the context.
                """;

        SystemMessage systemMessage = new SystemMessage(systemText);

        StringBuilder userTextBuilder = new StringBuilder();
        userTextBuilder.append("Here is the current context:\n\n");

        if (weatherDescription != null && !weatherDescription.isEmpty()) {
            userTextBuilder.append("- Weather Context: ").append(weatherDescription).append("\n");
        }

        if (visualDescription != null && !visualDescription.isEmpty()) {
            userTextBuilder.append("- Visual Context: ").append(visualDescription).append("\n");
        }

        if (moodDescription != null && !moodDescription.isEmpty()) {
            userTextBuilder.append("- User Mood: ").append(moodDescription).append("\n");
        }

        if (userPreferences != null && !userPreferences.isEmpty()) {
            userTextBuilder.append("- User Preferences: ").append(userPreferences).append("\n");
        }

        userTextBuilder.append("\nBased on this, generate a playlist of songs.");

        UserMessage userMessage = new UserMessage(userTextBuilder.toString());

        return new Prompt(List.of(systemMessage, userMessage));
    }

    private String interpretWeather(WeatherContext context) {
        if (context == null) return "Unknown weather.";

        StringBuilder sb = new StringBuilder();
        sb.append(context.description());
        sb.append(", felt temperature: ").append(context.felt_temperature()).append("°C");

        if (context.isDay()) {
            sb.append(", Daytime");
        } else {
            sb.append(", Nighttime");
        }

        if (context.precipitation() > 0) {
            sb.append(", Raining (").append(context.precipitation()).append("mm - suggests: Acoustic, Jazz, Lo-fi)");
        }

        if (context.cloudCover() > 50) {
            sb.append(", Cloudy");
        } else {
            sb.append(", Clear sky");
        }

        return sb.toString();
    }

    private String interpretMood(String mood) {
        if (mood == null) return "Unknown mood";
        return switch (mood.toLowerCase()) {
            case "happy" -> "Happy (High valence, Major key, Upbeat, Energetic)";
            case "sad" -> "Sad (Low valence, Minor key, Slow tempo, Acoustic, Melancholic)";
            case "angry" -> "Angry (High energy, Intense, Distorted, Fast tempo, Aggressive)";
            case "surprised" -> "Surprised (Dynamic, Unpredictable, Exciting, Novel)";
            case "neutral" -> "Neutral (Moderate tempo, Balanced energy, Background, Ambient)";
            default -> mood;
        };
    }

    private String interpretUserPreferences(UserProfile profile) {
        if (profile == null) return "No user profile available.";

        StringBuilder sb = new StringBuilder();

        if (profile.getLikedGenres() != null && !profile.getLikedGenres().isEmpty()) {
            sb.append("Likes: ").append(String.join(", ", profile.getLikedGenres())).append(". ");
        }

        if (profile.getHatedGenres() != null && !profile.getHatedGenres().isEmpty()) {
            sb.append("Hates: ").append(String.join(", ", profile.getHatedGenres())).append(". ");
        }

        if (Boolean.TRUE.equals(profile.getOpenToDiscovery())) {
            sb.append("Open to musical discovery. ");
        } else {
            sb.append("Prefers familiar genres. ");
        }

        if (profile.getAdditionnalInformation() != null && !profile.getAdditionnalInformation().isEmpty()) {
            sb.append("Note: ").append(profile.getAdditionnalInformation());
        }

        return sb.toString();
    }

    private String interpretVisuals(String visualContext) {
        if (visualContext == null || visualContext.isEmpty()) return "No visual context available.";
        return visualContext;
    }
}
