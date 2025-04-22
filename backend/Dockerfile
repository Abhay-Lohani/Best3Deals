# Use an official OpenJDK runtime as a parent image
FROM amazoncorretto:17-al2023-jdk
# Set the working directory inside the container
WORKDIR /app
# Copy the compiled JAR file from your target directory into the container
COPY target/best3deals-0.0.1-SNAPSHOT.jar /app/app.jar
# Expose the port on which your Spring Boot application will run (adjust as needed)
EXPOSE 8080
# Define the command to run your Spring Boot application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]