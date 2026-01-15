package com.arcos.maestromvp.Piped.Presentation.Responses;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PipedSearchResponse(List<PipedItem> items) {}