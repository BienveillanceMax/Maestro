package com.arcos.maestromvp.LLM;

import com.arcos.maestromvp.LLM.Entities.PlaylistResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LLMClient
{
    ChatClient chatClient;

    @Autowired
    public LLMClient(@Qualifier("mistralChatClient") ChatClient mistralChatClient){
        this.chatClient = mistralChatClient;
    }

    public PlaylistResponse generateChatResponse(Prompt prompt) {
        log.info(prompt.getContents());
        return chatClient.prompt(prompt)
                .tools()
                .call()
                .responseEntity(PlaylistResponse.class)
                .getEntity();
    }

}
