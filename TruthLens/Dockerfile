# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the project files into the container
COPY src/ /app/src/
COPY web/ /app/web/
COPY data/ /app/data/

# Create output directory for compiled classes
RUN mkdir -p out

# Compile the Java source code
RUN find src -name "*.java" > sources.txt
RUN javac -d out @sources.txt

# Render will set the PORT environment variable
EXPOSE 8080

# Run the web server
CMD ["java", "-cp", "out", "WebMain"]
