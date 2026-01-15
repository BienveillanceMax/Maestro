package com.arcos.maestromvp.LLM;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIConfig
{

    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(MistralAiEmbeddingModel mistralAiEmbeddingModel) {
        return mistralAiEmbeddingModel;
    }

    @Bean
    public ChatClient mistralChatClient(MistralAiChatModel mistralAiChatModel) {
        return ChatClient.builder(mistralAiChatModel).build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel).build();
    }
}