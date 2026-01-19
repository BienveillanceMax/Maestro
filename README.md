# Maestro-MVP

**Maestro** is an intelligent, agentic music orchestrator that curates and plays music based on your real-time mood, visual context, and musical preferences. By combining computer vision, local LLMs, and cloud-based cognitive services, Maestro creates a personalized auditory atmosphere that adapts to your environment.

## Features

*   **Visual Context Awareness**: Analyzes your environment using a webcam to understand the scene (e.g., working, relaxing, exercising).
*   **Mood Detection**: Infers user mood from visual cues and contextual data, as of now unimplemented.
*   **Gesture Control**: Precise "Pinch" gesture detection using MediaPipe to trigger music generation without touching the keyboard.
*   **AI-Powered Curation**: Utilizes Large Language Models (Mistral/Ollama) to generate context-aware playlists.
*   **Multi-Modal Analysis**: Combines Azure Computer Vision (for factual tagging) and Moondream (for qualitative scene description).
*   **Hybrid Audio Playback**: Supports audio playback via Lavalink/YouTube and Spotify.

## Architecture

Maestro operates as a set of containerized microservices:

*   **Maestro Core (Spring Boot)**: The brain of the operation. Orchestrates context gathering, LLM interaction, and audio playback.
*   **Visual Service (Python)**: Handles webcam input, performs hand landmark detection (MediaPipe), and serves the video feed. Triggers the core upon detecting a valid gesture.
*   **Ollama**: Local inference server running the `moondream` model for visual understanding.
*   **Frontend (React/Vite)**: A lightweight interface to view the camera feed and system status.

## Prerequisites

*   **Docker** & **Docker Compose**
*   **Webcam** (mapped to `/dev/video0` in the container)
*   **API Keys**:
    *   **Azure Computer Vision** (Endpoint & Key)
    *   **Mistral AI** (API Key)
    *   **Spotify** (Client ID & Secret)

## Configuration

Create a `.env` file in the root directory or export the following environment variables before running:

```bash
AZURE_VISION_ENDPOINT="your_azure_endpoint"
AZURE_VISION_KEY="your_azure_key"
MISTRAL_API_KEY="your_mistral_key"
```

## Getting Started


1.  **Set your environment variables** (as shown above).

2.  **Start the application:**
    ```bash
    docker-compose up --build
    ```

    *The first run may take a while as it pulls the necessary Docker images and the `moondream` model for Ollama.*

3.  **Access the Interface:**
    Open your browser and navigate to `http://localhost:3000`.

## 🎮 Usage

1.  **Position yourself** in front of the camera.
2.  **Trigger the Agent**:
    *   Raise your hand.
    *   Perform a **Pinch** gesture (touch thumb to index finger).
    *   **Hold** the pinch for **1 second**.
    *   **Visual Feedback**:
        *   🟡 **Yellow**: Charging (Hold the pinch).
        *   🟢 **Green**: Triggered! (Request sent).
        *   🔵 **Blue**: Cooldown (Wait 5 seconds).
3.  **Listen**: Maestro will analyze the scene and start playing a curated playlist suited to the moment.

## Project Structure

*   `Maetro-MVP/`: Java Spring Boot backend application.
*   `visual-service/`: Python Flask application for Computer Vision.
*   `maestro-front/`: Frontend web application.
*   `docker-compose.yml`: Container orchestration configuration.
