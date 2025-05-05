# Use OpenJDK 17 slim image
FROM openjdk:17-slim

# Add the jar file to the container
COPY target/ratelimiter-0.0.1-SNAPSHOT.jar /usr/app/ratelimiter.jar

# Set the working directory
WORKDIR /usr/app

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "ratelimiter.jar"]
