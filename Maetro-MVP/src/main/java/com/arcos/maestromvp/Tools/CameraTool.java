package com.arcos.maestromvp.Tools;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CameraTool {

    private final RestTemplate restTemplate;
    private final String visualServiceUrl = "http://visual-service:5000/api/frame";

    public CameraTool() {
        this.restTemplate = new RestTemplate();
    }

    public byte[] captureImage() throws IOException {
        try {
            byte[] imageBytes = restTemplate.getForObject(visualServiceUrl, byte[].class);
            if (imageBytes == null || imageBytes.length == 0) {
                throw new IOException("Received empty image from visual service");
            }
            return imageBytes;
        } catch (Exception e) {
            throw new IOException("Failed to fetch image from visual service: " + e.getMessage(), e);
        }
    }
}
