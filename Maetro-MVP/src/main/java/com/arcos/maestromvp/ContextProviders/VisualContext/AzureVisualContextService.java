package com.arcos.maestromvp.ContextProviders.VisualContext;

import com.arcos.maestromvp.Tools.CameraTool;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AzureVisualContextService {

    private final CameraTool cameraTool;

    @Value("${AZURE_VISION_ENDPOINT}")
    private String endpoint;

    @Value("${AZURE_VISION_KEY}")
    private String key;

    @Autowired
    public AzureVisualContextService(CameraTool cameraTool) {
        this.cameraTool = cameraTool;
    }

    public String getImageTags() {
        try {
            byte[] imageBytes = cameraTool.captureImage();

            // Client instantiation is lightweight, but typically we could wrap this in a protected method for easier testing
            ComputerVisionClient client = getClient();

            List<VisualFeatureTypes> features = new ArrayList<>();
            features.add(VisualFeatureTypes.TAGS);
            features.add(VisualFeatureTypes.COLOR);

            ImageAnalysis analysis = client.computerVision().analyzeImageInStream()
                    .withImage(imageBytes)
                    .withVisualFeatures(features)
                    .execute();

            String tags = analysis.tags().stream()
                    .map(ImageTag::name)
                    .collect(Collectors.joining(", "));

            // Safely handle color info
            String colorDesc = "";
            if (analysis.color() != null) {
                List<String> colors = analysis.color().dominantColors();
                String accent = analysis.color().accentColor();

                colorDesc = "Dominant Colors: " + (colors != null ? String.join(", ", colors) : "None");
                if (accent != null) {
                    colorDesc += ", Accent Color: #" + accent;
                }
            }

            return "Tags: " + tags + ", " + colorDesc;

        } catch (IOException e) {
            return "Error capturing image: " + e.getMessage();
        } catch (Exception e) {
            return "Error analyzing image: " + e.getMessage();
        }
    }

    // Protected for testing override
    protected ComputerVisionClient getClient() {
        return ComputerVisionManager.authenticate(key).withEndpoint(endpoint);
    }
}
