# Use official Java runtime
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Compile Java code
RUN javac -cp ".:jsoup-1.21.2.jar" Main.java

# Expose port 8080 for Render
EXPOSE 8080

# Run the server
CMD ["java", "-cp", ".:jsoup-1.21.2.jar", "Main"]
