package com.arcos.maestromvp.ContextProviders.VisualContext;

import com.arcos.maestromvp.Tools.CameraTool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisualContextServiceTest {

    @Mock
    private CameraTool cameraTool;

    @Mock
    private OllamaChatModel ollamaChatModel;

    @InjectMocks
    private VisualContextService visualContextService;

    @Test
    public void testGetVisualContext_Success() throws IOException {
        // Arrange
        byte[] mockImage = new byte[]{1, 2, 3};
        when(cameraTool.captureImage()).thenReturn(mockImage);

        // Mock ChatResponse structure
        // Generation -> AssistantMessage -> text
        AssistantMessage assistantMessage = new AssistantMessage("A cozy room with a vibe.");
        Generation generation = new Generation(assistantMessage);
        ChatResponse chatResponse = new ChatResponse(List.of(generation));

        when(ollamaChatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        // Act
        String result = visualContextService.getVisualContext();

        // Assert
        assertEquals("A cozy room with a vibe.", result);
    }

    @Test
    public void testGetVisualContext_CameraFailure() throws IOException {
        // Arrange
        when(cameraTool.captureImage()).thenThrow(new IOException("Camera not found"));

        // Act
        String result = visualContextService.getVisualContext();

        // Assert
        assertTrue(result.contains("Unable to capture visual context"));
    }
}
