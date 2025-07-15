# Stage 1: Build the application using a Maven image with Java 21
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
# Use go-offline to leverage Docker's layer caching
RUN mvn -B dependency:go-offline

COPY src ./src

# Build the application JAR
RUN mvn -B clean package -DskipTests

# Stage 2: Create the final, lightweight image with Java 21 JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the application JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]