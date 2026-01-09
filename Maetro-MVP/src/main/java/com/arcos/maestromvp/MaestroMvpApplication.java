package com.arcos.maestromvp;

import com.arcos.maestromvp.ContextProviders.UserContext.UserProfile;
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
        orchestrator.run(createUserProfile());
    }

    private static UserProfile createUserProfile()
    {
        //TODO SHOULD CREATE THE USER PROFILE BY ASKING THROUGH THE CONSOLE
        return new UserProfile(null,null,null,null);
    }
}

