package com.arcos.maestromvp.ContextProviders.WeatherContext;

public record WeatherContext(
    String description,      // Traduction du weathercode (ex: "Légère pluie")
    double temperature,      // Température ressentie
    boolean isDay,           // Pour l'énergie jour/nuit
    int cloudCover,          // Pour la "luminosité" de la musique
    double precipitation,    // Pour le côté "cosy/pluie"
    String windCondition     // "Calme", "Venteux", "Tempête"
) {

}
