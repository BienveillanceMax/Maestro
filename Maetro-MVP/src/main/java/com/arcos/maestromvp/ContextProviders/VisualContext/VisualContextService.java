package com.arcos.maestromvp.ContextProviders.VisualContext;

import com.arcos.maestromvp.Tools.CameraTool;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class VisualContextService {

    private final CameraTool cameraTool;
    private final OllamaChatModel ollamaChatModel;

    @Autowired
    public VisualContextService(CameraTool cameraTool, OllamaChatModel ollamaChatModel) {
        this.cameraTool = cameraTool;
        this.ollamaChatModel = ollamaChatModel;
    }

    public String getVisualContext() {
        try {
            byte[] imageBytes = cameraTool.captureImage();

            var media = new Media(MimeTypeUtils.IMAGE_JPEG, new ByteArrayResource(imageBytes));

            // Using correct builder methods: text() and media()
            var userMessage = UserMessage.builder()
                    .text("Describe the room in this image. Give me the vibe of the environment.")
                    .media(media) // varargs supported
                    .build();

            ChatResponse response = ollamaChatModel.call(
                    new org.springframework.ai.chat.prompt.Prompt(
                            List.of(userMessage),
                            OllamaChatOptions.builder()
                                    .model("moondream")
                                    .build()
                    )
            );

            return response.getResult().getOutput().getText();

        } catch (IOException e) {
            return "Unable to capture visual context: " + e.getMessage();
        } catch (Exception e) {
            return "Error getting visual context: " + e.getMessage();
        }
    }
}
