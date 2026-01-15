package com.arcos.maestromvp.Piped.Presentation;


import com.arcos.maestromvp.Piped.Presentation.Responses.PipedSearchResponse;
import com.arcos.maestromvp.Piped.Presentation.Responses.PipedStreamResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Component
public class PipedApiClient {

    private final RestClient restClient;
    public PipedApiClient(RestClient.Builder builder) {

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = builder
                .baseUrl("https://pipedapi.kavin.rocks")
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", "MaestroMvp/1.0")
                .build();
    }

    public PipedSearchResponse searchMusic(String query) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("filter", "music_songs")
                        .build())
                .retrieve()
                .body(PipedSearchResponse.class);
    }

    public PipedStreamResponse getStream(String videoId) {
        return restClient.get()
                .uri("/streams/{videoId}", videoId)
                .retrieve()
                .body(PipedStreamResponse.class);
    }
}