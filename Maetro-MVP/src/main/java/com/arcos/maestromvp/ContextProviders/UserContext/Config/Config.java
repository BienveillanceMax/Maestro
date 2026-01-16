package com.arcos.maestromvp.ContextProviders.UserContext.Config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Applique la règle sur toutes les URL de l'API
                .allowedOrigins(
                        "http://localhost",       // Ton front Docker (port 80)
                        "http://localhost:3000",
                        "http://127.0.0.1"        // Parfois le navigateur utilise l'IP
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Autorise tout
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}