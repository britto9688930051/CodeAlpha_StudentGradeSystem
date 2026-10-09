FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY src ./src
RUN javac -d out src/*.java

# Render sets the PORT environment variable; WebServer reads it.
CMD ["java", "-cp", "out", "WebServer"]
