package com.arcos.maestromvp.ContextProviders.WeatherContext;

import com.arcos.maestromvp.ContextProviders.WeatherContext.Location.LocationProvider;
import org.springframework.stereotype.Service;

@Service
public class WeatherContextService {

    private final LocationProvider locationProvider;
    private final OpenMeteoClient openMeteoClient;

    public WeatherContextService(LocationProvider locationProvider, OpenMeteoClient openMeteoClient) {
        this.locationProvider = locationProvider;
        this.openMeteoClient = openMeteoClient;
    }

    public WeatherContext getLocalWeather() {
        String coordinates = locationProvider.getCoordinates();
        return openMeteoClient.getLocalWeather(coordinates);
    }
}
