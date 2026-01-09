# --- Phase de build ---
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /app
# Correction des chemins pour correspondre à ta structure
COPY ./.mvn/ .mvn/
COPY ./mvnw ./pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw package -DskipTests

# --- Phase d'exécution ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Installation de fswebcam
RUN apt-get update && apt-get install -y fswebcam && rm -rf /var/lib/apt/lists/*

# Copie du jar depuis le builder
COPY --from=builder /app/target/*.jar app.jar

# L'application tournera sur le port 8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]