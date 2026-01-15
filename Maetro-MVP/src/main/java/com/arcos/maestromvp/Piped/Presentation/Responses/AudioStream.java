package com.arcos.maestromvp.Piped.Presentation.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record AudioStream(String url) {}