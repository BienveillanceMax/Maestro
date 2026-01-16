# --- Phase de build ---
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /app
# Copie des fichiers de configuration Maven
COPY ./.mvn/ .mvn/
COPY ./mvnw ./pom.xml ./
RUN ./mvnw dependency:go-offline
# Copie des sources et build
COPY ./src ./src
RUN ./mvnw package -DskipTests

# --- Phase d'exécution ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Installation de :
# 1. fswebcam (pour la caméra)
# 2. libasound2 (OBLIGATOIRE pour le son en Java)
# 3. alsa-utils (Outils de diagnostic audio)
# 4. libasound2-plugins (Pour l'intégration PulseAudio)
RUN apt-get update && \
    apt-get install -y fswebcam libasound2 alsa-utils libasound2-plugins && \
    rm -rf /var/lib/apt/lists/*

# Copie du jar depuis le builder
COPY --from=builder /app/target/*.jar app.jar

# L'application tournera sur le port 8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]