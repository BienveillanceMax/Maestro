package com.arcos.maestromvp.LLM;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class LLMClient
{
    ChatClient chatClient;

    public LLMClient(ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder.build();
    }

    public String generateChatResponse(Prompt prompt) {
        return chatClient.prompt(prompt)
                .tools()
                .call()
                .content();
    }


}
