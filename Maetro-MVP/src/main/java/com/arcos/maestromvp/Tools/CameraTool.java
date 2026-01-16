package com.arcos.maestromvp.Tools;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

@Component
public class CameraTool {

    private final RestTemplate restTemplate;
    private final String visualServiceUrl = "http://visual-service:5000/api/frame";

    public CameraTool() {
        this.restTemplate = new RestTemplate();
    }

    // Constructor for testing
    public CameraTool(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] captureImage() throws IOException {
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    visualServiceUrl,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );

            byte[] imageBytes = response.getBody();
            if (imageBytes == null || imageBytes.length == 0) {
                throw new IOException("Received empty image from visual service. Status: " + response.getStatusCode());
            }
            return imageBytes;
        } catch (Exception e) {
            throw new IOException("Failed to fetch image from visual service: " + e.getMessage(), e);
        }
    }
}
