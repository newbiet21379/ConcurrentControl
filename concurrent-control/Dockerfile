# Stage 1: Gradle-based Build stage
FROM gradle:jdk21 AS build

# Copying build.gradle and source code to the container
COPY build.gradle /home/gradle/src/
COPY src /home/gradle/src/src/

# Package the application
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

# Stage 2: Java Runtime stage
FROM openjdk:21-jdk-slim

# Metadata indicating the maintainer of the image
LABEL maintainer="maintainer@example.com"

# Create a volume at /tmp directory
VOLUME /tmp

# Copy the packaged jar file from the build stage into our Java runtime image
COPY --from=build /home/gradle/src/build/libs/*.jar application.jar

# Expose the application on port 8080
EXPOSE 8080

# Set the startup command to run the jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom","-jar","/application.jar"]