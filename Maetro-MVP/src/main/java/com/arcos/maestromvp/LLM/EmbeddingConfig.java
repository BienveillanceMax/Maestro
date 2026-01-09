package com.arcos.maestromvp.LLM;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingConfig {

    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(MistralAiEmbeddingModel mistralAiEmbeddingModel) {
        return mistralAiEmbeddingModel;
    }
}