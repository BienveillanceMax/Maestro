package com.arcos.maestromvp.ContextProviders.WeatherContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class OpenMeteoClient {

    private static final Logger logger = LoggerFactory.getLogger(OpenMeteoClient.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenMeteoClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // Constructor for testing
    public OpenMeteoClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public WeatherContext getLocalWeather(String coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        try {
            // Expected format "lat,lon"
            String[] parts = coordinates.split(",");
            if (parts.length != 2) {
                logger.warn("Invalid coordinates format: {}", coordinates);
                return null;
            }
            String lat = parts[0].trim();
            String lon = parts[1].trim();

            String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m,apparent_temperature,is_day,precipitation,weather_code,cloud_cover,wind_speed_10m", lat, lon);

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseWeatherResponse(response.body());
            } else {
                logger.error("OpenMeteo API failed with status: {}", response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching weather data", e);
            Thread.currentThread().interrupt();
        }

        return null;
    }

    private WeatherContext parseWeatherResponse(String jsonResponse) throws IOException {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode current = root.path("current");

        int weatherCode = current.path("weather_code").asInt();
        double apparentTemperature = current.path("apparent_temperature").asDouble();
        int isDayInt = current.path("is_day").asInt();
        int cloudCover = current.path("cloud_cover").asInt();
        double precipitation = current.path("precipitation").asDouble();
        double windSpeed = current.path("wind_speed_10m").asDouble();

        boolean isDay = (isDayInt == 1);

        String description = getWeatherDescription(weatherCode,isDay);
        String windCondition = getWindCondition(windSpeed);

        return new WeatherContext(description, apparentTemperature, isDay, cloudCover, precipitation, windCondition);
    }

    private String getWeatherDescription(int code, boolean isDay) {
        return switch (code) {
            case 0 -> isDay ? "Sunny" : "Clear";
            case 1 -> isDay ? "Mainly Sunny" : "Mainly Clear";
            case 2 -> "Partly Cloudy";
            case 3 -> "Cloudy";
            case 45 -> "Foggy";
            case 48 -> "Rime Fog";
            case 51 -> "Light Drizzle";
            case 53 -> "Drizzle";
            case 55 -> "Heavy Drizzle";
            case 56 -> "Light Freezing Drizzle";
            case 57 -> "Freezing Drizzle";
            case 61 -> "Light Rain";
            case 63 -> "Rain";
            case 65 -> "Heavy Rain";
            case 66 -> "Light Freezing Rain";
            case 67 -> "Freezing Rain";
            case 71 -> "Light Snow";
            case 73 -> "Snow";
            case 75 -> "Heavy Snow";
            case 77 -> "Snow Grains";
            case 80 -> "Light Showers";
            case 81 -> "Showers";
            case 82 -> "Heavy Showers";
            case 85 -> "Light Snow Showers";
            case 86 -> "Snow Showers";
            case 95 -> "Thunderstorm";
            case 96 -> "Light Thunderstorms With Hail";
            case 99 -> "Thunderstorm With Hail";
            default -> "Unknown";
        };
    }


    private String getWindCondition(double speedKmH) {
        if (speedKmH < 20) {
            return "Calme";
        } else if (speedKmH <= 50) {
            return "Venteux";
        } else {
            return "Tempête";
        }
    }
}
