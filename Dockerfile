# Stage 1: Build the Spring Boot application
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only pom.xml first to download dependencies (speeds up builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the JAR and skip tests
RUN mvn clean package -DskipTests

# Copy the generated JAR to a generic name
RUN cp target/*.jar app.jar

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/app.jar app.jar

# Set environment variables for Spring Boot (Railway will inject them)
ENV JAVA_OPTS=""

# Expose default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
