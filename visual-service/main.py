import cv2
import mediapipe as mp
import time
import threading
import requests
import math
from flask import Flask, Response, jsonify

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
# URL of the Java service trigger
JAVA_TRIGGER_URL = "http://maestro-app:8080/api/trigger"

# --- MediaPipe Setup ---
mp_hands = mp.solutions.hands
mp_drawing = mp.solutions.drawing_utils

def calculate_distance(p1, p2):
    return math.sqrt((p1.x - p2.x)**2 + (p1.y - p2.y)**2)

def camera_loop():
    global output_frame, last_trigger_time
    cap = cv2.VideoCapture(0)

    # Check if camera opened successfully
    if not cap.isOpened():
        print("Error: Could not open video device.")
        return

    with mp_hands.Hands(
        min_detection_confidence=0.5,
        min_tracking_confidence=0.5) as hands:

        while True:
            success, frame = cap.read()
            if not success:
                print("Ignoring empty camera frame.")
                time.sleep(0.1)
                continue

            # Convert BGR to RGB
            image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            image.flags.writeable = False
            results = hands.process(image)
            image.flags.writeable = True
            image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

            triggered_this_frame = False

            if results.multi_hand_landmarks:
                for hand_landmarks in results.multi_hand_landmarks:
                    mp_drawing.draw_landmarks(
                        image,
                        hand_landmarks,
                        mp_hands.HAND_CONNECTIONS)

                    # Get Thumb and Index tip coordinates (normalized 0-1)
                    thumb_tip = hand_landmarks.landmark[mp_hands.HandLandmark.THUMB_TIP]
                    index_tip = hand_landmarks.landmark[mp_hands.HandLandmark.INDEX_FINGER_TIP]

                    distance = calculate_distance(thumb_tip, index_tip)

                    # Detect Pinch
                    if distance < PINCH_THRESHOLD:
                        # Draw a circle on the pinch point
                        h, w, c = image.shape
                        cx = int((thumb_tip.x + index_tip.x) / 2 * w)
                        cy = int((thumb_tip.y + index_tip.y) / 2 * h)
                        cv2.circle(image, (cx, cy), 15, (0, 255, 0), cv2.FILLED)

                        current_time = time.time()
                        if current_time - last_trigger_time > TRIGGER_COOLDOWN:
                            triggered_this_frame = True
                            last_trigger_time = current_time

            # Send trigger in a separate thread to not block the video loop
            if triggered_this_frame:
                threading.Thread(target=send_trigger).start()

            with lock:
                output_frame = image.copy()

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
