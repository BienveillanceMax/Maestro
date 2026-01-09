package com.arcos.maestromvp.ContextProviders.WeatherContext.Location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class IpApiLocationProvider implements LocationProvider {

    private static final Logger logger = LoggerFactory.getLogger(IpApiLocationProvider.class);
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getCoordinates() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://ip-api.com/json/"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.path("status").asText().equals("success")) {
                    double lat = root.path("lat").asDouble();
                    double lon = root.path("lon").asDouble();
                    return lat + "," + lon;
                } else {
                    logger.warn("IP-API returned success=false: {}", root.path("message").asText());
                }
            } else {
                logger.warn("IP-API returned status code: {}", response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to fetch location from IP-API", e);
        }
        return "";
    }
}

