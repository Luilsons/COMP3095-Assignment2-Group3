# Use an official OpenJDK runtime as the base image
FROM openjdk:22-jdk-slim

# Command to build the JAR file
CMD ["./gradlew", "clean", "bootJar"]

# Copy the built JAR file into the container
COPY build/libs/*.jar app.jar

# Expose the port your service runs on (8081)
EXPOSE 8081

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]
