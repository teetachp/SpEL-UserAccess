# --- Build Stage ---
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Use cached dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy rest of the project
COPY src ./src

# Run tests and build the package
RUN mvn clean verify

# --- Run Stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]