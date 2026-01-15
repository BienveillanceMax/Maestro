package com.arcos.maestromvp.Piped.Service; // Déplacer dans un package 'Services'

import com.arcos.maestromvp.Piped.Presentation.PipedApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PipedService {

    private final PipedApiClient pipedApiClient; // On injecte notre nouveau Client

    public PipedService(PipedApiClient pipedApiClient) {
        this.pipedApiClient = pipedApiClient;
    }

    public String searchAndGetUrl(String query) {
        try {
            var searchResponse = pipedApiClient.searchMusic(query);

            if (searchResponse == null || searchResponse.items().isEmpty()) return null;

            String videoId = searchResponse.items().get(0).url().replace("/watch?v=", "");

            var streamResponse = pipedApiClient.getStream(videoId);

            if (streamResponse != null && !streamResponse.audioStreams().isEmpty()) {
                return streamResponse.audioStreams().get(0).url();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}