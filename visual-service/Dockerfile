# On passe à Python 3.10-slim pour une meilleure compatibilité des wheels
FROM python:3.10-slim

WORKDIR /app

# Ajout de libsm6 et libxext6 qui manquent souvent pour le backend C++ de MediaPipe/OpenCV
RUN apt-get update && apt-get install -y \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*

# Mise à jour de pip pour éviter les problèmes d'installation de wheels
RUN pip install --upgrade pip

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY main.py .

EXPOSE 5000

CMD ["python", "main.py"]