package com.arcos.maestromvp.ContextProviders.WeatherContext;

import org.springframework.stereotype.Service;

@Service
public class WeatherContextService
{


    public WeatherContext getLocalWeather()
    {
        String coordinates = ""; // As of now, this is hardcoded, when away from mvp stage, should be dynamic.

        return OpenMeteoClient.getLocalWeather(coordinates);
    }
}
