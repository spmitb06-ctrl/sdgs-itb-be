# Dockerfile

# Stage 1 - Build with Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copy only pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy the source code
COPY src ./src

# 3. Build the application
RUN mvn clean package -DskipTests -B

# 4. Copy built jar to predictable name
RUN cp target/*.jar app.jar

# Stage 2 - Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/app.jar ./app.jar

# JVM tuning / memory options
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
