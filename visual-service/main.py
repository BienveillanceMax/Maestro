import cv2
import mediapipe as mp
import time
import threading
import requests
import math
from flask import Flask, Response, jsonify
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
import numpy as np

app = Flask(__name__)

# --- Global State ---
output_frame = None
lock = threading.Lock()
# Timestamp of the last successful trigger
last_trigger_time = 0
# Trigger cooldown in seconds
TRIGGER_COOLDOWN = 5.0
# Threshold for pinch (distance between thumb and index tip)
PINCH_THRESHOLD = 0.05
# Duration to hold pinch before triggering (in seconds)
TRIGGER_HOLD_TIME = 1.0
# URL of the Java service trigger
JAVA_TRIGGER_URL = "http://locahost:8080/api/trigger"

# --- MediaPipe Setup ---
# Initialize the Hand Landmarker
base_options = python.BaseOptions(model_asset_path='hand_landmarker.task')
options = vision.HandLandmarkerOptions(
    base_options=base_options,
    num_hands=2,
    min_hand_detection_confidence=0.5,
    min_hand_presence_confidence=0.5,
    min_tracking_confidence=0.5)
detector = vision.HandLandmarker.create_from_options(options)

# Hand connections (list of tuples of indices)
# 0 is Wrist.
# Thumb: 0->1->2->3->4
# Index: 0->5->6->7->8
# Middle: 0->9->10->11->12
# Ring: 0->13->14->15->16
# Pinky: 0->17->18->19->20
HAND_CONNECTIONS = [
    (0, 1), (1, 2), (2, 3), (3, 4),
    (0, 5), (5, 6), (6, 7), (7, 8),
    (0, 9), (9, 10), (10, 11), (11, 12),
    (0, 13), (13, 14), (14, 15), (15, 16),
    (0, 17), (17, 18), (18, 19), (19, 20)
]

def draw_landmarks_on_image(rgb_image, detection_result):
    hand_landmarks_list = detection_result.hand_landmarks
    annotated_image = np.copy(rgb_image)

    # Loop through the detected hands to visualize.
    for hand_landmarks in hand_landmarks_list:
        # hand_landmarks is a list of NormalizedLandmark objects
        h, w, c = annotated_image.shape

        # Draw connections
        for connection in HAND_CONNECTIONS:
            start_idx = connection[0]
            end_idx = connection[1]

            start_point = hand_landmarks[start_idx]
            end_point = hand_landmarks[end_idx]

            # Convert normalized coordinates to pixel coordinates
            start_x = int(start_point.x * w)
            start_y = int(start_point.y * h)
            end_x = int(end_point.x * w)
            end_y = int(end_point.y * h)

            cv2.line(annotated_image, (start_x, start_y), (end_x, end_y), (224, 224, 224), 2)

        # Draw landmarks
        for landmark in hand_landmarks:
             cx = int(landmark.x * w)
             cy = int(landmark.y * h)
             cv2.circle(annotated_image, (cx, cy), 4, (0, 0, 255), -1)
             cv2.circle(annotated_image, (cx, cy), 2, (255, 255, 255), -1)

    return annotated_image

def calculate_distance(p1, p2):
    return math.sqrt((p1.x - p2.x)**2 + (p1.y - p2.y)**2)

def camera_loop():
    global output_frame, last_trigger_time
    cap = cv2.VideoCapture(0)

    # Check if camera opened successfully
    if not cap.isOpened():
        print("Error: Could not open video device.")
        return

    pinch_start_time = 0

    while True:
        success, frame = cap.read()
        if not success:
            print("Ignoring empty camera frame.")
            time.sleep(0.1)
            continue

        # Convert BGR to RGB
        image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

        # Create MediaPipe Image
        mp_image = mp.Image(image_format=mp.ImageFormat.SRGB, data=image)

        # Detect hands
        detection_result = detector.detect(mp_image)

        # Draw landmarks manually on BGR image
        annotated_image = draw_landmarks_on_image(frame, detection_result)

        triggered_this_frame = False
        pinch_detected_now = False
        pinch_center = None

        if detection_result.hand_landmarks:
            for hand_landmarks in detection_result.hand_landmarks:
                # Get Thumb and Index tip coordinates (normalized 0-1)
                # Thumb tip is index 4, Index tip is index 8
                thumb_tip = hand_landmarks[4]
                index_tip = hand_landmarks[8]

                distance = calculate_distance(thumb_tip, index_tip)

                # Detect Pinch
                if distance < PINCH_THRESHOLD:
                    pinch_detected_now = True
                    h, w, c = annotated_image.shape
                    cx = int((thumb_tip.x + index_tip.x) / 2 * w)
                    cy = int((thumb_tip.y + index_tip.y) / 2 * h)
                    pinch_center = (cx, cy)
                    break # Process only one pinch

        if pinch_detected_now:
            if pinch_start_time == 0:
                pinch_start_time = time.time()

            elapsed = time.time() - pinch_start_time

            if elapsed >= TRIGGER_HOLD_TIME:
                current_time = time.time()
                if current_time - last_trigger_time > TRIGGER_COOLDOWN:
                    triggered_this_frame = True
                    last_trigger_time = current_time
                    # Visual Feedback: Green (Triggered)
                    cv2.circle(annotated_image, pinch_center, 20, (0, 255, 0), cv2.FILLED)
                else:
                     # Visual Feedback: Blue (Cooldown)
                     cv2.circle(annotated_image, pinch_center, 20, (255, 0, 0), cv2.FILLED)
            else:
                 # Visual Feedback: Yellow (Charging)
                 # Radius grows from 5 to 20
                 radius = int(5 + (elapsed / TRIGGER_HOLD_TIME) * 15)
                 cv2.circle(annotated_image, pinch_center, radius, (0, 255, 255), cv2.FILLED)
                 # Target size ring
                 cv2.circle(annotated_image, pinch_center, 20, (255, 255, 255), 2)
        else:
            # Reset pinch timer if pinch is lost
            pinch_start_time = 0

        # Send trigger in a separate thread to not block the video loop
        if triggered_this_frame:
            threading.Thread(target=send_trigger).start()

        with lock:
            output_frame = annotated_image.copy()

        # Sleep slightly to avoid hogging CPU if FPS is high (though camera usually limits it)
        time.sleep(0.01)

def send_trigger():
    print("Pinch detected! Sending trigger...")
    try:
        response = requests.post(JAVA_TRIGGER_URL)
        print(f"Trigger response: {response.status_code}")
    except Exception as e:
        print(f"Failed to send trigger: {e}")

def generate():
    global output_frame
    while True:
        with lock:
            if output_frame is None:
                continue
            (flag, encodedImage) = cv2.imencode(".jpg", output_frame)
            if not flag:
                continue
        yield(b'--frame\r\n' b'Content-Type: image/jpeg\r\n\r\n' +
              bytearray(encodedImage) + b'\r\n')
        time.sleep(0.03) # Limit stream FPS

@app.route("/video_feed")
def video_feed():
    return Response(generate(),
        mimetype = "multipart/x-mixed-replace; boundary=frame")

@app.route("/api/frame")
def get_frame():
    global output_frame
    with lock:
        if output_frame is None:
             return "No frame captured yet", 503
        (flag, encodedImage) = cv2.imencode(".jpg", output_frame)

    return Response(bytearray(encodedImage), mimetype="image/jpeg")

if __name__ == '__main__':
    # Start camera thread
    t = threading.Thread(target=camera_loop)
    t.daemon = True
    t.start()

    # Run Flask
    app.run(host="0.0.0.0", port=5000, debug=False, use_reloader=False)
