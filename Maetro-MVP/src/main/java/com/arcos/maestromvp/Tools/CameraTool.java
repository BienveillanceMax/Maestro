package com.arcos.maestromvp.Tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Component;

@Component
public class CameraTool {

    public byte[] captureImage() throws IOException {
        String tempFileName = "snapshot_" + System.currentTimeMillis() + ".jpg";
        Path path = Paths.get(tempFileName);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "fswebcam",
                "--no-banner",
                "-r", "1280x720",
                "--jpeg", "85",
                "-D", "1",
                tempFileName
        );

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("fswebcam exited with code " + exitCode);
            }

            if (!Files.exists(path)) {
                throw new IOException("Captured image file not found");
            }

            return Files.readAllBytes(path);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted during image capture", e);
        } finally {
            // Ensure deletion happens
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignored) {
                // Ignore cleanup errors
            }
        }
    }
}
