# --- Stage 1: Build the JAR ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Build the app (skipping tests to be faster)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the App ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copy the JAR file from the build stage
# Note: Ensure the name below matches your pom.xml artifactId
COPY --from=build /app/target/lnuais-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]