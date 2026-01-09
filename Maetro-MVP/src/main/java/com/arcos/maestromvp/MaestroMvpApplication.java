package com.arcos.maestromvp;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.ContextProviders.WeatherContext.WeatherContextService;
import com.arcos.maestromvp.Orchestrators.Orchestrator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MaestroMvpApplication
{

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MaestroMvpApplication.class, args);
        Orchestrator orchestrator =  context.getBean(Orchestrator.class);
        WeatherContextService weatherContextService = (WeatherContextService) context.getBean("weatherContextService");
        UserProfile userProfile = context.getBean(UserProfile.class);
        System.out.println( weatherContextService.getLocalWeather());

        orchestrator.run(createUserProfile(userProfile));
    }

    private static UserProfile createUserProfile(UserProfile userProfile)
    {
        //TODO SHOULD CREATE THE USER PROFILE BY ASKING THROUGH THE CONSOLE
        return userProfile;
    }
}

