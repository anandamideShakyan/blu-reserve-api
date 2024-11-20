# Step 1: Use a base image with Java
FROM openjdk:17-jdk-slim

# Step 2: Set the working directory in the container
WORKDIR /app

# Step 3: Copy the JAR file from your target folder into the container
COPY target/ app.jar

# Step 4: Expose the application port (ensure this matches your Spring Boot app's port)
EXPOSE 8080

# Step 5: Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
