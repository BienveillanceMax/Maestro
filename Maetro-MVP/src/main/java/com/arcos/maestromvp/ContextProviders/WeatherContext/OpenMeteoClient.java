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
                case 0 -> isDay ? "Ensoleillé" : "Ciel dégagé";
                case 1 -> isDay ? "Globalement ensoleillé" : "Nuit claire";
                case 2 -> "Partiellement nuageux";
                case 3 -> "Couvert";
                case 45 -> "Brouillard";
                case 48 -> "Brouillard givrant";
                case 51 -> "Bruine légère";
                case 53 -> "Bruine";
                case 55 -> "Bruine forte";
                case 56 -> "Bruine verglaçante légère";
                case 57 -> "Bruine verglaçante";
                case 61 -> "Pluie légère";
                case 63 -> "Pluie";
                case 65 -> "Pluie forte";
                case 66 -> "Pluie verglaçante légère";
                case 67 -> "Pluie verglaçante";
                case 71 -> "Neige légère";
                case 73 -> "Neige";
                case 75 -> "Neige forte";
                case 77 -> "Grains de neige";
                case 80 -> "Averses de pluie légères";
                case 81 -> "Averses de pluie";
                case 82 -> "Violentes averses de pluie";
                case 85 -> "Averses de neige légères";
                case 86 -> "Averses de neige";
                case 95 -> "Orage";
                case 96 -> "Orage faible avec grêle";
                case 99 -> "Orage avec grêle";
                default -> "Inconnu";
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
