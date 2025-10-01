# Dockerfile
# Stage 1 - build with Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# copy only pom and download dependencies (speeds up rebuilds)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# copy source and build
COPY src ./src
RUN mvn -B clean package -DskipTests

# copy built jar to a predictable name
RUN cp target/*.jar app.jar

# Stage 2 - runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy jar from build stage
COPY --from=build /app/app.jar ./app.jar

# JVM tuning / memory options (adjust as needed)
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
