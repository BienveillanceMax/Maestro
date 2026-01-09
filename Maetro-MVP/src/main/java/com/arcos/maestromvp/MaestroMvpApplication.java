package com.arcos.maestromvp;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
import com.arcos.maestromvp.ContextProviders.VisualContext.AzureVisualContextService;
import com.arcos.maestromvp.ContextProviders.VisualContext.VisualContextService;
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
        VisualContextService visualContextService =  context.getBean(VisualContextService.class);
        AzureVisualContextService azureVisualContextService =  context.getBean(AzureVisualContextService.class);


        //WeatherContextService weatherContextService = (WeatherContextService) context.getBean("weatherContextService");
        //System.out.println( weatherContextService.getLocalWeather());


        //System.out.println( visualContextService.getVisualContext());

        //System.out.println( visualContextService.getVisualContext());

        System.out.println(azureVisualContextService.getImageTags());


        UserProfile userProfile = context.getBean(UserProfile.class);
        orchestrator.run(createUserProfile(userProfile));
    }

    private static UserProfile createUserProfile(UserProfile userProfile)
    {
        //TODO SHOULD CREATE THE USER PROFILE BY ASKING THROUGH THE CONSOLE
        return userProfile;
    }
}

