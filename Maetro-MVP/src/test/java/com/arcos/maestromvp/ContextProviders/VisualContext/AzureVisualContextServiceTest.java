package com.arcos.maestromvp.ContextProviders.VisualContext;

import com.arcos.maestromvp.Tools.CameraTool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AzureVisualContextServiceTest {

    @Mock
    private CameraTool cameraTool;

    @InjectMocks
    private AzureVisualContextService azureVisualContextService;

    @Test
    public void testGetImageTags_CameraFailure() throws IOException {
        // Arrange
        when(cameraTool.captureImage()).thenThrow(new IOException("Camera not found"));
        ReflectionTestUtils.setField(azureVisualContextService, "endpoint", "https://mock.cognitiveservices.azure.com/");
        ReflectionTestUtils.setField(azureVisualContextService, "key", "mockKey");

        // Act
        String result = azureVisualContextService.getImageTags();

        // Assert
        assertTrue(result.contains("Error capturing image"));
    }
}
