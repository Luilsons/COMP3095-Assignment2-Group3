# Use an official OpenJDK runtime as the base image
FROM openjdk:22-jdk-slim

# Argument to specify the JAR file name
ARG JAR_FILE=build/libs/event-service-0.0.1-SNAPSHOT.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Expose the port your service runs on (e.g., 8084)
EXPOSE 8084

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]
