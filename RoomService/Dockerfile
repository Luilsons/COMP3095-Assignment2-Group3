# Use an official OpenJDK runtime as the base image
FROM openjdk:22-jdk-slim

# Argument to specify the JAR file name
#ARG JAR_FILE=build/libs/room-service-0.0.1-SNAPSHOT.jar

CMD ["./gradlew", "clean", "bootJar"]
COPY build/libs/*.jar app.jar

# Copy the JAR file into the container
#COPY ${JAR_FILE} app.jar

# Expose the port your service runs on (e.g., 8081)
EXPOSE 8081

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]

#FROM openjdk:11
#WORKDIR /my-project
#CMD ["./gradlew", "clean", "bootJar"]
#COPY build/libs/*.jar app.jar
#
##EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]
